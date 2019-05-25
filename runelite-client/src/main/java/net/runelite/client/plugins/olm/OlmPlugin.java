package net.runelite.client.plugins.olm;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.Date;

@PluginDescriptor(
        name = "Olm Highlight",
        description = "Shadow functionality for GPU",
        tags = {"cox,raids,shadow,olm"},
        enabledByDefault = false
)
public class OlmPlugin extends Plugin {
    public Date time;
    public boolean showShadow;
    public int ID = 7551;
    //public int ID = 2130;

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private OlmConfig config;

    @Provides
    OlmConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(OlmConfig.class);
    }
    @Inject
    private OlmOverlay overlay;

    @Override
    protected void startUp() {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(overlay);
    }

}
