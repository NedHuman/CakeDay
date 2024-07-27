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
import java.util.Objects;

public final class CakeDay extends JavaPlugin {

    public static CakeDay INSTANCE;
    public static NamespacedKey CAKE_DAY_KEY;
    private ItemStack cakeDayItem;
    private String msg;
    private int daysCanClaim;
    private String[] commands;

    @Override
    public void onEnable() {
        INSTANCE = this;
        saveDefaultConfig();
        CAKE_DAY_KEY = new NamespacedKey(this, "cakeday");
        getServer().getPluginManager().registerEvents(new CakeDayListener(), this);
        try {
            msg = ChatColor.translateAlternateColorCodes('&', getConfig().getString("broadcast-msg", "&6{player} has received a cake for joining on their cake day!"));
            cakeDayItem = loadCakeDayItem();
            daysCanClaim = daysCanClaim();
            commands = commands();
        } catch (Exception e) {
            getLogger().warning(e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
        Objects.requireNonNull(getCommand("receivedcake")).setExecutor(new ReceivedCakeCommand());
        Objects.requireNonNull(getCommand("whenismycakeday")).setExecutor(new CakeDayCommand());
    }

    public String getMsg() {
        return msg;
    }

    public ItemStack getCakeDayItem() {
        return cakeDayItem;
    }

    private int daysCanClaim() {
        return getConfig().getInt("days-can-claim");
    }

    private String[] commands() {
        return getConfig().getStringList("commands").toArray(new String[0]);
    }

    public ItemStack loadCakeDayItem() throws NullPointerException {
        ConfigurationSection cdi = getConfig().getConfigurationSection("cake-day-item");
        if (cdi == null) {
            throw new IllegalArgumentException("Bad config: cake-day-item is null");
        }
        Material type = Material.getMaterial(cdi.getString("type", "CAKE"));
        String name = ChatColor.translateAlternateColorCodes('&', cdi.getString("name", "&6Cake Day"));
        List<String> lore = new ArrayList<>();
        for (String i : cdi.getStringList("lore")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', i));
        }
        assert type != null;
        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
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
