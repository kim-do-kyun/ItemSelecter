package org.desp.itemSelecter.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.util.HashMap;
import java.util.Map;
import org.bson.Document;
import org.desp.itemSelecter.dto.ItemListDto;

public class ItemRepository {

    private static ItemRepository instance;
    private final MongoCollection<Document> itemList;
    private static Map<String, ItemListDto> itemListMap = new HashMap<>();

    public ItemRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.itemList = database.getDatabase().getCollection("ItemList");
        loadItemList();
    }

    public static synchronized ItemRepository getInstance() {
        if (instance == null) {
            instance = new ItemRepository();
        }
        return instance;
    }

    private void loadItemList() {
        FindIterable<Document> documents = itemList.find();
        for (Document document : documents) {
            String consumeItemID = document.getString("consumeItemID");
            Document rewards = (Document) document.get("rewards");

            Map<String, Integer> rewardsMap = new HashMap<>();
            for (String key : rewards.keySet()) {
                System.out.println("key = " + key);
                System.out.println("rewards.getInteger(key) = " + rewards.getInteger(key));
                rewardsMap.put(key, rewards.getInteger(key));
            }

            ItemListDto item = ItemListDto.builder()
                    .consumeItemID(consumeItemID)
                    .rewardItems(rewardsMap)
                    .build();

            itemListMap.put(consumeItemID, item);
        }
    }

    public Map<String, ItemListDto> getItemListMap() {
        return itemListMap;
    }
}
