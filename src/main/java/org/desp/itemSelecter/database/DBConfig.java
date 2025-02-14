package org.desp.itemSelecter.database;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import org.desp.itemSelecter.ItemSelecter;

public class DBConfig {

    public String getMongoConnectionContent(){
        File file = new File(ItemSelecter.getInstance().getDataFolder().getPath() + "/config.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        String url = yml.getString("mongodb.url");
        int port = yml.getInt("mongodb.port");
        String address = yml.getString("mongodb.address");

        return String.format("%s%s:%s/ItemSelecter", url,address, port);
    }
}
