package xyz.prorickey.classicdupe.clans.builders;

import me.antritus.astraldupe.ForRemoval;
import org.bukkit.Location;

@ForRemoval(reason = "Clans will be removed fully from the classic dupe plugin.")
@Deprecated(forRemoval = true)
public class Warp {

    public final String name;
    public final Location location;
    public final Integer level;

    public Warp(String name, Location loc, Integer level) {
        this.name = name;
        this.location = loc;
        this.level = level;
    }

}
