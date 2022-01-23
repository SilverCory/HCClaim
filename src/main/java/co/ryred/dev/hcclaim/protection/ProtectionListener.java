package co.ryred.dev.hcclaim.protection;

import co.ryred.dev.hcclaim.HCClaimPlugin;
import co.ryred.dev.hcclaim.trust.TrustData;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.UUID;

public class ProtectionListener implements Listener {

    private final HCClaimPlugin plugin;

    public ProtectionListener(HCClaimPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        if (!canBuild(e.getBlock(), e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlaced(BlockPlaceEvent e) {
        if (!canBuild(e.getBlock(), e.getPlayer())) {
            e.setCancelled(true);
            e.setBuild(false);
        }
    }

    // TODO explosions and fire.

    public boolean canBuild(Block block, Player player) {
        UUID owner;
        try {
            owner = ProtectionPlanner.getBlockOwner(plugin, block);
        } catch (ProtectionPlanner.ChunkNotLoadedException e) {
            plugin.getLogger().warning("Chunk not loaded.");
            player.sendMessage(ChatColor.RED + "An error has occurred whilst checking the owner of this block!");
            player.sendMessage(ChatColor.RED + "   You are temporarily not allowed to place/break blocks here.");
            return false;
        }

        if (owner == null) {
            return true;
        }

        TrustData trustData = plugin.getTrustManager().getTrustData(player.getUniqueId());
        if (trustData == null) {
            plugin.getLogger().warning("Trusts not loaded.");
            player.sendMessage(ChatColor.RED + "An error has occurred whilst checking if you are trusted!");
            player.sendMessage(ChatColor.RED + "   You are temporarily not allowed to place/break blocks here.");
            return false;// TODO this shouldn't occur.
        }

        return player.getUniqueId().equals(owner) || trustData.isTrusteeOn(owner);
    }

}
