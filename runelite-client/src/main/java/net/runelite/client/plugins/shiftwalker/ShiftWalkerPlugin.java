package net.runelite.client.plugins.shiftwalker;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.FocusChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

@PluginDescriptor(
        name = "[L] Shift Click Walk Under",
        description = "Use Shift to toggle the Walk Here menu option. While pressed you will Walk rather than interact with objects.",
        tags = {"npcs", "items", "objects"},
        enabledByDefault = false
)
public class ShiftWalkerPlugin extends Plugin {
    private static final String WALK_HERE = "WALK HERE";
    private static final String CANCEL = "CANCEL";
    @Inject
    private Client client;
    @Inject
    private ShiftWalkerConfig config;
    @Inject
    private ShiftWalkerInputListener inputListener;
    @Inject
    private ConfigManager configManager;
    @Inject
    private KeyManager keyManager;
    private boolean hotKeyPressed = false;

    public ShiftWalkerPlugin() {
    }

    @Provides
    ShiftWalkerConfig provideConfig(ConfigManager configManager) {
        return (ShiftWalkerConfig)configManager.getConfig(ShiftWalkerConfig.class);
    }

    public void startUp() {
        this.keyManager.registerKeyListener(this.inputListener);
    }

    public void shutDown() {
        this.keyManager.unregisterKeyListener(this.inputListener);
    }

    @Subscribe
    public void onFocusChanged(FocusChanged event) {
        if (!event.isFocused()) {
            this.hotKeyPressed = false;
        }

    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if (this.client.getGameState() == GameState.LOGGED_IN && this.hotKeyPressed) {
            String pOptionToReplace = Text.removeTags(event.getOption()).toUpperCase();
            if (!pOptionToReplace.equals("CANCEL") && !pOptionToReplace.equals("WALK HERE")) {
                String target = Text.removeTags(event.getTarget().toUpperCase());
                if (this.config.shiftWalkEverything()) {
                    this.stripEntries();
                } else if (this.config.shiftWalkBoxTraps() && ShiftWalkerGroups.BOX_TRAP_TARGETS.contains(target) && ShiftWalkerGroups.BOX_TRAP_KEYWORDS.contains(pOptionToReplace)) {
                    this.stripEntries();
                } else if (this.config.shiftWalkAttackOption() && ShiftWalkerGroups.ATTACK_OPTIONS_KEYWORDS.contains(pOptionToReplace)) {
                    this.stripEntries();
                }

            }
        }
    }

    private void stripEntries() {
        MenuEntry walkkHereEntry = null;
        MenuEntry[] newEntries = this.client.getMenuEntries();
        int var3 = newEntries.length;
        int var4 = 0;

        while(var4 < var3) {
            MenuEntry entry = newEntries[var4];
            String var6 = entry.getOption();
            byte var7 = -1;
            switch(var6.hashCode()) {
                case -918617145:
                    if (var6.equals("Walk here")) {
                        var7 = 0;
                    }
            }

            switch(var7) {
                case 0:
                    walkkHereEntry = entry;
                default:
                    ++var4;
            }
        }

        if (walkkHereEntry != null) {
            newEntries = new MenuEntry[]{walkkHereEntry};
            this.client.setMenuEntries(newEntries);
        }

    }

    private void swap(String pOptionToReplace) {
        MenuEntry[] entries = this.client.getMenuEntries();
        Integer walkHereEntry = this.searchIndex(entries, "WALK HERE");
        Integer entryToReplace = this.searchIndex(entries, pOptionToReplace);
        if (walkHereEntry != null && entryToReplace != null) {
            MenuEntry walkHereMenuEntry = entries[walkHereEntry];
            entries[walkHereEntry] = entries[entryToReplace];
            entries[entryToReplace] = walkHereMenuEntry;
            this.client.setMenuEntries(entries);
        }

    }

    private Integer searchIndex(MenuEntry[] pMenuEntries, String pMenuEntryToSearchFor) {
        Integer indexLocation = 0;
        MenuEntry[] var4 = pMenuEntries;
        int var5 = pMenuEntries.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            MenuEntry menuEntry = var4[var6];
            String entryOption = Text.removeTags(menuEntry.getOption()).toUpperCase();
            if (entryOption.equals(pMenuEntryToSearchFor)) {
                return indexLocation;
            }

            indexLocation = indexLocation + 1;
        }

        return null;
    }

    public void setHotKeyPressed(boolean hotKeyPressed) {
        this.hotKeyPressed = hotKeyPressed;
    }
}
