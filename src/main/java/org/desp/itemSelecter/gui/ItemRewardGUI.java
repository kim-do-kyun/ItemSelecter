package org.desp.itemSelecter.gui;

import java.util.Map;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.desp.itemSelecter.database.ItemRepository;
import org.desp.itemSelecter.dto.ItemListDto;
import org.jetbrains.annotations.NotNull;

public class ItemRewardGUI implements InventoryHolder {

    public Inventory inventory;
    public String playerRightHandItemId;

    public ItemRewardGUI(String playerRightHandItemId) {
        this.playerRightHandItemId = playerRightHandItemId;
    }
    @Override
    public @NotNull Inventory getInventory() {
        ItemListDto itemListDto = ItemRepository.getInstance().getItemListMap().get(playerRightHandItemId);
        Inventory inventory = Bukkit.createInventory(this, 27, "아이템 보상 선택");

        for (Map.Entry<String, Integer> entry : itemListDto.getRewardItems().entrySet()) {
            ItemStack rewardItem;
            if (MMOItems.plugin.getItem(Type.SWORD, entry.getKey()) == null) {
                rewardItem = MMOItems.plugin.getItem(Type.MISCELLANEOUS, entry.getKey());
            } else {
                rewardItem = MMOItems.plugin.getItem(Type.SWORD, entry.getKey());
            }
            rewardItem.setAmount(entry.getValue());

            inventory.addItem(rewardItem);
        }
        return inventory;
    }
}
