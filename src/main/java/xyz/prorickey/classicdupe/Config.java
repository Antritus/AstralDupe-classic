package xyz.prorickey.classicdupe;

import com.github.antritus.astral.configuration.Configuration;
import me.antritus.astraldupe.AstralDupe;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import xyz.prorickey.classicdupe.commands.default1.DupeCMD;
import xyz.prorickey.classicdupe.events.BlockPlace;

import java.io.File;

public class Config extends Configuration {
    private static Config config;
    private final ClassicDupe astralDupe;

    private Config(ClassicDupe astralDupe) {
        super(astralDupe, new File(astralDupe.getDataFolder(), "config.yml"));
        this.astralDupe = astralDupe;
    }

    public static void reloadConfig() {
        config = new Config(AstralDupe.getInstance());
        Config.getConfig().getStringList("blockFromPlacing").forEach(str -> BlockPlace.bannedToPlaceBcAnnoyingASF.add(Material.valueOf(str.toUpperCase())));
        Config.getConfig().getStringList("forbiddenDupes").forEach(str -> DupeCMD.forbiddenDupes.add(Material.valueOf(str.toUpperCase())));
        Config.getConfig().getStringList("forbiddenDupesInCombat").forEach(str -> DupeCMD.forbiddenDupesInCombat.add(Material.valueOf(str.toUpperCase())));
        Config.getConfig().getStringList("removedItems").forEach(str -> ClassicDupe.randomItems.remove(new ItemStack(Material.valueOf(str.toUpperCase()))));
        ClassicDupe.configuration = config;
    }

    /**
     * Use this method when astral dupe initializes as classic dupe plugin is still needed.
     * When astral dupe can completely detach from the old sources, the normal method can be used
     * @param classicDupe classic dupe
     */
    protected static void reloadConfig(ClassicDupe classicDupe) {
        config = new Config(classicDupe);
        Config.getConfig().getStringList("blockFromPlacing").forEach(str -> BlockPlace.bannedToPlaceBcAnnoyingASF.add(Material.valueOf(str.toUpperCase())));
        Config.getConfig().getStringList("forbiddenDupes").forEach(str -> DupeCMD.forbiddenDupes.add(Material.valueOf(str.toUpperCase())));
        Config.getConfig().getStringList("forbiddenDupesInCombat").forEach(str -> DupeCMD.forbiddenDupesInCombat.add(Material.valueOf(str.toUpperCase())));
        Config.getConfig().getStringList("removedItems").forEach(str -> ClassicDupe.randomItems.remove(new ItemStack(Material.valueOf(str.toUpperCase()))));
        ClassicDupe.configuration = config;
    }
    public static FileConfiguration getConfig() { return config; }

}