package net.runelite.client.plugins.hydra;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static net.runelite.client.plugins.raids.RaidRoom.Type.PUZZLE;

public class HydraOverlay extends Overlay{
    private static final Color COLOR_ICON_BACKGROUND = new Color(0, 0, 0, 128);
    private static final Color COLOR_ICON_BORDER = new Color(0, 0, 0, 255);
    private static final Color COLOR_ICON_BORDER_FILL = new Color(219, 175, 0, 255);
    private static final int OVERLAY_ICON_DISTANCE = 50;
    private static final int OVERLAY_ICON_MARGIN = 8;

    private Client client;
    private final HydraPlugin plugin;

    @Inject
    private SkillIconManager iconManager;
    private final PanelComponent panelComponent = new PanelComponent();
    @Inject
    public HydraOverlay(Client client, HydraPlugin plugin)
    {
        setPosition(OverlayPosition.BOTTOM_RIGHT);
        panelComponent.setOrientation(PanelComponent.Orientation.HORIZONTAL);
        this.client = client;
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        panelComponent.getChildren().clear();
        if(!plugin.hydraFound){
            return null;
        }
        if(!client.isInInstancedRegion()){
            return null;
        }

        Color prayColor = Color.GRAY;
        String pray = "";
        int prayC = 0;

        if(plugin.prayMage){
            pray = "Mage";
            prayC = plugin.mage;
            prayColor= Color.BLUE;
            panelComponent.getChildren().add(new ImageComponent(plugin.mageImage));
        }else if(plugin.prayRange){
            pray = "Range";
            prayC = plugin.range;
            prayColor = Color.GREEN;
            panelComponent.getChildren().add(new ImageComponent(plugin.rangeImage));
        }
/*
        Color color = Color.WHITE;

        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Hydra")
                .color(color)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left(pray)
                .right(""+prayC)
                .rightColor(color)
                .build());
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Pray " + pray)
                .color(prayColor)
                .build());
                */

        return panelComponent.render(graphics);
    }



    private void renderGraphicsObjects(Graphics2D graphics)
    {
        List<GraphicsObject> graphicsObjects = client.getGraphicsObjects();

        for (GraphicsObject graphicsObject : graphicsObjects)
        {
            LocalPoint lp = graphicsObject.getLocation();
            Polygon poly = Perspective.getCanvasTilePoly(client, lp);
            Polygon polyBox = Perspective.getCanvasTileAreaPoly(client,lp,3);

            if (poly != null)
            {
                OverlayUtil.renderPolygon(graphics, polyBox, Color.MAGENTA);
            }

        }
    }

}
