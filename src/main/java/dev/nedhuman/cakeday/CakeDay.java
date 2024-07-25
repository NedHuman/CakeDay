package dev.nedhuman.cakeday;

import dev.nedhuman.cakeday.command.CakeDayCommand;
import dev.nedhuman.cakeday.command.ReceivedCakeCommand;
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
    private int daysCanClaim;
    private String[] commands;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        INSTANCE = this;
        CAKE_DAY_KEY = new NamespacedKey(this, "cakeday");
        getServer().getPluginManager().registerEvents(new CakeDayListener(), this);
        try{
            msg = ChatColor.translateAlternateColorCodes('&', getConfig().getString("broadcast-msg"));
            cakeDayItem = loadCakeDayItem();
            daysCanClaim = daysCanClaim();
            commands = commands();
        }catch (Exception e) {
            e.printStackTrace();
            getPluginLoader().disablePlugin(this);
        }
        getCommand("receivedcake").setExecutor(new ReceivedCakeCommand());
        getCommand("whenismycakeday").setExecutor(new CakeDayCommand());
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

    private int daysCanClaim() {
        return getConfig().getInt("days-can-claim");
    }

    private String[] commands() {
        return getConfig().getStringList("commands").toArray(new String[0]);
    }

    /**
     * Load the cake day item from config
     * @return The cake day item
     * @throws NullPointerException if malformed config
     */
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

    public int getDaysCanClaim() {
        return daysCanClaim;
    }

    public String[] getCommands() {
        return commands;
    }
}
