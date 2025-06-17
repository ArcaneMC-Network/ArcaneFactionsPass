package it.arcanemc.data;

import com.massivecraft.factions.FPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
public class Reward {

    private final String name;
    private final ItemStack itemStack;
    private final List<String> commands;
    private final long requiredTime;
    private final Pass pass;

    public Reward(String name, ItemStack itemStack, List<String> commands, long requiredTime, Pass pass) {
        this.name = name;
        this.itemStack = itemStack;
        this.commands = commands;
        this.requiredTime = requiredTime;
        this.pass = pass;
    }

    public String getPassRewardName(){
        return String.format("%s.%s", pass.getName().toLowerCase(), this.name);
    }

    public void claim(FPlayer player){
        this.commands.forEach(command -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                    .replace("{player}", player.getName())
                    .replace("{faction}", player.getFaction().getTag())
            );
        });
    }
}
