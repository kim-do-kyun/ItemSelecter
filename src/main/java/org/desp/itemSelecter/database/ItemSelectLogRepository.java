package org.desp.itemSelecter.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import java.util.HashMap;
import java.util.Map;
import org.bson.Document;
import org.desp.itemSelecter.dto.ItemSelectLogDto;

public class ItemSelectLogRepository {

    private static ItemSelectLogRepository instance;
    private final MongoCollection<Document> itemSelectLog;
    private static Map<String, ItemSelectLogDto> itemSelectLogMap = new HashMap<>();

    public ItemSelectLogRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.itemSelectLog = database.getDatabase().getCollection("ItemSelectLog");
        loadItemList();
    }

    public static synchronized ItemSelectLogRepository getInstance() {
        if (instance == null) {
            instance = new ItemSelectLogRepository();
        }
        return instance;
    }

    private void loadItemList() {
        FindIterable<Document> documents = itemSelectLog.find();
        for (Document document : documents) {
            String user_id = document.getString("user_id");

            ItemSelectLogDto itemSelectLog = ItemSelectLogDto.builder()
                    .user_id(user_id)
                    .uuid(document.getString("uuid"))
                    .selectedItem(document.getString("selectedItem"))
                    .quantity(document.getInteger("quantity"))
                    .build();
            itemSelectLogMap.put(itemSelectLog.getUuid(), itemSelectLog);
        }
    }

    public void insertSelectLog(ItemSelectLogDto itemSelectLogDto, String currentTime) {
        Document document = new Document()
                .append("user_id", itemSelectLogDto.getUser_id())
                .append("uuid", itemSelectLogDto.getUuid())
                .append("selectedItem", itemSelectLogDto.getSelectedItem())
                .append("quantity", itemSelectLogDto.getQuantity())
                .append("time", currentTime);

        itemSelectLog.insertOne(document);
    }

    public Map<String, ItemSelectLogDto> getItemListMap() {
        return itemSelectLogMap;
    }
}
