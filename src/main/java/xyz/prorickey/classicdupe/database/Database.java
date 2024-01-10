package xyz.prorickey.classicdupe.database;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import xyz.prorickey.classicdupe.ClassicDupe;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Database {

    static Connection playerConn;
    static Connection serverConn;
    static Connection linkingConn;
    static Connection homesConn;
    static Connection bountyConn;
    @Getter
    private final FilterDatabase filterDatabase;
    @Getter
    private final PlayerDatabase playerDatabase;
    @Getter
    private final LinkingDatabase linkingDatabase;
    @Getter
    private final HomesDatabase homesDatabase;

    public Database() {
        try {
                playerConn = DriverManager.getConnection ("jdbc:h2:" + ClassicDupe.getInstance().getDataFolder().getAbsolutePath() + File.separator + "playerData");
                serverConn = DriverManager.getConnection ("jdbc:h2:" + ClassicDupe.getInstance().getDataFolder().getAbsolutePath() + File.separator + "serverData");
                linkingConn = DriverManager.getConnection ("jdbc:h2:" + ClassicDupe.getInstance().getDataFolder().getAbsolutePath() + File.separator + "linkData");
                homesConn = DriverManager.getConnection ("jdbc:h2:" + ClassicDupe.getInstance().getDataFolder().getAbsolutePath() + File.separator + "homes");
                bountyConn = DriverManager.getConnection ("jdbc:h2:" + ClassicDupe.getInstance().getDataFolder().getAbsolutePath() + File.separator + "bountyData");

                playerConn.prepareStatement("CREATE TABLE IF NOT EXISTS players(" +
                        "uuid varchar, name varchar, nickname varchar, " +
                        "timesjoined long, playtime long, randomitem BOOLEAN, " +
                        "night BOOLEAN, deathmessages BOOLEAN, mutepings BOOLEAN, killStreak INT)").execute();
                serverConn.prepareStatement("CREATE TABLE IF NOT EXISTS filter(text varchar, fullword BOOLEAN)").execute();
                serverConn.prepareStatement("CREATE TABLE IF NOT EXISTS spawn(spawn varchar, x DOUBLE, y DOUBLE, z DOUBLE, pitch FLOAT, yaw FLOAT, world varchar)").execute();
                playerConn.prepareStatement("CREATE TABLE IF NOT EXISTS stats(uuid VARCHAR, kills INT, deaths INT)").execute();
                playerConn.prepareStatement("CREATE TABLE IF NOT EXISTS particleEffects(uuid VARCHAR, killEffect VARCHAR, particleEffect VARCHAR)").execute();
                linkingConn.prepareStatement("CREATE TABLE IF NOT EXISTS link(uuid VARCHAR, dscid Long)").execute();
                homesConn.prepareStatement("CREATE TABLE IF NOT EXISTS homes(uuid VARCHAR, name VARCHAR, world VARCHAR, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT)").execute();
                bountyConn.prepareStatement("CREATE TABLE IF NOT EXISTS bounties(uuid VARCHAR, amount BIGINT)").execute();

                filterDatabase = new FilterDatabase(serverConn);
                playerDatabase = new PlayerDatabase(playerConn);
                linkingDatabase = new LinkingDatabase(linkingConn);
                homesDatabase = new HomesDatabase(homesConn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        loadSpawns();
    }

    public Map<String, Location> spawns = new HashMap<>();

    public void setSpawn(String name, Location loc) {
        spawns.put(name, loc);
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getInstance(), () -> {
            try {
                PreparedStatement stat = serverConn.prepareStatement("INSERT INTO spawn(spawn, x, y, z, pitch, yaw, world) VALUES(?, ?, ?, ?, ?, ?, ?)");
                stat.setString(1, name);
                stat.setDouble(2, loc.getX());
                stat.setDouble(3, loc.getY());
                stat.setDouble(4, loc.getZ());
                stat.setFloat(5, loc.getPitch());
                stat.setFloat(6, loc.getYaw());
                stat.setString(7, loc.getWorld().getName());
                stat.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void loadSpawns() {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getInstance(), () -> {
            try {
                ResultSet set = serverConn.prepareStatement("SELECT * FROM spawn").executeQuery();
                while(set.next()) {
                    spawns.put(
                            set.getString("spawn"),
                            new Location(
                                    Bukkit.getWorld(set.getString("world")),
                                    set.getDouble("x"),
                                    set.getDouble("y"),
                                    set.getDouble("z"),
                                    set.getFloat("yaw"),
                                    set.getFloat("pitch")
                            )
                    );
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Location getSpawn(String name) {
        return spawns.get(name);
    }

}
