package net.runelite.client.plugins.hydra;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.Notifier;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

@PluginDescriptor(
        name = "[L] Hydra",
        description = "Helps you kill hydra",
        tags = {"hydra", "slayer", "pvm"},
        enabledByDefault =false
)
public class HydraPlugin extends Plugin {

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private InfoBoxManager infoBoxManager;

    @Inject
    private HydraOverlay overlay;

    @Inject
    private Notifier notifier;

    @Inject
    private HydraConfig config;

    @Inject
    private SkillIconManager iconManager;

    @Inject
    private ChatMessageManager chatMessageManager;
    boolean projectileFinished;
    boolean projectileStarted;
    boolean prayMage;
    boolean prayRange;
    int mage = 0;
    int total = 0;
    int range = 0;
    int lastAttack = 0;
    int swapStyle = 2;
    boolean done;
    NPC hydra = null;
    List<WorldPoint> splats;
    final WorldArea hydraArea = new WorldArea(11488,7583,30,30,0);
    Projectile lastProjectile = null;
    BufferedImage rangeImage= ImageUtil.getResourceStreamFromClass(getClass(), "ranged.png");
    BufferedImage mageImage= ImageUtil.getResourceStreamFromClass(getClass(), "magic.png");
    String test = "Unknown";
    boolean hydraFound;

    @Provides
    HydraConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(HydraConfig.class);
    }

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
        //overlayManager.add(splatMarkers);
        swapStyle=2;
        done = true;
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
       // overlayManager.remove(splatMarkers);
        mage =0;
        range =0;
        total=0;
    }

    @Subscribe
    public void onGameTick(GameTick tick){
        hydraFound = false;
        List<NPC> npcs = client.getNpcs();

        if(npcs != null){
            for(int i=0;i<npcs.size();i++){
                //System.out.println(npcs.get(i).getName());
                if(npcs.get(i).getName() != null) {
                    if (npcs.get(i).getName().equals("Alchemical Hydra")) {
                        hydra = npcs.get(i);
                        hydraFound = true;
                        break;
                    }
                }
            }
        }

        if(hydra != null){
            if(hydra.getId() == 8621 && done){

                if(lastAttack == 1662){
                    prayMage = true;
                    prayRange = false;
                    mage = 1;
                    range = 0;
                }
                if(lastAttack == 1663){
                    prayRange = true;
                    prayMage = false;
                    mage = 0;
                    range = 1;
                }
                done = false;
            }
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned spawned){

            hydra = spawned.getNpc();
            mage = 0;
            range = 0;
            hydraFound = true;
            swapStyle = 2;
            total = 0;
            lastAttack = 0;
            lastProjectile = null;
            done = true;
        }


    @Subscribe
    public void onNpcDespawned(NpcDespawned despawned){
            hydra = null;
            mage = 0;
            range = 0;
            hydraFound = false;
            swapStyle = 2;
            total = 0;
            lastAttack = 0;
            lastProjectile = null;
            done = true;
    }

    //1645 = hydra acid

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated graphic){
       /*
        GraphicsObject splat = graphic.getGraphicsObject();
        if(splat.getId() != 1645){
            return;
        }

        WorldPoint newSplat = new WorldPoint(splat.getLocation().getX(),splat.getLocation().getY(),client.getPlane());
        if(newSplat != null){
           splats.add(newSplat);
        }
        */

    }
    public void sendMsg(String msg){
        String chatMessage = new ChatMessageBuilder()
                .append(ChatColorType.NORMAL)
                .append(msg)
                .build();
        chatMessageManager.queue(QueuedMessage.builder()
                .type(ChatMessageType.CONSOLE)
                .runeLiteFormattedMessage(chatMessage)
                .build());
    }
    @Subscribe
    public void onProjectileMoved(ProjectileMoved pq) {
        //1662 = hydra range
        //1663 = hydra mage
        //8615 = hydra boss
        //11105,11615,0
        //11132,11640,0
        Projectile p = pq.getProjectile();
        if(config.debug()){
            sendMsg("Mage: " + mage);
            sendMsg("Range: " + range);
            sendMsg("prayMage: " + prayMage);
            sendMsg("prayRange: " + prayRange);
            sendMsg("swapStyle: " + swapStyle);
            sendMsg("_________________________________");
        }
        if(!client.isInInstancedRegion()){
            return;
        }
/*
        if(!hydraFound){

            return;
        }
        */
        if(hydra == null){
            return;
        }

        int lastPhaseId = 8621;
        if(hydra.getId() == lastPhaseId){
            swapStyle = 0;
            if(lastAttack == 1662){
                range = 1;
                mage = 0;
            }
            if(lastAttack == 1663){
                mage = 1;
                range = 0;
            }
        }else{
            swapStyle = 2;
        }
        Player player = client.getLocalPlayer();
        Projectile projectile = p;


        int cycle = p.getStartMovementCycle();
        int finishCycle = p.getEndCycle();
        int id = p.getId();
        int cyclesLeft = p.getRemainingCycles();
        if (id == 1662 || id == 1663) {

            // The event fires once before the projectile starts moving,
            // and we only want to check each projectile once
            if (client.getGameCycle() >= projectile.getStartMovementCycle()) {
                return;
            }

            if(lastProjectile != null) {
                if (projectile.getStartMovementCycle() == lastProjectile.getStartMovementCycle()) {
                    return;
                }
            }

            if (id == 1663) {
                range++;
                total++;
            }
            if (id == 1662) {
                mage++;
                total++;
            }
            lastAttack = id;
            if (range > swapStyle) {
                prayMage = true;
                prayRange = false;
                range = 0;
                mage = 0;
                test = "Pray Mage";
            }
            if (mage > swapStyle) {
                prayMage = false;
                prayRange = true;
                mage = 0;
                range = 0;
                test = "Pray Range";
            }
            if(total <3){
                if(id==1662){
                    test = "Pray Mage";
                    prayMage = true;
                    prayRange = false;
                }
                if(id==1663){
                    test = "Pray Range";
                    prayMage = false;
                    prayRange = true;
                }
            }

            //set count to a variable currently 3, if hydra id is last phase make it 1

            if(config.chat()) {
                String chatMessage = new ChatMessageBuilder()
                        .append(ChatColorType.NORMAL)
                        .append(test)
                        .build();
                chatMessageManager.queue(QueuedMessage.builder()
                        .type(ChatMessageType.CONSOLE)
                        .runeLiteFormattedMessage(chatMessage)
                        .build());
            }
            lastProjectile = projectile;
        }
    }

}
