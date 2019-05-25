
package net.runelite.client.plugins.shiftwalker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("shiftwalkhere")
public interface ShiftWalkerConfig extends Config {
    @ConfigItem(
            keyName = "shiftWalkEverything",
            name = "Walk Under Everything",
            description = "Enable this option when you do not want to interact with anything while Shift is pressed. If Walk Here is an option it will be the action taken."
    )
    default boolean shiftWalkEverything() {
        return true;
    }

    @ConfigItem(
            keyName = "shiftWalkBoxTraps",
            name = "Walk Under Box Traps",
            description = "Press \"Shift\" to be able to walk under instead of picking up a Box Trap."
    )
    default boolean shiftWalkBoxTraps() {
        return true;
    }

    @ConfigItem(
            keyName = "shiftWalkAttackOption",
            name = "Walk Under Attack Options",
            description = "Press \"Shift\" to be able to walk instead of attacking. Make sure Left Click Attack is on."
    )
    default boolean shiftWalkAttackOption() {
        return true;
    }
}
