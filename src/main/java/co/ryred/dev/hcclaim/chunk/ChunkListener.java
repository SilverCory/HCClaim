package co.ryred.dev.hcclaim.chunk;

import co.ryred.dev.hcclaim.HCClaimPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public record ChunkListener(HCClaimPlugin plugin,
                            ChunkManager manager) implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChunkLoad(ChunkLoadEvent ev) {
        plugin.getServer().getScheduler()
                .runTaskAsynchronously(plugin, () -> this.manager.loadChunk(ev.getChunk()));

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChunkUnload(ChunkUnloadEvent ev) {
        plugin.getServer().getScheduler()
                .runTaskAsynchronously(plugin, () -> this.manager.unloadChunk(ev.getChunk()));
    }
}
