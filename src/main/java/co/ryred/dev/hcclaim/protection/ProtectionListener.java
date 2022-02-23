package co.ryred.dev.hcclaim.protection;

import co.ryred.dev.hcclaim.HCClaimPlugin;
import co.ryred.dev.hcclaim.chunk.ChunkData;
import co.ryred.dev.hcclaim.trust.TrustData;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.UUID;

public class ProtectionListener implements Listener {

    private final HCClaimPlugin plugin;

    public ProtectionListener(HCClaimPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBoom(BlockExplodeEvent e) {
        ChunkData chunkData = plugin.getChunkManager().getChunkData(e.getBlock().getChunk());
        if (chunkData == null) {
            e.setCancelled(true);
            return; // todo log?
        }

        UUID owner = chunkData.getOwner(e.getBlock().getLocation());
        if (owner == null) return;

        switch (e.getBlock().getType()) {
            case WHITE_STAINED_GLASS, ORANGE_STAINED_GLASS, MAGENTA_STAINED_GLASS, LIGHT_BLUE_STAINED_GLASS,
                    YELLOW_STAINED_GLASS, LIME_STAINED_GLASS, PINK_STAINED_GLASS, GRAY_STAINED_GLASS,
                    LIGHT_GRAY_STAINED_GLASS, CYAN_STAINED_GLASS, PURPLE_STAINED_GLASS, BLUE_STAINED_GLASS,
                    BROWN_STAINED_GLASS, GREEN_STAINED_GLASS, RED_STAINED_GLASS, BLACK_STAINED_GLASS,
                    WHITE_STAINED_GLASS_PANE, ORANGE_STAINED_GLASS_PANE, MAGENTA_STAINED_GLASS_PANE, LIGHT_BLUE_STAINED_GLASS_PANE,
                    YELLOW_STAINED_GLASS_PANE, LIME_STAINED_GLASS_PANE, PINK_STAINED_GLASS_PANE, GRAY_STAINED_GLASS_PANE,
                    LIGHT_GRAY_STAINED_GLASS_PANE, CYAN_STAINED_GLASS_PANE, PURPLE_STAINED_GLASS_PANE, BLUE_STAINED_GLASS_PANE,
                    BROWN_STAINED_GLASS_PANE, GREEN_STAINED_GLASS_PANE, RED_STAINED_GLASS_PANE, BLACK_STAINED_GLASS_PANE -> {
                e.setCancelled(false);
            }
            default -> e.setCancelled(true);
        }
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

    // TODO fire.

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
            return false;
        }

        return player.getUniqueId().equals(owner) || trustData.isTrusteeOn(owner);
    }

}
