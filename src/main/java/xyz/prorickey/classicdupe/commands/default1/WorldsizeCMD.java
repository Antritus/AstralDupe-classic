package xyz.prorickey.classicdupe.commands.default1;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WorldsizeCMD extends AstralCommand {

    private static long overworldSize;
    private static long netherSize;
    private static long endSize;

    public WorldsizeCMD(AstralDupe astralDupe) {
        super(astralDupe, "worldsize");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        sender.sendMessage(Utils.cmdMsg("<yellow>Fetching World Sizes..."));
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getInstance(), () -> {
            overworldSize = Utils.size(Paths.get(System.getProperty("user.dir"), "/worlds/world/"));
            netherSize = Utils.size(Paths.get(System.getProperty("user.dir"), "/worlds/nether/")); // Added missing netherSize calculation
            endSize = Utils.size(Paths.get(System.getProperty("user.dir"), "/worlds/end/"));
            sender.sendMessage(Utils.cmdMsg("<yellow>Overworld Size: <white>" + Math.round((float) overworldSize / 1000000) + " MB"));
            sender.sendMessage(Utils.cmdMsg("<yellow>Nether Size: <white>" + Math.round((float) netherSize / 1000000) + " MB"));
            sender.sendMessage(Utils.cmdMsg("<yellow>End Size: <white>" + Math.round((float) endSize / 1000000) + " MB"));
        });
        return true;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
