package co.ryred.dev.hcclaim.particles;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ParticlePlanner {

    public static Collection<Location> getHollowCubePoints(Location center, double radius, double density) {
        List<Location> result = new ArrayList<>();

        center = center.toCenterLocation();
        World world = center.getWorld();
        double minX = center.getX() - radius;
        double minY = center.getY() - radius;
        double minZ = center.getZ() - radius;
        double maxX = center.getX() + radius;
        double maxY = center.getY() + radius;
        double maxZ = center.getZ() + radius;

        // 2 sides (top & bottom)
        for (double x = minX; x <= maxX; x += density) {
            for (double z = minZ; z <= maxZ; z += density) {
                result.add(new Location(world, x, minY, z));
                result.add(new Location(world, x, maxY, z));
            }
        }

        // 2 sides (front & back)
        for (double x = minX; x <= maxX; x += density) {
            for (double y = minY; y <= maxY; y += density) {
                result.add(new Location(world, x, y, minZ));
                result.add(new Location(world, x, y, maxZ));
            }
        }

        // 2 sides (left & right)
        for (double z = minZ; z <= maxZ; z += density) {
            for (double y = minY; y <= maxY; y += density) {
                result.add(new Location(world, minX, y, z));
                result.add(new Location(world, maxX, y, z));
            }
        }

        // Shuffle makes it look extra pretty and not so glitchy.
        Collections.shuffle(result);
        return result;
    }

}
