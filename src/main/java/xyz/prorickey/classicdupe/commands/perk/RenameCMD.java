package xyz.prorickey.classicdupe.commands.perk;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

public class RenameCMD extends AstralCommand{
    public RenameCMD(AstralDupe main) {
        super(main, "rename");
        setPermission("astraldupe.perk.rename");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if(p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
            p.sendMessage(Utils.cmdMsg("<red>You must be holding an item to rename it"));
            return true;
        }
        if(args.length < 1) {
            sender.sendMessage(Utils.cmdMsg("<red>You must include a name to rename your item to"));
            return true;
        }
        StringBuilder msg = new StringBuilder();
        for (String arg : args) msg.append(arg).append(" ");
        p.getInventory().getItemInMainHand().editMeta(meta -> meta.displayName(Utils.format(Utils.convertColorCodesToAdventure(msg.toString()))));
        p.sendMessage(Utils.cmdMsg("<green>Renamed the item in your hand to " + Utils.convertColorCodesToAdventure(msg.toString())));
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
