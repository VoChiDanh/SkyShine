package net.danh.MMOStats.PlaceholderAPI;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.danh.MMOStats.Manager.PStats;
import net.danh.SkyShine;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Placeholder extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "mmostats";
    }

    @Override
    public @NotNull String getAuthor() {
        return SkyShine.getPlugin().getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player p, @NotNull String i) {
        if (p == null) return "";
        if (i.startsWith("stats_")) {
            String stats = i.substring(6);
            return String.valueOf(PStats.getStats(p, stats));
        }
        return null;
    }
}
