package net.runelite.client.plugins.shiftwalker;

import java.util.HashSet;

public final class ShiftWalkerGroups {
    private static final String BOX_TRAP = "BOX TRAP";
    private static final String BOX_TRAP_SHAKING = "SHAKING BOX";
    private static final String BOX_TRAP_DISMANTLE = "DISMANTLE";
    private static final String BOX_TRAP_CHECK = "CHECK";
    private static final String ATTACK_OPTIONS_ATTACK = "ATTACK";
    public static final HashSet<String> BOX_TRAP_TARGETS = new HashSet();
    public static final HashSet<String> BOX_TRAP_KEYWORDS = new HashSet();
    public static final HashSet<String> ATTACK_OPTIONS_KEYWORDS = new HashSet();

    public ShiftWalkerGroups() {
    }

    static {
        BOX_TRAP_TARGETS.add("BOX TRAP");
        BOX_TRAP_TARGETS.add("SHAKING BOX");
        BOX_TRAP_KEYWORDS.add("DISMANTLE");
        BOX_TRAP_KEYWORDS.add("CHECK");
        ATTACK_OPTIONS_KEYWORDS.add("ATTACK");
    }
}
