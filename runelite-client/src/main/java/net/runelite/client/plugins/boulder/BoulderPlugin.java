package net.runelite.client.plugins.boulder;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Slf4j
@PluginDescriptor(
        name = "Raid Shortcuts",
        description = "Highlights Raid Shortcuts",
        tags = {"boulder", "cox", "raids", "highlight"},
        enabledByDefault = false
)
public class BoulderPlugin extends Plugin {
    //boulder id = 29740
    //rocks id = 29738
    //tree id = 29736
@Inject
private Client client;
    @Getter(AccessLevel.PACKAGE)
    private final List<TileObject> objects = new ArrayList<>();
    public boolean inRaid;
    @Inject
    private ConfigManager configManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private BoulderOverlay overlay;

    @Inject
    private KeyManager keyManager;

    @Provides
    BoulderConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(BoulderConfig.class);
    }

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
        inRaid = false;
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        inRaid = false;
    }
    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        final WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, event.getGameObject().getLocalLocation());

        if (worldPoint == null)
        {
            return;
        }
        if(event.getGameObject().getId() == 29740 || event.getGameObject().getId() == 29736 || event.getGameObject().getId() == 29738 ){
            objects.add(event.getGameObject());
        }


    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event)
    {
        objects.remove(event.getGameObject());
    }

    @Subscribe
    public void onGameTick(GameTick tick){
        if(objects == null){
            return;
        }
        Iterator<TileObject> it = objects.iterator();
        while(it.hasNext()){
            TileObject o = it.next();
            if(o.getCanvasLocation() == null){
                it.remove();
            }
        }
    }
    @Subscribe
    public void onVarbitChanged(VarbitChanged bit){
        if(client.getVar(Varbits.IN_RAID) == 1){
            inRaid = true;
        }else{
            objects.clear();
        }
    }
}
