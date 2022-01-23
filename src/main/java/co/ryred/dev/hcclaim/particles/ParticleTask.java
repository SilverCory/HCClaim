package co.ryred.dev.hcclaim.particles;

import co.ryred.dev.hcclaim.HCClaimPlugin;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;


public class ParticleTask extends BukkitRunnable {

    private final HCClaimPlugin plugin;
    private final Location center;
    private final double maxRadius;
    private final double radiusIncrement;
    private double lastRadius = 0;
    private Collection<Location> lastPoints;
    private int lingeringIterations = 0;

    public ParticleTask(HCClaimPlugin plugin, Location center, double maxRadius, double radiusIncrement) {
        this.plugin = plugin;
        this.center = center;
        this.maxRadius = maxRadius;
        this.radiusIncrement = radiusIncrement;
    }

    @Override
    public void run() {
        lastRadius += radiusIncrement;
        World world = center.getWorld();
        if (lastRadius > maxRadius) {
            if (lingeringIterations++ >= 5) {
                cancel();
                return;
            }

            renderParticles(world);
            return;
        }

        double density = 0.5D;
        if (lastRadius >= (maxRadius / 5) * 3) {
            density = 0.7D;
        }

        lastPoints = ParticlePlanner.getHollowCubePoints(center, lastRadius, density);
        renderParticles(world);
    }

    private void renderParticles(World world) {
        lastPoints.forEach(location -> world.spawnParticle(Particle.VILLAGER_ANGRY, location, 1));
    }
}
