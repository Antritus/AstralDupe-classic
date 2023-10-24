package xyz.prorickey.classicdupe;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.database.PlayerData;
import xyz.prorickey.classicdupe.database.PlayerDatabase;
import xyz.prorickey.classicdupe.metrics.PlayerMetrics;

@ApiStatus.OverrideOnly
public class ClassicDupeExpansion extends PlaceholderExpansion {

    public ClassicDupeExpansion(ClassicDupe plugin) {
    }

    @Override
    public @NotNull String getIdentifier() { return "classicdupe"; }

    @Override
    public @NotNull String getAuthor() { return "Prorickey"; }

    @Override
    public @NotNull String getVersion() { return "1.0"; }

    @Override
    public boolean persist() { return true; }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if(params.equalsIgnoreCase("name")) {
            if(player == null) return null;
            PlayerData data = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(player.getUniqueId());
            if(data.getNickname() == null) return player.getName();
            else return Utils.convertAdventureToColorCodes(data.getNickname());
        }
        if(params.toLowerCase().startsWith("top_kills_")) {
            for (int i = 0; i < 10; i++) {
                if (PlayerDatabase.killsLeaderboard.size() < i + 1) return " ";
                if (params.equalsIgnoreCase("top_kills_" + (i + 1) + "_name"))
                    return PlayerDatabase.killsLeaderboard.get(i + 1);
                else if (params.equalsIgnoreCase("top_kills_" + (i + 1) + "_kills"))
                    return PlayerDatabase.killsLeaderboardK.get(i + 1).toString();
            }
        } else if(params.toLowerCase().startsWith("top_deaths_")) {
            for(int i2 = 0; i2 < 10; i2++) {
                if(PlayerDatabase.deathsLeaderboard.size() < i2+1) return " ";
                if(params.equalsIgnoreCase("top_deaths_" + (i2+1) + "_name")) return PlayerDatabase.deathsLeaderboard.get(i2+1);
                else if(params.equalsIgnoreCase("top_deaths_" + (i2+1) + "_deaths")) return PlayerDatabase.deathsLeaderboardD.get(i2+1).toString();
            }
        }
        else if(params.toLowerCase().startsWith("top_playtime_")) {
            for(int i5 = 0; i5 < 10; i5++) {
                if(PlayerDatabase.playtimeLeaderboard.size() < i5+1) return " ";
                if(params.equalsIgnoreCase("top_playtime_" + (i5+1) + "_name")) return PlayerDatabase.playtimeLeaderboard.get(i5+1);
                else if(params.equalsIgnoreCase("top_playtime_" + (i5+1) + "_playtime")) return PlayerMetrics.getPlaytimeFormatted(PlayerDatabase.playtimeLeaderboardP.get(i5+1));
            }
        } else if(params.toLowerCase().startsWith("top_killstreak_")) {
            for(int i6 = 0; i6 < 10; i6++) {
                if(PlayerDatabase.killStreakLeaderboard.size() < i6+1) return " ";
                if(params.equalsIgnoreCase("top_killstreak_" + (i6+1) + "_name")) return PlayerDatabase.killStreakLeaderboard.get(i6+1);
                else if(params.equalsIgnoreCase("top_killstreak_" + (i6+1) + "_killstreak")) return PlayerDatabase.killStreakLeaderboardK.get(i6+1).toString();
            }
        }
        return null;
    }

}
