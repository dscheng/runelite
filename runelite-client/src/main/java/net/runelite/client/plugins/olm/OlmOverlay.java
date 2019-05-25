package net.runelite.client.plugins.olm;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

public class OlmOverlay extends Overlay {

    private OlmPlugin plugin;
    private OlmConfig config;
    private Client client;
    @Inject
    public OlmOverlay(OlmPlugin plugin, Client client, OlmConfig config)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_MAP);
        setPriority(OverlayPriority.HIGH);
        this.plugin = plugin;
        this.client = client;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        showOlm(graphics);
        return null;
    }
    private void showOlm(Graphics2D graphics)
    {
        List<NPC> npcs = client.getNpcs();
        for (NPC npc : npcs)
        {
            if(npc == null){
                continue;
            }
            if(npc.getId() != plugin.ID){
               continue;
            }
            Color color = config.shadow();
            OverlayUtil.renderActorOverlay(graphics, npc, "", color);
        }
    }
}
