package xyz.prorickey.classicdupe.commands.admin;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.utils.PlayerUtils;
import me.antritus.astraldupe.commands.AstralCommand; // Updated import
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

public class InvseeCMD extends AstralCommand {

    public InvseeCMD(AstralDupe main) {
        super(main, "invsee");
        setPermission("astraldupe.admin.invsee");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if (args.length == 0) {
            p.sendMessage(Utils.cmdMsg("<red>You must include at least 1 argument"));
            return true;
        }
        Player invPlayer = Bukkit.getServer().getPlayer(args[0]);
        if (invPlayer == null || !invPlayer.isOnline()) {
            sender.sendMessage(Utils.cmdMsg("<yellow>" + args[0] + " <red>is not currently online"));
            return true;
        }
        Inventory targetInv = invPlayer.getInventory();
        p.openInventory(targetInv);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return PlayerUtils.getVisiblePlayerNames(sender);
        }
        return new ArrayList<>();
    }
}