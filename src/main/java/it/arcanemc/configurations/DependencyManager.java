package it.arcanemc.configurations;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

@Getter
public class DependencyManager {
    private final ArrayList<String> dependencies;

    public DependencyManager() {
        this.dependencies = new ArrayList<>();
    }

    public void add(String string){
        dependencies.add(string);
    }

    public void remove(String string){
        dependencies.remove(string);
    }

    public void validate() {
        for (String dependency : this.dependencies){
            Plugin pluginDependency = Bukkit.getPluginManager().getPlugin(dependency);

            if (pluginDependency == null || !pluginDependency.isEnabled()) {
                throw new IllegalStateException(
                        String.format("Missed dependency: %s is not present or not enabled", dependency)
                );
            }
        }
    }
}
