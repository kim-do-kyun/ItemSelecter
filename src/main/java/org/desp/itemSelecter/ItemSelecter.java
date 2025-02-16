package org.desp.itemSelecter;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.desp.itemSelecter.database.ItemRepository;
import org.desp.itemSelecter.listener.ItemSelectListener;

public final class ItemSelecter extends JavaPlugin {

    @Getter
    private static ItemSelecter instance;
    @Override
    public void onEnable() {
        this.instance = this;

        ItemRepository.getInstance();
        Bukkit.getPluginManager().registerEvents(new ItemSelectListener(), this);
    }

    @Override
    public void onDisable() {
    }
}
