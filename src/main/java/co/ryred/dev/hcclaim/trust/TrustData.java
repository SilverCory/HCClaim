package co.ryred.dev.hcclaim.trust;

import co.ryred.dev.hcclaim.HCClaimPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class TrustData {

    final File configFile;
    final YamlConfiguration config;

    public TrustData(HCClaimPlugin plugin, UUID owner) {
        this.configFile = new File(new File(plugin.getDataFolder(), "userdata"), owner.toString() + ".yml");
        this.configFile.getParentFile().mkdirs();
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void setTrusteeOn(UUID owner, boolean allowed) {
        getConfig().set(owner.toString(), allowed);
    }

    public boolean isTrusteeOn(UUID owner) {
        return getConfig().getBoolean(owner.toString());
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
}
