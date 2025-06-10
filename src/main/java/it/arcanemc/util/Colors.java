package it.arcanemc.util;

import net.md_5.bungee.api.ChatColor;

public class Colors {
    public static String translate(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
