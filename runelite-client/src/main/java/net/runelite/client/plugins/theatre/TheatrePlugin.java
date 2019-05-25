/*
 * THIS SOFTWARE WRITTEN BY A KEYBOARD-WIELDING MONKEY BOI
 * No rights reserved. Use, redistribute, and modify at your own discretion,
 * and in accordance with Yagex and RuneLite guidelines.
 * However, aforementioned monkey would prefer if you don't sell this plugin for profit.
 * Good luck on your raids!
 */

package net.runelite.client.plugins.theatre;

import java.util.*;
import java.util.stream.Collectors;
import javax.inject.Inject;
import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import sun.rmi.server.LoaderHandler;

@PluginDescriptor(
        name = "[L] Theatre",
        description = "All-in-one plugin for Theatre of Blood",
        tags = {"ToB"},
        enabledByDefault = false
)

public class TheatrePlugin extends Plugin {
    private static final int GRAPHICSOBJECT_ID_MAIDEN = 1579;
    private static final int NPCID_NYLOCAS_PILLAR = 8358;
    private static final int GROUNDOBJECT_ID_BLACKMAZE = 33034;
    private static final int GROUNDOBJECT_ID_REDMAZE = 33035;
    private static final int GROUNDOBJECT_ID_EXHUMED = 32743;
    private static final int ANIMATION_ID_XARPUS = 8059;
    private static final int GRAPHICSOBJECT_ID_YELLOW = 1595;
    private static final int PROJECTILE_ID_P2RANGE = 1583;
    private static final int PROJECTILE_ID_YELLOW = 1596;
    private static final int ANIMATION_ID_P3_WEB = 8127;
    private static final int ANIMATION_ID_P3_YELLOW = 8126;
    private static final int ANIMATION_ID_P3_MELEE = 8123;
    private static final int ANIMATION_ID_P3_MAGE = 8124;
    private static final int ANIMATION_ID_P3_RANGE = 8125;
    private static final int VERZIK_ID_P3 = NpcID.VERZIK_VITUR_8374;
    private static final int NPC_ID_TORNADO = 8386;
    private static final int PROJECTILE_ID_P3_GREEN = 1598;


    @Getter
    int exhumecount;

    private boolean bloatFlag = false;

    @Getter
    int bloatTimer = 0;

    int bloatFeetTimer = 0;

    @Getter
    private Set<WorldPoint> bloatTiles = new HashSet<>();

    @Getter
    private Set<WorldPoint> temp = new HashSet<>();

    @Getter
    private Set<WorldPoint> temp2 = new HashSet<>();

    @Getter
    private Set<WorldPoint> localTemp = new HashSet<>();

    @Getter
    private boolean tempFlag = false;

    @Getter
    private boolean temp2Flag = false;

    //@Getter
    //private List<WorldPoint> bloatTiles = new ArrayList<>();


    @Getter(AccessLevel.PACKAGE)
    private boolean runMaiden;

    @Getter(AccessLevel.PACKAGE)
    private List<WorldPoint> Maiden_BloodSpatters = new ArrayList<>();

    private List<NPC> Maiden_Spawns = new ArrayList<>();

    @Getter(AccessLevel.PACKAGE)
    private List<WorldPoint> Maiden_SpawnLocations = new ArrayList<>();

    @Getter(AccessLevel.PACKAGE)
    private List<WorldPoint> Maiden_SpawnLocations2 = new ArrayList<>();


    @Getter(AccessLevel.PACKAGE)
    private boolean runBloat;

    @Getter(AccessLevel.PACKAGE)
    private NPC Bloat_NPC;

    private int Bloat_downCount;

    @Getter(AccessLevel.PACKAGE)
    private Integer Bloat_State;


    @Getter(AccessLevel.PACKAGE)
    private boolean runNylocas;

    @Getter(AccessLevel.PACKAGE)
    private Map<NPC, Integer> Nylocas_Pillars = new HashMap<>();

    @Getter(AccessLevel.PACKAGE)
    private Map<NPC, Integer> Nylocas_Map = new HashMap<>();


