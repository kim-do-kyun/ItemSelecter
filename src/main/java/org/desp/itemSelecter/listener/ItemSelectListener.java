package org.desp.itemSelecter.listener;

import static org.desp.itemSelecter.utils.ItemSelecterUtils.getCurrentTime;

import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.objects.Mail;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
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

public class  ItemSelectListener implements Listener {

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

        player.openInventory(confirmInventory);
    }

    @EventHandler
    public void onConfirmClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("선택 확정 창")) return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        String itemName = clickedItem.getItemMeta().getDisplayName();

        Map<String, ItemListDto> itemListMap = itemRepository.getItemListMap();

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        String itemInMainHand_ID = MMOItems.getID(itemInMainHand);

        ItemStack selectedItem = itemSelectCache.get(player.getUniqueId().toString());

        String selectedItemID = MMOItems.getID(selectedItem);
        ItemListDto itemListDto = itemListMap.get(itemInMainHand_ID);

        Integer itemQuantity = itemListDto.getRewardItems().get(selectedItemID);

        if (itemName.equals("§f이 아이템으로 §a선택§f하겠습니다.")) {
            List<ItemStack> reward = new ArrayList<>();
            ItemStack rewardItem;
            if (MMOItems.plugin.getItem(Type.SWORD, selectedItemID) == null) {
                rewardItem = MMOItems.plugin.getItem(Type.MISCELLANEOUS, selectedItemID);

            } else {
                rewardItem = MMOItems.plugin.getItem(Type.SWORD, selectedItemID);
            }
            rewardItem.setAmount(itemQuantity);
            reward.add(rewardItem);

            MMOMail mmoMail = MMOMail.getInstance();
            Mail rewardMail = mmoMail.getMailAPI().createMail(
                    "시스템",
                    "선택권 사용 보상입니다.",
                    0,
                    reward
            );
            mmoMail.getMailAPI().sendMail(player.getName(), rewardMail);

            player.sendMessage("§a 아이템이 메일함으로 지급되었습니다.");

            itemInMainHand.setAmount(itemInMainHand.getAmount() - 1);
            player.getInventory().setItemInMainHand(itemInMainHand);

            ItemSelectLogDto newLog = ItemSelectLogDto.builder()
                            .user_id(player.getName())
                                    .uuid(player.getUniqueId().toString())
                                            .selectedItem(selectedItemID)
                                                    .quantity(itemQuantity)
                                                            .build();

            itemSelectLogRepository.insertSelectLog(newLog, getCurrentTime());

            player.closeInventory();
        } else if (itemName.equals("§f다시 §c선택§f하겠습니다.")) {
            player.sendMessage("§c 선택이 취소되었습니다.");
        }
        player.closeInventory();
    }
}
