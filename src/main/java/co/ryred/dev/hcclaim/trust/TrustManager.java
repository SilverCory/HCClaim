package co.ryred.dev.hcclaim.trust;

import co.ryred.dev.hcclaim.HCClaimPlugin;
import org.bukkit.Chunk;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TrustManager {

    private final HCClaimPlugin plugin;
    private final Map<UUID, TrustData> trustDataMap;

    public TrustManager(HCClaimPlugin plugin) {
        this.plugin = plugin;
        trustDataMap = new HashMap<>();
    }

    public void setTrusted(UUID trustee, UUID owner, boolean trusted) {
        TrustData trustData = trustDataMap.get(trustee);
        boolean unload = false;
        if (trustData == null) {
            trustData = loadTrustee(trustee);
            unload = true;
        }

        trustData.setTrusteeOn(owner, trusted);
        if (unload) unloadTrustee(trustee);
    }

    public void setTrusted(Collection<UUID> trustees, UUID owner, boolean trusted) {
        trustees.forEach(uuid -> setTrusted(uuid, owner, trusted));
    }

    public TrustData getTrustData(UUID trustee) {
        return trustDataMap.get(trustee);
    }

    public TrustData loadTrustee(UUID trustee) {
        TrustData trusteeData = new TrustData(plugin, trustee);
        trustDataMap.put(trustee, trusteeData);
        return trusteeData;
    }

    public void unloadTrustee(UUID trustee) {
        TrustData trustData = trustDataMap.remove(trustee);
        if (trustData != null) trustData.save();
    }

    public void unloadTrustees() {
        trustDataMap.forEach((s, trustData) -> trustData.save());
        trustDataMap.clear();
    }

    private String getChunkStringKey(Chunk chunk) {
        return chunk.getWorld().getName() + "." + chunk.getX() + "." + chunk.getZ();
    }
}
