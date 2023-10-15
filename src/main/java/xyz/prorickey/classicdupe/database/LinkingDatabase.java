package xyz.prorickey.classicdupe.database;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LinkingDatabase {

    final Connection conn;

    public LinkingDatabase(Connection conn) {
        this.conn = conn;
    }

    public static class Link {
        public final String uuid;
        public final Long id;
        public Link(String uuid1, Long id1) {
            uuid = uuid1;
            id = id1;
        }
    }

    public void unlinkById(Long id) {
        try {
            conn.prepareStatement("DELETE FROM link WHERE dscid=" + id + "").execute();
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
        }
    }

    public void unlinkByUUID(@NotNull String uuid) {
        try {
            conn.prepareStatement("DELETE FROM link WHERE uuid='" + uuid + "'").execute();
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
        }
    }

    public void setLink(@NotNull String uuid, @NotNull Long id) {
        try {
            conn.prepareStatement("DELETE FROM link WHERE uuid='" + uuid + "'").execute();
            conn.prepareStatement("DELETE FROM link WHERE dscid=" + id + "").execute();
            conn.prepareStatement("INSERT INTO link(uuid, dscid) VALUES('" + uuid + "', " + id + ")").execute();
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
        }
    }

    @Nullable
    public Link getLinkFromUUID(@NotNull String uuid) {
        try {
            ResultSet set = conn.prepareStatement("SELECT * FROM link WHERE uuid='" + uuid + "'").executeQuery();
            if(set.next()) return new Link(set.getString("uuid"), set.getLong("dscid"));
            return null;
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
            return null;
        }
    }

    @Nullable
    public Link getLinkFromId(@NotNull Long id) {
        try {
            ResultSet set = conn.prepareStatement("SELECT * FROM link WHERE dscid=" + id).executeQuery();
            if(set.next()) return new Link(set.getString("uuid"), set.getLong("dscid"));
            return null;
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
            return null;
        }
    }

}
