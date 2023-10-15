package xyz.prorickey.classicdupe.metrics;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.database.PlayerData;
import xyz.prorickey.classicdupe.database.PlayerDatabase;

import java.io.File;
import java.sql.*;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMetrics implements Listener {

    private static Connection conn;

    public PlayerMetrics(JavaPlugin plugin) {
        try {
            conn = DriverManager.getConnection("jdbc:h2:" + ClassicDupe.getInstance().getDataFolder().getAbsolutePath() + File.separator + "metrics" + File.separator + "player");
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS player_metrics(" +
                    "uuid VARCHAR, " +
                    "playtime BIGINT" +
                    ")"
            ).execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        new PlaytimeTask().runTaskTimer(plugin, 0, 20);
    }

    @Nullable
    public Long getPlaytime(UUID uuid) {
        try {
            ResultSet set = conn.prepareStatement("SELECT * FROM player_metrics WHERE uuid='" + uuid + "'").executeQuery();
            if(set.next()) {
                return set.getLong("playtime");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public String getPlaytimeFormatted(UUID uuid) {
        Long playtime = getPlaytime(uuid);
        if(playtime == null) return null;
        Duration duration = Duration.ofMillis(playtime);
        long seconds = duration.getSeconds();
        long HH = seconds / 3600;
        long MM = (seconds % 3600) / 60;
        long SS = seconds % 60;
        return String.format("%02d:%02d:%02d", HH, MM, SS);
    }

    public static String getPlaytimeFormatted(Long playtime) {
        Duration duration = Duration.ofMillis(playtime);
        long seconds = duration.getSeconds();
        long HH = seconds / 3600;
        long MM = (seconds % 3600) / 60;
        long SS = seconds % 60;
        return String.format("%02d:%02d:%02d", HH, MM, SS);
    }

    public static class PlaytimeTask extends BukkitRunnable {

        private static final Map<Player, Long> lastUpdate = new HashMap<>();

        @Override
        public void run() {
            Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getInstance(), () -> {
                Bukkit.getOnlinePlayers().forEach(player-> {
                    PreparedStatement stat2 = null;
                    try {
                        stat2 = conn.prepareStatement("UPDATE player_metrics SET playtime=? WHERE uuid=?");
                        long playtime = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
                        stat2.setLong(1, playtime);
                        stat2.setString(2, String.valueOf(player.getUniqueId()));
                        stat2.execute();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                try {
                    ResultSet playtimeSet = conn.prepareStatement("SELECT * FROM player_metrics ORDER BY playtime DESC").executeQuery();
                    for(int i = 0; i < 10; i++) {
                        if(playtimeSet.next()) {
                            PlayerData data = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(UUID.fromString(playtimeSet.getString("uuid")));
                            PlayerDatabase.playtimeLeaderboard.put(i+1, data.name);
                            PlayerDatabase.playtimeLeaderboardP.put(i+1, playtimeSet.getLong("playtime"));
                        }
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }

}
