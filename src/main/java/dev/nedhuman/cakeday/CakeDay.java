package dev.nedhuman.cakeday;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class CakeDay extends JavaPlugin {
    public static CakeDay INSTANCE;
    public static NamespacedKey CAKE_DAY_KEY;
    private ItemStack cakeDayItem;
    private String msg;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        INSTANCE = this;
        CAKE_DAY_KEY = new NamespacedKey(this, "cakeday");
        getServer().getPluginManager().registerEvents(new CakeDayListener(), this);
        try{
            msg = ChatColor.translateAlternateColorCodes('&', getConfig().getString("broadcast-msg"));
            cakeDayItem = loadCakeDayItem();
        }catch (NullPointerException e) {
            getPluginLoader().disablePlugin(this);
        }
        getCommand("whenismycakeday").setExecutor(new WhenIsMyCakeDayCommand());
    }

    public String getMsg() {
        return msg;
    }
    public ItemStack getCakeDayItem() {
        return cakeDayItem;
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public ItemStack loadCakeDayItem() throws NullPointerException{
        ConfigurationSection cdi = getConfig().getConfigurationSection("cake-day-item");
        if(cdi == null) {
            throw new IllegalArgumentException("Bad config: cake-day-item is null");
        }
        Material type = Material.getMaterial(cdi.getString("type"));
        String name = ChatColor.translateAlternateColorCodes('&', cdi.getString("name"));
        List<String> lore = new ArrayList<>();
        for(String i : cdi.getStringList("lore")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', i));
        }
        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
