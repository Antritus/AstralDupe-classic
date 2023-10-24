package xyz.prorickey.classicdupe.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.ClassicDupe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class HomesDatabase {

    public final Connection conn;
    Map<Player, Map<String, Location>> homes = new HashMap<>();

    public HomesDatabase(Connection conn) {
        this.conn = conn;
    }

    public void addHome(Player player, String home, Location location) {
        if(homes.containsKey(player)) homes.get(player).put(home, location);
        else loadPlayer(player);
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getInstance(), () -> {
            try {
                PreparedStatement stat1 = conn.prepareStatement("SELECT * FROM homes WHERE uuid=? AND name=?");
                stat1.setString(1, player.getUniqueId().toString());
                stat1.setString(2, home);
                ResultSet set = stat1.executeQuery();
                if(set.next()) {
                    PreparedStatement stat = conn.prepareStatement("UPDATE homes SET world=?, x=?, y=?, z=?, yaw=?, pitch=? WHERE uuid=? AND name=?");
                    stat.setString(1, location.getWorld().getName());
                    stat.setDouble(2, location.getX());
                    stat.setDouble(3, location.getY());
                    stat.setDouble(4, location.getZ());
                    stat.setFloat(5, location.getYaw());
                    stat.setFloat(6, location.getPitch());
                    stat.setString(7, player.getUniqueId().toString());
                    stat.setString(8, home);
                    stat.execute();
                } else {
                    PreparedStatement stat = conn.prepareStatement("INSERT INTO homes(uuid, name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
                    stat.setString(1, player.getUniqueId().toString());
                    stat.setString(2, home);
                    stat.setString(3, location.getWorld().getName());
                    stat.setDouble(4, location.getX());
                    stat.setDouble(5, location.getY());
                    stat.setDouble(6, location.getZ());
                    stat.setFloat(7, location.getYaw());
                    stat.setFloat(8, location.getPitch());
                    stat.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void delHome(Player player, String name) {
        if(homes.containsKey(player)) homes.get(player).remove(name);
        else loadPlayer(player);
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getInstance(), () -> {
            try {
                PreparedStatement stat = conn.prepareStatement("DELETE FROM homes WHERE uuid=? AND name=?");
                stat.setString(1, player.getUniqueId().toString());
                stat.setString(2, name);
                stat.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public Location getHome(Player player, String home) {
        if(!homes.containsKey(player)) return null;
        return homes.get(player).get(home);
    }

    public Map<String, Location> getHomes(UUID uuid) {
        Optional<Player> pHomes = homes.keySet().stream().filter(player -> player.getUniqueId().equals(uuid)).findFirst();
        if(pHomes.isPresent()) return homes.get(pHomes.get());
        try {
            PreparedStatement stat = conn.prepareStatement("SELECT * FROM homes WHERE uuid=?");
            stat.setString(1, uuid.toString());
            ResultSet set = stat.executeQuery();
            Map<String, Location> playerHomes = new HashMap<>();
            while(set.next()) {
                playerHomes.put(
                        set.getString("name"),
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
            return playerHomes;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void unloadPlayer(Player player) {
        homes.remove(player);
    }

    public void loadPlayer(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getInstance(), () -> {
            try {
                PreparedStatement stat = conn.prepareStatement("SELECT * FROM homes WHERE uuid=?");
                stat.setString(1, player.getUniqueId().toString());
                ResultSet set = stat.executeQuery();
                Map<String, Location> playerHomes = new HashMap<>();
                while(set.next()) {
                    playerHomes.put(
                            set.getString("name"),
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
                homes.put(player, playerHomes);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

}