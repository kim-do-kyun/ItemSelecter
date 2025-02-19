package org.desp.itemSelecter.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class ItemConfirmGUI implements InventoryHolder {

    public Inventory confirmInventory;
    @Override
    public @NotNull Inventory getInventory() {

        confirmInventory = Bukkit.createInventory(this, 9, "선택 확정 창");

        ItemStack yesItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);

        ItemMeta itemMeta = yesItem.getItemMeta();
        itemMeta.setDisplayName("§f이 아이템으로 §a선택§f하겠습니다.");
        yesItem.setItemMeta(itemMeta);

        ItemStack noItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);

        ItemMeta itemMeta1 = noItem.getItemMeta();
        itemMeta1.setDisplayName("§f다시 §c선택§f하겠습니다.");
        noItem.setItemMeta(itemMeta1);

        confirmInventory.setItem(3, yesItem);
        confirmInventory.setItem(5, noItem);
        return confirmInventory;
    }
}
