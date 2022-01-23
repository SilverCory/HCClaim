package co.ryred.dev.hcclaim.trust;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TrustListener implements Listener {

    private final TrustManager trustManager;

    public TrustListener(TrustManager trustManager) {
        this.trustManager = trustManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent e) {
        trustManager.loadTrustee(e.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent e) {
        trustManager.unloadTrustee(e.getPlayer().getUniqueId());
    }

}