    @Getter(AccessLevel.PACKAGE)
    private boolean runSotetseg;

    @Getter(AccessLevel.PACKAGE)
    private final Map<GroundObject, Tile> RedTiles = new LinkedHashMap<>();

    @Getter(AccessLevel.PACKAGE)
    private List<WorldPoint> RedTilesOverworld = new ArrayList<>();

    private List<WorldPoint> BlackTilesOverworld = new ArrayList<>();

    private List<WorldPoint> BlackTilesUnderworld = new ArrayList<>();

    private List<WorldPoint> RedTilesUnderworld = new ArrayList<>();

    private List<Point> GridPath = new ArrayList<>();

    @Getter(AccessLevel.PACKAGE)
    private boolean runXarpus;

    private int Xarpus_previousAnimation;

    @Getter(AccessLevel.PACKAGE)
    private boolean Xarpus_Stare;

    @Getter(AccessLevel.PACKAGE)
    private final Map<GroundObject, Integer> Xarpus_Exhumeds = new HashMap<>();

    @Getter(AccessLevel.PACKAGE)
    private final Map<GroundObject, Integer> xarpusExhumedsTimer = new HashMap<>();

    @Getter(AccessLevel.PACKAGE)
    private int Xarpus_TicksUntilShoot = 9;

    @Getter(AccessLevel.PACKAGE)
    private NPC Xarpus_NPC;

    @Getter(AccessLevel.PACKAGE)
    private boolean runVerzik;

    @Getter(AccessLevel.PACKAGE)
    private final Map<Projectile, WorldPoint> Verzik_RangeProjectiles = new HashMap<>();

    @Getter(AccessLevel.PACKAGE)
    private int P3_TicksUntilAttack = -1;

    @Getter(AccessLevel.PACKAGE)
    private Projectile Verzik_YellowBall;

    @Getter(AccessLevel.PACKAGE)
    private List<WorldPoint> Verzik_YellowTiles = new ArrayList<>();

    @Getter(AccessLevel.PACKAGE)
    private NPC Verzik_NPC;

    @Getter(AccessLevel.PACKAGE)
    private List<NPC> tornadoList;

    @Getter(AccessLevel.PACKAGE)
    private List<NPC> crabList;

    @Getter
    private int redCrabsTimer;

    private int P3_attacksLeft;

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private TheatreOverlay overlay;

    @Inject
    private TheatreConfig config;

    @Inject
    private TheatreXarpusOverlay xarpusOverlay;

    @Inject
    private ChatMessageManager chatMessageManager;

    @Inject
    private VerzikNyloOverlay verzikNyloOverlay;

