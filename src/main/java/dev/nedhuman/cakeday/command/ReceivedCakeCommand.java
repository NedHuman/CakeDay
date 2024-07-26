package dev.nedhuman.cakeday.command;

import dev.nedhuman.cakeday.CakeDay;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class ReceivedCakeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            player.sendMessage(ChatColor.YELLOW + "You have received cake days on the following years:");
            List<Integer> years = player.getPersistentDataContainer().get(CakeDay.CAKE_DAY_KEY, PersistentDataType.LIST.integers());
            if (years == null || years.isEmpty()) {
                player.sendMessage(ChatColor.GRAY + "None");
                return true;
            }
            for (int i : years) {
                player.sendMessage(ChatColor.GRAY + "- " + ChatColor.WHITE + i);
            }
        }
        return true;
    }
}
