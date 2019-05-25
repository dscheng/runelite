package net.runelite.client.plugins.hydra;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

public class HydraSplats extends Overlay {
    private final Client client;
    private final HydraConfig config;
    private final HydraPlugin plugin;

    @Inject
    private HydraSplats(Client client, HydraConfig config, HydraPlugin plugin)
    {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        List<WorldPoint> points = plugin.splats;
        if(points == null){
            return null;
        }
        for (WorldPoint point : points)
        {
            if (point.getPlane() != client.getPlane())
            {
                continue;
            }

            drawTile(graphics, point);
        }

        return null;
    }

    private void drawTile(Graphics2D graphics, WorldPoint point)
    {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

        if (point.distanceTo(playerLocation) >= 32)
        {
            return;
        }

        LocalPoint lp = LocalPoint.fromWorld(client, point);
        if (lp == null)
        {
            return;
        }

        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        if (poly == null)
        {
            return;
        }

        OverlayUtil.renderPolygon(graphics, poly, Color.YELLOW);
    }
}
