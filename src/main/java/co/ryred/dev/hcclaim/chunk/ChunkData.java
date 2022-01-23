package co.ryred.dev.hcclaim.chunk;

import co.ryred.dev.hcclaim.HCClaimPlugin;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

public class ChunkData {

    final File configFile;
    final YamlConfiguration config;

    public ChunkData(HCClaimPlugin plugin, String worldName, int x, int z) {
        this.configFile = new File(new File(new File(plugin.getDataFolder(), "chunkdata"), worldName), x + "_" + z + ".yml");
        this.configFile.getParentFile().mkdirs();
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void setProtection(Collection<Location> protectedBlocks, UUID owner) {
        protectedBlocks.forEach(location -> {
            UUID currentOwner = getOwner(location);
            if (currentOwner == null || currentOwner.equals(owner))
                getConfig().set(getLocationKey(location), owner == null ? null : owner.toString());
        });
    }

    public UUID getOwner(Location location) {
        String ownerStr = getConfig().getString(getLocationKey(location));
        if (ownerStr == null || ownerStr.isEmpty()) {
            return null;
        }

        return UUID.fromString(ownerStr);
    }

    public void save() {
        try {
            getConfig().save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private YamlConfiguration getConfig() {
        return this.config;
    }

    private String getLocationKey(Location location) {
        return location.getWorld().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
    }
}
