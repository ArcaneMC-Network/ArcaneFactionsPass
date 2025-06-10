package it.arcanemc.manager;

import it.arcanemc.data.Pass;
import it.arcanemc.data.Reward;
import it.arcanemc.util.ItemStackLoader;
import it.arcanemc.util.Timer;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
public class PassManager {
    private final HashMap<Pass, ArrayList<Reward>> passes;

    public PassManager(FileConfiguration yaml) {
        this.passes = new HashMap<>();
        this.load(yaml);
    }

    public PassManager(HashMap<Pass, ArrayList<Reward>> passes) {
        this.passes = passes;
    }

    public void sortRewards() {
        this.getPasses().values().forEach(rewards ->
                rewards.sort(Comparator.comparingLong(Reward::getRequiredTime))
        );
    }

    private void loadPasses(ConfigurationSection section){
        if (section != null) {
            for (String key : section.getKeys(false)){
                ConfigurationSection passSection = section.getConfigurationSection(key);
                if (passSection != null) {
                    boolean isDefault = passSection.getBoolean("default");
                    ConfigurationSection itemSection = passSection.getConfigurationSection("item");
                    ItemStack itemStack = ItemStackLoader.get(itemSection);
                    Pass pass = new Pass(key, isDefault, itemStack);
                    passes.put(pass, new ArrayList<>());
                }
            }
        }
    }

    private void loadPassesWithRewards(ConfigurationSection passesSection, ConfigurationSection rewardsSection){
        this.loadPasses(passesSection);
        if (rewardsSection != null) {
            for (String key : rewardsSection.getKeys(false)){
                ConfigurationSection rewardSection = rewardsSection.getConfigurationSection(key);
                if (rewardSection != null) {
                    ConfigurationSection itemSection = rewardSection.getConfigurationSection("item");
                    String requiredTimeVerbose = rewardSection.getString("required-time");
                    long requiredTime = Timer.convertVerbose(requiredTimeVerbose);
                    ItemStack itemStack = ItemStackLoader.get(itemSection);
                    String name = rewardSection.getString("name");
                    ArrayList<String> commands = (ArrayList<String>) rewardSection.getStringList("commands");
                    String passName = rewardSection.getString("pass");
                    Optional<Pass> optionalPass = passes.keySet().stream()
                            .filter(instance -> passName.equalsIgnoreCase(instance.getName()))
                            .findFirst();
                    optionalPass.ifPresent(pass -> {
                        Reward reward = new Reward(name, itemStack, commands, requiredTime, pass);
                        ArrayList<Reward> rewards = this.passes.get(pass);
                        if (rewards != null) {
                            rewards.add(reward);
                        }
                    });
                }
            }
        }
        this.sortRewards();
    }

    public void load(FileConfiguration yaml){
        this.passes.clear();
        ConfigurationSection passesSection = yaml.getConfigurationSection("passes");
        ConfigurationSection rewardsSection = yaml.getConfigurationSection("rewards");
        this.loadPassesWithRewards(passesSection, rewardsSection);
    }
}
