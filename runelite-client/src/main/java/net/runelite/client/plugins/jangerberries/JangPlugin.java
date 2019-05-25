package net.runelite.client.plugins.jangerberries;

import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@PluginDescriptor(
        name = "[L] Jangerberries",
        description = "Noone wants to eat a fucking jangerberry",
        enabledByDefault = true
)
public class JangPlugin extends Plugin{

    private final List<MenuEntry> entries = new ArrayList<>();

    @Inject
    private Client client;
    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event)
    {
            entries.clear();
            if(!event.getTarget().endsWith("Jangerberries")) {
                return;
            }

            MenuEntry[] menuEntries = client.getMenuEntries();


            for (MenuEntry entry : menuEntries)
            {
                String option = entry.getOption();

                if (!option.startsWith("Eat"))
                {
                    entries.add(entry);
                }
            }
            if(entries != null)
            client.setMenuEntries(entries.toArray(new MenuEntry[entries.size()]));

    }



}
