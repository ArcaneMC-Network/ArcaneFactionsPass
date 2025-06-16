package it.arcanemc.util.loader;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class SoundLoader {
    private static List<?> get(ConfigurationSection soundConfig) {
        return List.of(
                Sound.valueOf(soundConfig.getString("name").toUpperCase()),
                soundConfig.getDouble("volume"),
                soundConfig.getDouble("pitch")
        );
    }

    public static void play(Player player, ConfigurationSection soundConfig) {
        List<?> soundData = get(soundConfig);
        Sound sound = (Sound) soundData.get(0);
        float volume = ((Number) soundData.get(1)).floatValue();
        float pitch = ((Number) soundData.get(2)).floatValue();

        player.playSound(player.getLocation(), sound, volume, pitch);
    }
}
