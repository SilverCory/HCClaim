package co.ryred.dev.hcclaim;

import co.ryred.dev.hcclaim.chunk.ChunkListener;
import co.ryred.dev.hcclaim.chunk.ChunkManager;
import co.ryred.dev.hcclaim.protection.BeaconListener;
import co.ryred.dev.hcclaim.protection.ProtectionListener;
import co.ryred.dev.hcclaim.trust.TrustCommand;
import co.ryred.dev.hcclaim.trust.TrustListener;
import co.ryred.dev.hcclaim.trust.TrustManager;
import co.ryred.dev.hcclaim.trust.TrustTabComplete;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class HCClaimPlugin extends JavaPlugin {

    private ChunkManager chunkManager;
    private TrustManager trustManager;

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        this.chunkManager = new ChunkManager(this);
        this.trustManager = new TrustManager(this);

        getServer().getPluginManager().registerEvents(new TrustListener(this.trustManager), this);
        getServer().getPluginManager().registerEvents(new ChunkListener(this, this.chunkManager), this);
        getServer().getPluginManager().registerEvents(new BeaconListener(this), this);
        getServer().getPluginManager().registerEvents(new ProtectionListener(this), this);


        TrustTabComplete tabComplete = new TrustTabComplete(this);
        setExecutors("trust", new TrustCommand(this, true), tabComplete);
        setExecutors("untrust", new TrustCommand(this, false), tabComplete);
    }

    @Override
    public void onDisable() {
        this.chunkManager.unloadChunks();
        this.chunkManager = null;
        this.trustManager.unloadTrustees();
        this.trustManager = null;
    }

    public ChunkManager getChunkManager() {
        return this.chunkManager;
    }

    public TrustManager getTrustManager() {
        return this.trustManager;
    }

    public void setExecutors(String name, CommandExecutor exec, TabCompleter completer) {
        PluginCommand cmd = Objects.requireNonNull(getCommand(name));
        cmd.setExecutor(exec);
        cmd.setTabCompleter(completer);
    }
}
