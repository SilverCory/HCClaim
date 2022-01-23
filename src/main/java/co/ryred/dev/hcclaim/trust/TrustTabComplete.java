package co.ryred.dev.hcclaim.trust;

import co.ryred.dev.hcclaim.HCClaimPlugin;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TrustTabComplete implements TabCompleter {

    private final HCClaimPlugin plugin;

    public TrustTabComplete(HCClaimPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> ret = new ArrayList<>();
        if (args.length <= 0) return ret;
        String lastArg = args[args.length - 1].toLowerCase();

        StringUtil.copyPartialMatches(
                lastArg,
                Arrays.stream(plugin.getServer().getOfflinePlayers())
                        .limit(20)
                        .map(OfflinePlayer::getName)
                        .filter(Objects::nonNull)
                        .map(String::toLowerCase)
                        .collect(Collectors.toList()),
                ret
        );

        return ret;
    }
}
