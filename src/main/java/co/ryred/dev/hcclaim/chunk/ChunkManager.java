package co.ryred.dev.hcclaim.chunk;

import co.ryred.dev.hcclaim.HCClaimPlugin;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class ChunkManager {

    private final HCClaimPlugin plugin;
    private final Map<String, ChunkData> chunkDataMap;
    private final ReentrantLock lock = new ReentrantLock();

    public ChunkManager(HCClaimPlugin plugin) {
        this.plugin = plugin;
        chunkDataMap = new ConcurrentHashMap<>();
    }

    public void protectChunks(Collection<Location> locations, UUID owner) {
        lock.lock();
        locations.stream()
                .collect(Collectors.groupingBy(
                        Location::getChunk, Collectors.toSet()
                )).forEach((chunk, l) -> getChunkData(chunk).setProtection(l, owner));
        lock.unlock();
    }

    public ChunkData getChunkData(Chunk chunk) {
        lock.lock();
        ChunkData chunkData = chunkDataMap.get(getChunkStringKey(chunk));
        lock.unlock();
        return chunkData;
    }

    public void loadChunk(Chunk chunk) {
        lock.lock();
        chunkDataMap.put(getChunkStringKey(chunk), new ChunkData(plugin, chunk.getWorld().getName(), chunk.getX(), chunk.getZ()));
        lock.unlock();
    }

    public void unloadChunk(Chunk chunk) {
        lock.lock();
        ChunkData chunkData = chunkDataMap.remove(getChunkStringKey(chunk));
        if (chunkData != null) chunkData.save();
        lock.unlock();
    }

    public void unloadChunks() {
        lock.lock();
        chunkDataMap.forEach((s, chunkData) -> chunkData.save());
        chunkDataMap.clear();
        lock.unlock();
    }

    private String getChunkStringKey(Chunk chunk) {
        return chunk.getWorld().getName() + "." + chunk.getX() + "." + chunk.getZ();
    }
}
