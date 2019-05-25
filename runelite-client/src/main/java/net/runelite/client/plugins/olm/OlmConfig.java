package net.runelite.client.plugins.olm;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("olmshadow")
public interface OlmConfig extends Config{
    @Alpha
    @ConfigItem(
            position = 3,
            keyName = "shadow",
            name = "Shadow Color",
            description = "Color of his shadow"
    )
    default Color shadow()
    {
        return Color.red;
    }

}
