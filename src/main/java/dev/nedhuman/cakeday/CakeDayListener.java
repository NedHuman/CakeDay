package dev.nedhuman.cakeday;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CakeDayListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(isCakeDay(event.getPlayer())) {

            // Code to check if they already claimed their cake day today
            Player player = event.getPlayer();

            List<Integer> years = event.getPlayer().getPersistentDataContainer().get(CakeDay.CAKE_DAY_KEY, PersistentDataType.LIST.integers());
            if(years == null) {
                years = new ArrayList<>();
            }
            int year = LocalDateTime.now().getYear();
            if(!years.contains(year)) {
                years.add(year);
                for(Player i : Bukkit.getOnlinePlayers()) {
                    i.sendMessage(CakeDay.INSTANCE.getMsg().replace("{player}", player.getName()));
                }
                player.getInventory().addItem(replaceNameAndDate(CakeDay.INSTANCE.getCakeDayItem(), player));
            }
            player.getPersistentDataContainer().set(CakeDay.CAKE_DAY_KEY, PersistentDataType.LIST.integers(), years);
        }
    }
    private static boolean isCakeDay(Player player) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstJoin = LocalDateTime.ofInstant(Instant.ofEpochMilli(player.getFirstPlayed()), ZoneId.systemDefault());

        return now.getDayOfMonth() == firstJoin.getDayOfMonth() && now.getMonthValue() == firstJoin.getMonthValue() && now.getYear() != firstJoin.getYear();
    }
    private static ItemStack replaceNameAndDate(ItemStack item, Player player) {
        ItemStack newItem = item.clone();
        ItemMeta meta = newItem.getItemMeta();
        meta.setDisplayName(meta.getDisplayName().replace("{player}", player.getName()).replace("{date}", new Date().toString()));
        List<String> newLore = new ArrayList<>();
        for(String i : meta.getLore()) {
            newLore.add(i.replace("{player}", player.getName()).replace("{date}", new Date().toString()));
        }
        meta.setLore(newLore);
        newItem.setItemMeta(meta);
        return newItem;
    }

}
