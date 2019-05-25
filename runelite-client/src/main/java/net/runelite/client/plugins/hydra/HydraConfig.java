package net.runelite.client.plugins.hydra;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("hydraplugin")
public interface HydraConfig extends Config {
    @ConfigItem(
            keyName = "achat",
            name = "Show prayers in chat",
            description = "Shows prayers in chat",
            position = 2,
            hidden = true
    )
    default boolean chat()
    {
        return false;
    }
    @ConfigItem(
            keyName = "adebug",
            name = "Debug",
            description = "Debug",
            position = 3,
            hidden = true
    )
    default boolean debug()
    {
        return false;
    }

}
