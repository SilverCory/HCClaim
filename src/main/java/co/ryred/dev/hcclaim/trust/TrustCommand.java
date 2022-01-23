package co.ryred.dev.hcclaim.trust;

import co.ryred.dev.hcclaim.HCClaimPlugin;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public record TrustCommand(HCClaimPlugin plugin, boolean trust) implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) return false;
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can run this command.");
            return true;
        }

        Set<String> notFoundUsers = new HashSet<>();
        Set<String> foundUsers = new HashSet<>();
        Set<UUID> trustees = new HashSet<>();
        Arrays.stream(args).forEach(s -> {
            OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(s);
            if (!offlinePlayer.hasPlayedBefore()) {
                notFoundUsers.add(s);
                return;
            }

            trustees.add(offlinePlayer.getUniqueId());
            foundUsers.add(offlinePlayer.getName());
        });

        this.plugin.getTrustManager().setTrusted(trustees, player.getUniqueId(), trust);

        String trustedText = trust ? "trusted" : "untrusted";
        if (!notFoundUsers.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Unable to find the following players:");
            sender.sendMessage(ChatColor.RED + "   " + String.join(", ", notFoundUsers));
        }
        if (!foundUsers.isEmpty()) {
            sender.sendMessage(ChatColor.GREEN + "The following player have been " + trustedText + ":");
            sender.sendMessage(ChatColor.GREEN + "   " + String.join(", ", foundUsers));
        }

        return true;
    }
}