package co.ryred.dev.hcclaim.protection;

import co.ryred.dev.hcclaim.HCClaimPlugin;
import co.ryred.dev.hcclaim.chunk.ChunkData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ProtectionPlanner {

    public static Collection<Location> getProtectionPoints(Location origin, int radius) {
        World world = origin.getWorld();
        int maxX = origin.getBlockX() + radius;
        int maxY = origin.getBlockY() + radius;
        int maxZ = origin.getBlockZ() + radius;
        int minX = origin.getBlockX() - radius;
        int minY = origin.getBlockY() - radius;
        int minZ = origin.getBlockZ() - radius;

        Set<Location> locations = new HashSet<>();
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    locations.add(new Location(world, x, y, z));
                }
            }
        }
        return locations;
    }

    public static boolean isMonument(Block block) throws NoWhereNearAMonumentException, NotEnoughVerticalException {
        int yOffset;
        Material requiredBlock;

        switch (block.getType()) {
            case BELL -> {
                yOffset = -1;
                requiredBlock = Material.OBSIDIAN;
            }
            case OBSIDIAN -> {
                yOffset = 1;
                requiredBlock = Material.BELL;
            }
            default -> throw new NoWhereNearAMonumentException(); // So I've gotten used to writing go with multiple returns...
        }

        if (!block.getRelative(0, yOffset, 0).getType().equals(requiredBlock)) {
            return false;
        }

        if (block.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
            return true;
        }

        if (block.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
            // TODO manage this.
            return false;
        }

        if (block.getLightFromSky() == 0) {
            throw new NotEnoughVerticalException();
        }

        return true;

    }

    public static Block getBellBlock(Block block) {
        if (block.getType().equals(Material.OBSIDIAN)) {
            return block.getRelative(0, 1, 0);
        }
        return block;
    }

    public static UUID getBlockOwner(HCClaimPlugin plugin, Block block) throws ChunkNotLoadedException {
        ChunkData chunkData = plugin.getChunkManager().getChunkData(block.getChunk());
        if (chunkData == null) {
            throw new ChunkNotLoadedException();
        }

        return chunkData.getOwner(block.getLocation());
    }

    public static class ChunkNotLoadedException extends Throwable {
    }

    public static class NoWhereNearAMonumentException extends Throwable {
    }

    public static class NotEnoughVerticalException extends Throwable {
    }
}
