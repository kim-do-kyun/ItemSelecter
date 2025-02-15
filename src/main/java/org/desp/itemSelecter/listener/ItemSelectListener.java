package org.desp.itemSelecter.listener;

import static org.desp.itemSelecter.utils.ItemSelecterUtils.getCurrentTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.desp.itemSelecter.database.ItemRepository;
import org.desp.itemSelecter.database.ItemSelectLogRepository;
import org.desp.itemSelecter.dto.ItemListDto;
import org.desp.itemSelecter.dto.ItemSelectLogDto;

public class ItemSelectListener implements Listener {

    private final ItemRepository itemRepository;
    private final ItemSelectLogRepository itemSelectLogRepository;
    private static Map<String, ItemStack> itemSelectCache = new HashMap<>();

    public ItemSelectListener() {
        this.itemSelectLogRepository = ItemSelectLogRepository.getInstance();
        this.itemRepository = ItemRepository.getInstance();
    }

    @EventHandler
    public void onPlayerUseItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
//        ItemRepository itemRepository = ItemRepository.getInstance();
        Map<String, ItemListDto> itemListMap = itemRepository.getItemListMap();

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        String playerRightHandItemId = MMOItems.getID(itemInMainHand);

        if (event.getAction().isRightClick() && itemListMap.containsKey(playerRightHandItemId)) {
            ItemListDto itemListDto = itemListMap.get(playerRightHandItemId);
            Inventory inventory = Bukkit.createInventory(player, 27, "아이템 보상 선택");

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
            player.openInventory(inventory);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("아이템 보상 선택")) return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        itemSelectCache.put(player.getUniqueId().toString(), clickedItem);

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        Inventory confirmInventory = Bukkit.createInventory(player, 9, "선택 확정 창");

        ItemStack yesItem = new ItemStack(Material.GREEN_WOOL);
        //yesItem.getItemMeta().setDisplayName("Yes");

        ItemMeta itemMeta = yesItem.getItemMeta();
        itemMeta.setDisplayName("Yes");
        yesItem.setItemMeta(itemMeta);

        ItemStack noItem = new ItemStack(Material.RED_WOOL);
        //noItem.getItemMeta().setDisplayName("No");

        ItemMeta itemMeta1 = noItem.getItemMeta();
        itemMeta1.setDisplayName("No");
        noItem.setItemMeta(itemMeta1);

        confirmInventory.setItem(3, yesItem);
        confirmInventory.setItem(5, noItem);

        player.openInventory(confirmInventory);
    }

    @EventHandler
    public void onConfirmClick(InventoryClickEvent event) {
        System.out.println("ItemSelectListener.onConfirmClick");
        if (!event.getView().getTitle().equals("선택 확정 창")) return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        System.out.println("clickedItem = " + clickedItem);

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        System.out.println("asdfasdfadsf");
        String itemName = clickedItem.getItemMeta().getDisplayName();

        System.out.println("itemName = " + itemName);

//        ItemRepository itemRepository = ItemRepository.getInstance();
        Map<String, ItemListDto> itemListMap = itemRepository.getItemListMap();

        for (String s : itemListMap.keySet()) {
            System.out.println("s = " + s);
        }

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        ItemStack selectedItem = itemSelectCache.get(player.getUniqueId().toString());

        String selectedItemID = MMOItems.getID(selectedItem);
        System.out.println("selectedItemID = " + selectedItemID);
        ItemListDto itemListDto = itemListMap.get(itemInMainHand.getItemMeta().getDisplayName());

        //String playerRightHandItemId = MMOItems.getID(itemInMainHand);

        //ItemListDto itemListDto = itemListMap.get(playerRightHandItemId);

        Map<String, Integer> rewardItems = itemListDto.getRewardItems();
        for (Entry<String, Integer> stringIntegerEntry : rewardItems.entrySet()) {
            System.out.println("stringIntegerEntry.getKey() = " + stringIntegerEntry.getKey());
            System.out.println("stringIntegerEntry.getValue() = " + stringIntegerEntry.getValue());
        }

        Integer itemQuantity = itemListDto.getRewardItems().get(selectedItemID);
        System.out.println("-------------디비에 저장될 정보(아이템)----------------------");
        System.out.println("selectedItemID = " + selectedItemID);
        System.out.println("itemQuantity = " + itemQuantity);
        System.out.println("----------------------------------------------------------");

        if (itemName.equals("Yes")) {
            // 메일함으로 지급 로직 구현
            player.sendMessage("아이템이 메일함으로 지급되었습니다.");

            // 아이템 지급 후에 아이템 소모
            //ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
            itemInMainHand.setAmount(itemInMainHand.getAmount() - 1);
            player.getInventory().setItemInMainHand(itemInMainHand);

//            player.getInventory().removeItem(itemInMainHand);
            ItemSelectLogDto newLog = ItemSelectLogDto.builder()
                            .user_id(player.getName())
                                    .uuid(player.getUniqueId().toString())
                                            .selectedItem(selectedItemID)
                                                    .quantity(itemQuantity)
                                                            .build();

//            ItemSelectLogRepository itemSelectLogRepository = ItemSelectLogRepository.getInstance();
            itemSelectLogRepository.insertSelectLog(newLog, getCurrentTime());

            System.out.println(itemInMainHand+" 제거 완료");
            player.closeInventory();
        } else if (itemName.equals("No")) {
            System.out.println("선택 취소됨");
            player.sendMessage("선택이 취소되었습니다.");
        }

        player.closeInventory();
    }
}
