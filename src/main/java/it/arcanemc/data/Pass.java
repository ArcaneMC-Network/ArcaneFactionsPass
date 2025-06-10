package it.arcanemc.data;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public class Pass {
    private final String name;
    private final Boolean isDefault;
    private final ItemStack itemStack;

    public Pass(String name, Boolean isDefault, ItemStack itemStack) {
        this.name = name.toLowerCase();
        this.isDefault = isDefault;
        this.itemStack = itemStack;
    }
}
