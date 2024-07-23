package dev.nedhuman.cakeday;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class WhenIsMyCakeDayCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player player) {
            LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(player.getFirstPlayed()), ZoneId.systemDefault());
            int month = date.getMonthValue();
            int day = date.getDayOfMonth();
            player.sendMessage(ChatColor.GOLD+"Your cake day is on "+month+"-"+day);
        }
        return true;
    }
}
