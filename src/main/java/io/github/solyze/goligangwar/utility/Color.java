package io.github.solyze.goligangwar.utility;

import org.bukkit.ChatColor;

public class Color {

    public static String process(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
