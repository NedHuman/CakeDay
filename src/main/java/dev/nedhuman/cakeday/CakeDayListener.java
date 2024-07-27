package dev.nedhuman.cakeday;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CakeDayListener implements Listener {
    private static ItemStack replaceNameAndDate(ItemStack item, Player player, int year) {
        ItemStack newItem = item.clone();
        ItemMeta meta = newItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(meta.getDisplayName().replace("{player}", player.getName()).replace("{date}", formatDate(player, year)));
        List<String> newLore = new ArrayList<>();
        for (String i : Objects.requireNonNull(meta.getLore())) {
            newLore.add(i.replace("{player}", player.getName()).replace("{date}", formatDate(player, year)));
        }
        meta.setLore(newLore);
        newItem.setItemMeta(meta);
        return newItem;
    }

    private static String formatDate(Player player, int year) {
        LocalDate joined = LocalDate.ofInstant(Instant.ofEpochMilli(player.getFirstPlayed()), ZoneId.systemDefault());
        return year + "-" + joined.getMonthValue() + "-" + joined.getDayOfMonth();
    }

    private static boolean cakeDayYearIsTheSameYearAsThePlayerFirstJoined(Player player, int year) {
        LocalDate joined = LocalDate.ofInstant(Instant.ofEpochMilli(player.getFirstPlayed()), ZoneId.systemDefault());
        return joined.getYear() == year;
    }

    private static int howManyDaysAgoWasCakeDay(Player player) {
        LocalDate cakeDay = LocalDate.ofInstant(Instant.ofEpochMilli(player.getFirstPlayed()), ZoneId.systemDefault());
        LocalDate now = LocalDate.now();

        LocalDate mostRecentCakeDay;

        // Should we use last year or this year's cake day?
        if (now.getDayOfYear() >= cakeDay.getDayOfYear()) {
            mostRecentCakeDay = LocalDate.of(now.getYear(), cakeDay.getMonth(), cakeDay.getDayOfMonth());
        } else {
            mostRecentCakeDay = LocalDate.of(now.getYear() - 1, cakeDay.getMonth(), cakeDay.getDayOfMonth());
        }
        return (int) ChronoUnit.DAYS.between(mostRecentCakeDay, now);
    }

    private static boolean playerHasClaimedThisYear(Player player, int year) {
        List<Integer> years = player.getPersistentDataContainer().get(CakeDay.CAKE_DAY_KEY, PersistentDataType.LIST.integers());
        if (years == null) {
            years = new ArrayList<>();
        }
        return years.contains(year);
    }

    private static int getYearTheseDaysAgo(int daysAgo) {
        LocalDate now = LocalDate.now();
        return now.minusDays(daysAgo).getYear();
    }

    private static void registerClaim(Player player, int year) {
        List<Integer> years = player.getPersistentDataContainer().get(CakeDay.CAKE_DAY_KEY, PersistentDataType.LIST.integers());
        if (years == null) {
            years = new ArrayList<>();
        }
        years.add(year);
        player.getPersistentDataContainer().set(CakeDay.CAKE_DAY_KEY, PersistentDataType.LIST.integers(), years);
    }

    private static void launchFirework(Player player) {
        // Minecraft cake colours
        FireworkEffect red = FireworkEffect.builder()
                .withColor(Color.RED)
                .with(FireworkEffect.Type.BALL)
                .build();
        FireworkEffect white = FireworkEffect.builder()
                .withColor(Color.WHITE)
                .with(FireworkEffect.Type.BALL)
                .build();
        FireworkEffect brown = FireworkEffect.builder()
                .withColor(Color.fromRGB(181, 101, 29))
                .with(FireworkEffect.Type.BALL)
                .build();

        Firework fw = player.getWorld().spawn(player.getEyeLocation(), Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();
        meta.setPower(1);
        meta.addEffect(red);
        meta.addEffect(white);
        meta.addEffect(brown);
        fw.setFireworkMeta(meta);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        int daysAgo = howManyDaysAgoWasCakeDay(player);
        // Checks if the most recent cake day is within bounds of claiming
        if (daysAgo <= CakeDay.INSTANCE.getDaysCanClaim()) {
            int yearTheseDaysAgo = getYearTheseDaysAgo(daysAgo);
            // Checks if the player has claimed this year already
            if (!playerHasClaimedThisYear(player, yearTheseDaysAgo)) {
                if (cakeDayYearIsTheSameYearAsThePlayerFirstJoined(player, yearTheseDaysAgo)) {
                    return;
                }
                // Registers the claim
                registerClaim(player, yearTheseDaysAgo);

                // Execute rewards (wait 2 seconds first)
                Bukkit.getScheduler().runTaskLater(CakeDay.INSTANCE, () -> {
                    for (Player i : Bukkit.getOnlinePlayers()) {
                        i.sendMessage(CakeDay.INSTANCE.getMsg().replace("{player}", player.getName()));
                    }
                    for (String i : CakeDay.INSTANCE.getCommands()) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), i.replace("{player}", player.getName()));
                    }

                    player.getInventory().addItem(replaceNameAndDate(CakeDay.INSTANCE.getCakeDayItem(), player, yearTheseDaysAgo));
                    launchFirework(player);
                }, 20 * 2);
            }
        }
    }

}
