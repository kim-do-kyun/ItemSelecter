package org.desp.itemSelecter.listener;

import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.desp.itemSelecter.database.ItemRepository;
import org.desp.itemSelecter.dto.ItemListDto;

public class ItemSelectListener implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ItemRepository itemRepository = ItemRepository.getInstance();
        Map<String, ItemListDto> itemListMap = itemRepository.getItemListMap();

        for (Entry<String, ItemListDto> stringItemListDtoEntry : itemListMap.entrySet()) {
            System.out.println(
                    "stringItemListDtoEntry.getValue().getConsumeItemID() = " + stringItemListDtoEntry.getValue()
                            .getConsumeItemID());
            Map<String, Integer> rewardItems = stringItemListDtoEntry.getValue().getRewardItems();
            for (Entry<String, Integer> stringIntegerEntry : rewardItems.entrySet()) {
                System.out.println("stringIntegerEntry.getKey() = " + stringIntegerEntry.getKey());
                System.out.println("stringIntegerEntry.getValue() = " + stringIntegerEntry.getValue());
            }
//            for (Integer value : rewardItems.values()) {
//                System.out.println("value = " + value);
//            }
//            System.out.println("rewardItems.get(사용될MMOITemID) = " + rewardItems.get("사용될MMOITemID"));
        }

    }
}