    @Inject
    private BloatTimerOverlay bloatTimerOverlay;
    @Provides
    TheatreConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(TheatreConfig.class);
    }

    @Override
    protected void startUp() {
        overlayManager.add(overlay);
        overlayManager.add(xarpusOverlay);
        overlayManager.add(verzikNyloOverlay);
        overlayManager.add(bloatTimerOverlay);
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(overlay);
        overlayManager.remove(xarpusOverlay);
        overlayManager.remove(verzikNyloOverlay);
        overlayManager.remove(bloatTimerOverlay);
    }



    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event) {
        /*
        int THEATRE_OF_BLOOD_REGION = 12867;


        final ItemContainer container;
        switch (event.getGroupId()) {

            case (WidgetID.THEATRE_OF_BLOOD_GROUP_ID):
                int region = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID();
                if (region != THEATRE_OF_BLOOD_REGION) {
                    return;
                }
                container = client.getItemContainer(InventoryID.THEATRE_OF_BLOOD_CHEST);
                break;
            default:
                return;
        }
        if (container == null)
        {
            return;
        }

        // Convert container items to array of ItemStack
        final Collection<ItemStack> items = Arrays.stream(container.getItems())
                .filter(item -> item.getId() > 0)
                .map(item -> new ItemStack(item.getId(), item.getQuantity(), client.getLocalPlayer().getLocalLocation()))
                .collect(Collectors.toList());

        if (!items.isEmpty()){
            if (client.getLocalPlayer().getName().toLowerCase().equals("mcneill") ||
                    client.getLocalPlayer().getName().toLowerCase().equals("mk6")) {
                String chatMessage = new ChatMessageBuilder()
                        .append(ChatColorType.HIGHLIGHT)
                        .append("You have a funny feeling like you would have been followed...")
                        .build();
                chatMessageManager.queue(QueuedMessage.builder()
                        .type(ChatMessageType.CONSOLE)
                        .runeLiteFormattedMessage(chatMessage)
                        .build());
            }
        }
*/
    }

    @Subscribe
    public void onNpcLootReceived(final NpcLootReceived npcLootReceived)
    {
/*
        final NPC npc = npcLootReceived.getNpc();
        final String name = npc.getName();
        if (npc.getName().toLowerCase().contains("blood")) {
            if (client.getLocalPlayer().getName().toLowerCase().equals("mcneill") ||
                    client.getLocalPlayer().getName().toLowerCase().equals("mk6")) {
                String chatMessage = new ChatMessageBuilder()
                        .append(ChatColorType.HIGHLIGHT)
                        .append("You have a funny feeling like you would have been followed...")
                        .build();
                chatMessageManager.queue(QueuedMessage.builder()
                        .type(ChatMessageType.CONSOLE)
                        .runeLiteFormattedMessage(chatMessage)
                        .build());
            }
        }
        */
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {

        NPC npc = npcSpawned.getNpc();
        switch (npc.getId()) {
            case NpcID.THE_MAIDEN_OF_SUGADINTI:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_8361:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_8362:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_8363:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_8364:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_8365:
                runMaiden = true;
                break;
            case NpcID.BLOOD_SPAWN:
                Maiden_Spawns.add(npc);
                break;
            case NpcID.PESTILENT_BLOAT:
                runBloat = true;
                Bloat_NPC = npc;
                bloatTimer = 0;
                bloatFlag = false;
                break;
            case NPCID_NYLOCAS_PILLAR:
                runNylocas = true;
                if (!Nylocas_Pillars.keySet().contains(npc)) {
                    Nylocas_Pillars.put(npc, 100);
                }
                break;
            case 8342:
            case 8343:
            case 8344:
            case 8345:
            case 8346:
            case 8347:
            case 8348:
            case 8349:
            case 8350:
            case 8351:
            case 8352:
            case 8353:
                if (runNylocas) {
                    Nylocas_Map.put(npc, 52);
                }
                break;
            case NpcID.SOTETSEG:
            case NpcID.SOTETSEG_8388:
                runSotetseg = true;
                RedTiles.clear();
                break;
            case NpcID.XARPUS:
            case NpcID.XARPUS_8339:
            case NpcID.XARPUS_8340:
            case NpcID.XARPUS_8341:
                runXarpus = true;
                exhumecount = 25;
                Xarpus_NPC = npc;
                Xarpus_Stare = false;
                Xarpus_TicksUntilShoot = 9;
                Xarpus_previousAnimation = -1;
                break;
            case NpcID.VERZIK_VITUR_8369:
            case NpcID.VERZIK_VITUR_8370:
            case NpcID.VERZIK_VITUR_8371:
            case NpcID.VERZIK_VITUR_8372:
            case NpcID.VERZIK_VITUR_8373:
            case NpcID.VERZIK_VITUR_8374:
            case NpcID.VERZIK_VITUR_8375:
                P3_TicksUntilAttack = -1;
                P3_attacksLeft = 9;
                redCrabsTimer = 13;
                Verzik_NPC = npc;
                runVerzik = true;
                tornadoList = new ArrayList<>();
                crabList = new ArrayList<>();
                break;
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        NPC npc = npcDespawned.getNpc();
        switch (npc.getId()) {
            case NpcID.THE_MAIDEN_OF_SUGADINTI:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_8361:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_8362:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_8363:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_8364:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_8365:
                runMaiden = false;
                Maiden_Spawns.clear();
                break;
            case NpcID.BLOOD_SPAWN:
                Maiden_Spawns.remove(npc);
                break;
            case NpcID.PESTILENT_BLOAT:
                runBloat = false;
                Bloat_NPC = null;
                bloatTimer = 0;
                break;
            case NPCID_NYLOCAS_PILLAR:
                if (Nylocas_Pillars.keySet().contains(npc)) {
                    Nylocas_Pillars.remove(npc);
                }
                break;
            case 8342:
            case 8343:
            case 8344:
            case 8345:
            case 8346:
            case 8347:
            case 8348:
            case 8349:
            case 8350:
            case 8351:
            case 8352:
            case 8353:
                if (Nylocas_Map.keySet().contains(npc)) {
                    Nylocas_Map.remove(npc);
                }
                break;
            case NpcID.SOTETSEG:
            case NpcID.SOTETSEG_8388:
                RedTiles.clear();
                if (client.getPlane() != 3) {
                    runSotetseg = false;
                }
                break;
            case NpcID.XARPUS:
            case NpcID.XARPUS_8339:
            case NpcID.XARPUS_8340:
            case NpcID.XARPUS_8341:
                runXarpus = false;
                Xarpus_NPC = null;
                Xarpus_Stare = false;
                Xarpus_TicksUntilShoot = 9;
                Xarpus_previousAnimation = -1;
                Xarpus_Exhumeds.clear();
                exhumecount = 0;
                break;
            case NpcID.VERZIK_VITUR_8369:
            case NpcID.VERZIK_VITUR_8370:
            case NpcID.VERZIK_VITUR_8371:
            case NpcID.VERZIK_VITUR_8372:
            case NpcID.VERZIK_VITUR_8373:
            case NpcID.VERZIK_VITUR_8374:
            case NpcID.VERZIK_VITUR_8375:
                runVerzik = false;
                redCrabsTimer = 0;
                Verzik_NPC = null;
                break;
        }

    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event) {
        if (runSotetseg) {
            GroundObject o = event.getGroundObject();
            if (o.getId() == GROUNDOBJECT_ID_BLACKMAZE) {
                Tile t = event.getTile();
                WorldPoint p = t.getWorldLocation();
                if (t.getPlane() == 0) {
                    if (!BlackTilesOverworld.contains(p))
                        BlackTilesOverworld.add(p);
                } else {
                    if (!BlackTilesUnderworld.contains(p))
                        BlackTilesUnderworld.add(p);
                }
            }

            if (o.getId() == GROUNDOBJECT_ID_REDMAZE) {
                Tile t = event.getTile();
                WorldPoint p = t.getWorldLocation();
                if (p.getPlane() == 0) {
                    if (!RedTiles.containsValue(t)) {
                        RedTiles.put(o, t);
                    }
                } else {
                    if (!RedTilesUnderworld.contains(p))
                        RedTilesUnderworld.add(p);
                }
            }
        }

        if (runXarpus) {
            GroundObject o = event.getGroundObject();
            if (o.getId() == GROUNDOBJECT_ID_EXHUMED) {

                xarpusExhumedsTimer.put(o, 18);

                    Xarpus_Exhumeds.put(o, 18);

            }

        }
    }

    @Subscribe
    public void onProjectileMoved(ProjectileMoved event)
    {
        if (runVerzik)
        {
            Projectile projectile = event.getProjectile();
            if (projectile.getId() == PROJECTILE_ID_P2RANGE)
            {
                WorldPoint p = WorldPoint.fromLocal(client,event.getPosition());
                Verzik_RangeProjectiles.put(projectile, p);
            }
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event){
        if(runVerzik){
            if(event.getActor().getAnimation() == 8117){
                redCrabsTimer = 11;
            }
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event){
        if(runBloat){

            if(client.getVar(Varbits.BLOAT_DOOR) == 1){
                if(!bloatFlag){
                    bloatTimer = 0;
                    bloatFlag = true;
                }
            }
        }
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event){
        System.out.println("Spawned");
        GraphicsObject obj = event.getGraphicsObject();
        if (obj.getId() == 1570 || obj.getId() == 1571 || obj.getId() == 1572 || obj.getId() == 1573 || obj.getId() == 1574 || obj.getId() == 1575 || obj.getId() == 1576) {
            WorldPoint p = WorldPoint.fromLocal(client, obj.getLocation());
            if(temp.size() == 0){

            } else {

            }
        }
    }


    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (runMaiden)
        {
            Maiden_BloodSpatters.clear();
            for (GraphicsObject o : client.getGraphicsObjects())
            {
                if (o.getId() == GRAPHICSOBJECT_ID_MAIDEN)
                {
                    Maiden_BloodSpatters.add(WorldPoint.fromLocal(client, o.getLocation()));
                }
            }

            Maiden_SpawnLocations2.clear();
            Maiden_SpawnLocations2.addAll(Maiden_SpawnLocations);
            Maiden_SpawnLocations.clear();
            for (NPC spawn : Maiden_Spawns)
            {
                Maiden_SpawnLocations.add(spawn.getWorldLocation());
            }
        }

        if (runBloat)
        {

            localTemp.clear();

            //System.out.println("Temp flag" + tempFlag);
            //System.out.println("Temp2 flag" + temp2Flag);



                    for (GraphicsObject obj : client.getGraphicsObjects()) {
                        if (obj.getId() == 1570 || obj.getId() == 1571 || obj.getId() == 1572 || obj.getId() == 1573 || obj.getId() == 1574 || obj.getId() == 1575 || obj.getId() == 1576) {
                            WorldPoint p = WorldPoint.fromLocal(client, obj.getLocation());
                            //Already have some feet in temp Set
                            if(temp.size() > 0){
                                    //System.out.println("temp size > 0, tempflag set false, tempflag2 set true");
                                    tempFlag = false;
                                    temp2Flag = true;
                            } else {
                                //System.out.println("temp size 0, tempflag set true, tempflag2 set false");
                                tempFlag = true;
                                temp2Flag = false;

                            }
                            localTemp.add(p);
                        }
                    }

                        if(tempFlag){
                            temp2.clear();
                            temp2Flag = false;
                            temp.addAll(localTemp);



                           //System.out.println("temp2 cleared, temp2flag set false, added to temp set");
                        } else if (temp2Flag) {
                            temp.clear();
                            tempFlag = false;
                           temp2.addAll(localTemp);
                            //System.out.println("temp cleared, tempflag set false, added to temp2 set");

                    }

            Bloat_downCount++;

            if (Bloat_NPC.getAnimation() == -1) //1 = up; 2 = down; 3 = warn;
            {
                bloatTimer++;
                Bloat_downCount = 0;
                if (Bloat_NPC.getHealth() == 0)
                {
                    Bloat_State = 2;
                }
                else
                    Bloat_State = 1;
            }
            else
            {
                if (25<Bloat_downCount && Bloat_downCount<35)
                {
                    Bloat_State = 3;
                }
                else if (Bloat_downCount < 26)
                {
                    tempFlag = false;
                    temp2Flag = false;
                    temp2.clear();
                    temp.clear();
                    Bloat_State = 2;
                    bloatTimer = 0;
                }
                else if (Bloat_NPC.getModelHeight() == 568)
                {
                    tempFlag = false;
                    temp2Flag = false;
                    temp2.clear();
                    temp.clear();
                    Bloat_State = 2;
                }
                else
                    Bloat_State = 1;


            }
        }

        if (runNylocas)
        {
            for (Iterator<NPC> it = Nylocas_Map.keySet().iterator(); it.hasNext();)
            {
                NPC npc = it.next();
                int ticksLeft = Nylocas_Map.get(npc);

                if (ticksLeft < 0)
                {
                    it.remove();
                    continue;
                }
                Nylocas_Map.replace(npc, ticksLeft - 1);
            }

            for (NPC pillar : Nylocas_Pillars.keySet())
            {
                int healthPercent = pillar.getHealthRatio();
                if (healthPercent > -1)
                {
                    Nylocas_Pillars.replace(pillar, healthPercent);
                }
            }
            for (NPC npc : client.getNpcs())
            {
                if (npc.getId() == 8358)
                {
                    runNylocas = true;
                    break;
                }
                runNylocas = false;
            }
        }

        if (runSotetseg)
        {
            boolean sotetsegFighting = false;
            for (NPC npc : client.getNpcs())
            {
                if (npc.getId() == NpcID.SOTETSEG_8388)
                {
                    BlackTilesUnderworld.clear();
                    BlackTilesOverworld.clear();
                    RedTilesOverworld.clear();
                    RedTilesUnderworld.clear();
                    GridPath.clear();
                    sotetsegFighting = true;
                    RedTiles.clear();
                    break;
                }
            }

            if (!sotetsegFighting)
            {
                if (!BlackTilesUnderworld.isEmpty() && !RedTilesUnderworld.isEmpty() && GridPath.isEmpty())
                {
                    int minX = 99999;
                    int minY = 99999;
                    for (WorldPoint p : BlackTilesUnderworld)
                    {
                        int x = p.getX();
                        int y = p.getY();
                        if (x < minX)
                        {
                            minX = x;
                        }
                        if (y < minY)
                        {
                            minY = y;
                        }
                    }



                    boolean messageSent = false;
                    for (WorldPoint p : RedTilesUnderworld)
                    {
                        WorldPoint pN = new WorldPoint(p.getX(), p.getY() + 1, p.getPlane());
                        WorldPoint pS = new WorldPoint(p.getX(), p.getY() - 1, p.getPlane());
                        WorldPoint pE = new WorldPoint(p.getX() + 1, p.getY(), p.getPlane());
                        WorldPoint pW = new WorldPoint(p.getX() - 1, p.getY(), p.getPlane());

                        if ( !(     (RedTilesUnderworld.contains(pN) && RedTilesUnderworld.contains(pS)) ||
                                (RedTilesUnderworld.contains(pE) && RedTilesUnderworld.contains(pW))        )   )
                        {
                            GridPath.add(new Point(p.getX() - minX, p.getY() - minY));
                            if (!messageSent)
                            {
                                //client.addChatMessage(ChatMessageType.SERVER, "", "Maze path acquired.", null);
                                messageSent = true;
                            }
                        }

                    }
                }

                if (!BlackTilesOverworld.isEmpty() && !GridPath.isEmpty() && RedTilesOverworld.isEmpty())
                {
                    int minX = 99999;
                    int minY = 99999;
                    for (WorldPoint p : BlackTilesOverworld)
                    {
                        int x = p.getX();
                        int y = p.getY();
                        if (x < minX)
                        {
                            minX = x;
                        }
                        if (y < minY)
                        {
                            minY = y;
                        }
                    }
                    for (Point p : GridPath)
                    {
                        RedTilesOverworld.add(new WorldPoint(minX + p.getX(), minY + p.getY(), 0));
                    }
                }
            }
        }

        if (runXarpus)
        {
            int size = xarpusExhumedsTimer.size();
            for (Map.Entry<GroundObject, Integer> exhumes : xarpusExhumedsTimer.entrySet()) {
                if (size > 0) {
                    exhumes.setValue(exhumes.getValue() - 1);
                }

            }
            for (Iterator<GroundObject> it = Xarpus_Exhumeds.keySet().iterator(); it.hasNext();)
            {
                GroundObject key = it.next();
                Xarpus_Exhumeds.replace(key, Xarpus_Exhumeds.get(key) - 1);
                if (Xarpus_Exhumeds.get(key) < 0)
                {
                    it.remove();
                    exhumecount--;
                }
            }
            if((Xarpus_NPC.getComposition().getOverheadIcon() != null ) )
            {
                Xarpus_Stare = true;
            }
            if (Xarpus_Stare) {
                //dont hit xarpus if it looking at u
            }
           else if (Xarpus_NPC.getId() == NpcID.XARPUS_8340)
            {
                Xarpus_TicksUntilShoot--;
                //if (Xarpus_NPC.getAnimation() == ANIMATION_ID_XARPUS && Xarpus_previousAnimation != ANIMATION_ID_XARPUS)
                //{
                    //Xarpus_TicksUntilShoot = 3;
                //}
                //Xarpus_previousAnimation = Xarpus_NPC.getAnimation();
            }

        }

        if (runVerzik)
        {
        crabList.clear();
            for(NPC npc : client.getNpcs()){

                if(npc.getName().toLowerCase().contains("nylo")){
                        crabList.add(npc);
                }
            }

           if(Verzik_NPC.getAnimation() == 8117){
               redCrabsTimer = redCrabsTimer -1;
           }
            if (!Verzik_RangeProjectiles.isEmpty())
            {
                for (Iterator<Projectile> it = Verzik_RangeProjectiles.keySet().iterator(); it.hasNext();)
                {
                    Projectile projectile = it.next();
                    if (projectile.getRemainingCycles() < 1)
                    {
                        it.remove();
                    }
                }
            }

            Verzik_YellowBall = null;
            Verzik_YellowTiles.clear();

            for (Projectile projectile : client.getProjectiles())
            {
                if (projectile.getId() == PROJECTILE_ID_YELLOW)
                {
                    Verzik_YellowBall = projectile;
                    break;
                }
            }
            for (GraphicsObject o : client.getGraphicsObjects())
            {
                if (o.getId() == GRAPHICSOBJECT_ID_YELLOW)
                {

                    Verzik_YellowTiles.add(WorldPoint.fromLocal(client, o.getLocation()));
                }
            }

            if (Verzik_NPC.getId() == VERZIK_ID_P3) {
                boolean tornadosActive = false;
                for (NPC npc : client.getNpcs())
                {
                    if (npc.getId() == NPC_ID_TORNADO)
                    {
                        tornadoList.add(npc);
                        tornadosActive = true;
                        break;
                    }
                }

                boolean isGreenBall = false;
                for (Projectile projectile : client.getProjectiles())
                {
                    if (projectile.getId() == PROJECTILE_ID_P3_GREEN) {
                        isGreenBall = projectile.getRemainingCycles() > 210;
                        break;
                    }
                }

                P3_TicksUntilAttack--;

                switch (Verzik_NPC.getAnimation()) {
                    case ANIMATION_ID_P3_MAGE:
                        if (P3_TicksUntilAttack < 2) {
                            P3_attacksLeft--;
                            if (tornadosActive) {
                                P3_TicksUntilAttack = 5;
                            } else {
                                P3_TicksUntilAttack = 7;
                            }
                            if (P3_attacksLeft < 1) {
                                P3_TicksUntilAttack = 24;
                            }
                        }
                        break;
                    case ANIMATION_ID_P3_RANGE:
                        if (P3_TicksUntilAttack < 2) {
                            P3_attacksLeft--;
                            if (tornadosActive) {
                                P3_TicksUntilAttack = 5;
                            } else {
                                P3_TicksUntilAttack = 7;
                            }
                            if (P3_attacksLeft < 1) {
                                P3_TicksUntilAttack = 30;
                            }
                            if (isGreenBall) {
                                P3_TicksUntilAttack = 12;
                            }
                        }
                        break;
                    case ANIMATION_ID_P3_MELEE:
                        if (P3_TicksUntilAttack < 2) {
                            P3_attacksLeft--;
                            if (tornadosActive) {
                                P3_TicksUntilAttack = 5;
                            } else {
                                P3_TicksUntilAttack = 7;
                            }
                            if (P3_attacksLeft < 1) {
                                P3_TicksUntilAttack = 24;
                            }
                        }
                        break;
                    case ANIMATION_ID_P3_WEB:
                        P3_attacksLeft = 4;
                        P3_TicksUntilAttack = 11; //
                        break;
                    case ANIMATION_ID_P3_YELLOW:
                        P3_attacksLeft = 14;
                        P3_TicksUntilAttack = 11;
                        break;
                }
            }

        }


    }

}