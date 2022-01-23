package co.ryred.dev.hcclaim.protection;

import co.ryred.dev.hcclaim.HCClaimPlugin;
import co.ryred.dev.hcclaim.particles.ParticleTask;
import io.papermc.paper.event.block.BellRingEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.UUID;

public class BeaconListener implements Listener {

    private final int radius = 16;

    private final HCClaimPlugin plugin;

    public BeaconListener(HCClaimPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        try {
            if (!ProtectionPlanner.isMonument(e.getBlock())) {
                return;
            }
        } catch (ProtectionPlanner.NoWhereNearAMonumentException ignored) {
            return;
        }

        try {
            UUID owner = ProtectionPlanner.getBlockOwner(plugin, e.getBlock());
            if (!e.getPlayer().getUniqueId().equals(owner)) {
                e.getPlayer().sendMessage(ChatColor.YELLOW + "Only the area owner can build monuments in their area.");
                e.setCancelled(true);
                return;
            }
        } catch (ProtectionPlanner.ChunkNotLoadedException ex) {
            plugin.getLogger().warning("Chunk not loaded.");
            e.getPlayer().sendMessage(ChatColor.RED + "An error has occurred whilst checking the owner of this block!");
            e.getPlayer().sendMessage(ChatColor.RED + "   You are temporarily not allowed to place/break blocks here.");
            e.setCancelled(true);
            return;
        }

        Location location = ProtectionPlanner.getBellBlock(e.getBlock()).getLocation();
        plugin.getChunkManager().protectChunks(ProtectionPlanner.getProtectionPoints(location, radius), null);
        e.getPlayer().sendMessage(ChatColor.YELLOW + "This area has been unclaimed!");
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        try {
            if (!ProtectionPlanner.isMonument(e.getBlock())) {
                // TODO add a cooldown here.
                e.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Place a bell on top of a block of obsidian to get your area protected!");
                return;
            }
        } catch (ProtectionPlanner.NoWhereNearAMonumentException ignored) {
            return;
        }

        try {
            UUID owner = ProtectionPlanner.getBlockOwner(plugin, e.getBlock());
            if (!e.getPlayer().getUniqueId().equals(owner)) {
                e.getPlayer().sendMessage(ChatColor.YELLOW + "Only the area owner can build monuments in their area.");
                e.setBuild(false);
                e.setCancelled(true);
                return;
            }
        } catch (ProtectionPlanner.ChunkNotLoadedException ex) {
            plugin.getLogger().warning("Chunk not loaded.");
            e.getPlayer().sendMessage(ChatColor.RED + "An error has occurred whilst checking the owner of this block!");
            e.getPlayer().sendMessage(ChatColor.RED + "   You are temporarily not allowed to place/break blocks here.");
            e.setBuild(false);
            e.setCancelled(true);
            return;
        }


        Location location = ProtectionPlanner.getBellBlock(e.getBlock()).getLocation();
        plugin.getChunkManager().protectChunks(ProtectionPlanner.getProtectionPoints(location, radius), e.getPlayer().getUniqueId());
        ParticleTask pt = new ParticleTask(plugin, location, radius, 0.5d);
        pt.runTaskTimer(plugin, 0, 4);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBellRing(BellRingEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

        try {
            if (!ProtectionPlanner.isMonument(e.getBlock())) {
                return;
            }
        } catch (ProtectionPlanner.NoWhereNearAMonumentException ignored) {
            return;
        }


        UUID owner;
        try {
            owner = ProtectionPlanner.getBlockOwner(plugin, e.getBlock());
        } catch (ProtectionPlanner.ChunkNotLoadedException ex) {
            plugin.getLogger().warning("Chunk not loaded.");
            player.sendMessage(ChatColor.RED + "An error has occurred whilst checking the owner of this block!");
            player.sendMessage(ChatColor.RED + "   You are temporarily not allowed to place/break blocks here.");
            return;
        }

        if (player.getUniqueId().equals(owner)) {
            ParticleTask pt = new ParticleTask(plugin, e.getBlock().getLocation(), radius, 0.5d);
            pt.runTaskTimer(plugin, 0, 4);
            return;
        }

        // TODO logic for takeover.
        player.sendMessage("Takeover initiated.");
    }

}
