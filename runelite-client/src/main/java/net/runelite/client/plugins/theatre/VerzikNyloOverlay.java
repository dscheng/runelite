package net.runelite.client.plugins.theatre;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

public class VerzikNyloOverlay extends Overlay {

    private final Client client;
    private final TheatrePlugin plugin;
    private final TheatreConfig config;

    @Inject
    private VerzikNyloOverlay(Client client, TheatrePlugin plugin, TheatreConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public Dimension render(Graphics2D graphics){

        if(plugin.isRunVerzik()){
            if(config.NyloTargetOverlay()){
                if(plugin.getCrabList().size() > 0){

                    for(NPC npc : plugin.getCrabList()){
                        if(npc.isDead()){
                            continue;
                        }
                        String renderText = "";
                        if(npc.getInteracting() != null){

                            renderText = npc.getInteracting().getName();
                            Point point = npc.getCanvasTextLocation(graphics, npc.getInteracting().getName(),0);


                            if(client.getLocalPlayer().getName().toLowerCase().equals(client.getLocalPlayer().getName().toLowerCase())){
                                point = npc.getCanvasTextLocation(graphics,client.getLocalPlayer().getName(),0);
                                renderText = config.nylomsg();

                            }
                            else{
                                point = npc.getCanvasTextLocation(graphics,client.getLocalPlayer().getName().toLowerCase(),0);
                                renderText = client.getLocalPlayer().getName().toLowerCase();
                            }
                            if(renderText.equals(config.nylomsg())){
                                renderTextLocation(graphics, renderText, 12, Font.BOLD, Color.RED, point);
                            } else{
                                renderTextLocation(graphics, renderText, 12, Font.BOLD, Color.GREEN, point);
                            }
                        }

                    }
                }

            }
        }

        return null;
    }

    private void renderTextLocation(Graphics2D graphics, String txtString, int fontSize, int fontStyle, Color fontColor, Point canvasPoint)
    {
        graphics.setFont(new Font("Arial", fontStyle, fontSize));
        if (canvasPoint != null)
        {
            final Point canvasCenterPoint = new Point(
                    canvasPoint.getX(),
                    canvasPoint.getY());
            final Point canvasCenterPoint_shadow = new Point(
                    canvasPoint.getX() + 1,
                    canvasPoint.getY() + 1) ;
            OverlayUtil.renderTextLocation(graphics, canvasCenterPoint_shadow, txtString, Color.BLACK);
            OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor);
        }
    }
}
