package xyz.prorickey.classicdupe.database;

import org.bukkit.Bukkit;
import xyz.prorickey.classicdupe.ClassicDupe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerData {

    private final Connection conn;
    public final UUID uuid;
    public final String name;
    public String nickname;
    public long timesjoined;
    public long playtime;
    public boolean randomitem;
    public boolean night;
    public boolean deathmessages;
    public boolean mutepings;
    public Integer killStreak;

    public PlayerData(Connection conn,
                      UUID uuid1,
                      String name1,
                      String nickname1,
                      long timesjoined1,
                      long playtime1,
                      boolean randomitem1,
                      boolean night1,
                      boolean deathmessages1,
                      boolean mutepings1,
                      Integer killStreak1
    ) {
        this.conn = conn;
        uuid = uuid1;
        name = name1;
        nickname = nickname1;
        timesjoined = timesjoined1;
        playtime = playtime1;
        randomitem = randomitem1;
        night = night1;
        deathmessages = deathmessages1;
        mutepings = mutepings1;
        killStreak = killStreak1;
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public String getNickname() { return nickname; }
    public long getTimesJoined() { return timesjoined; }
    public long getPlaytime() { return playtime; }
    public boolean isRandomItem() { return randomitem; }
    public boolean isNight() { return night; }
    public boolean getDeathMessages() { return deathmessages; }
    public boolean getMutePings() { return mutepings; }
    public Integer getKillStreak() { return killStreak; }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getInstance(), () -> {
            try {
                PreparedStatement stat = conn.prepareStatement("UPDATE players SET nickname=? WHERE uuid=?");
                stat.setString(1, nickname);
                stat.setString(2, this.uuid.toString());
                stat.execute();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    public void resetNickname() {
        this.nickname = this.name;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getInstance(), () -> {
            try {
                PreparedStatement stat = conn.prepareStatement("UPDATE players SET nickname=null WHERE uuid=?");
                stat.setString(1, this.uuid.toString());
                stat.execute();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    public void enableRandomItem() {
        this.randomitem = true;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getInstance(), () -> {
            try {
                PreparedStatement stat = conn.prepareStatement("UPDATE players SET randomitem=true WHERE uuid=?");
                stat.setString(1, this.uuid.toString());
                stat.execute();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    public Boolean swapRandomItem() {
        if(this.randomitem) {
            disableRandomItem();
            return false;
        } else {
            enableRandomItem();
            return true;
        }
    }

    public void disableRandomItem() {
        this.randomitem = false;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getInstance(), () -> {
            try {
                PreparedStatement stat = conn.prepareStatement("UPDATE players SET randomitem=false WHERE uuid=?");
                stat.setString(1, this.uuid.toString());
                stat.execute();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    public void setNightVision(boolean value) {
        this.night = value;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getInstance(), () -> {
            try {
                PreparedStatement stat = conn.prepareStatement("UPDATE players SET night=? WHERE uuid=?");
                stat.setBoolean(1, value);
                stat.setString(2, this.uuid.toString());
                stat.execute();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    public void setDeathMessages(boolean deathMessages) {
        this.deathmessages = deathMessages;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getInstance(), () -> {
            try {
                PreparedStatement statement = conn.prepareStatement("UPDATE players SET deathMessages=? WHERE uuid=?");
                statement.setBoolean(1, deathMessages);
                statement.setString(2, this.uuid.toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    public void setMutePings(boolean mutePings) {
        this.mutepings = mutePings;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getInstance(), () -> {
            try {
                PreparedStatement statement = conn.prepareStatement("UPDATE players SET mutepings=? WHERE uuid=?");
                statement.setBoolean(1, mutePings);
                statement.setString(2, this.uuid.toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    public void setKillStreak(Integer killStreak) {
        this.killStreak = killStreak;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getInstance(), () -> {
            try {
                PreparedStatement statement = conn.prepareStatement("UPDATE players SET killstreak=? WHERE uuid=?");
                statement.setInt(1, killStreak);
                statement.setString(2, this.uuid.toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    public void addKillStreak(Integer add) {
        this.killStreak += add;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getInstance(), () -> {
            try {
                PreparedStatement statement = conn.prepareStatement("UPDATE players SET killstreak=killstreak+? WHERE uuid=?");
                statement.setInt(1, add);
                statement.setString(2, this.uuid.toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

}
