package it.arcanemc.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getLogger;

public class Msg {
    public static void all(String message) {
        all(message, true);
    }

    public static void all(String message, boolean isConsole){
        Bukkit.broadcastMessage(Colors.translate(message));
        if (isConsole)
            getLogger().info(Colors.translate(message));
    }

    public static void player(Player player, String message){
        player.sendMessage(Colors.translate(message));
    }
}