package xyz.prorickey.classicdupe.commands.admin;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.utils.PlayerUtils;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

public class HeadCMD extends AstralCommand {

    public HeadCMD(AstralDupe main) {
        super(main, "skull");
        setPermission("astraldupe.admin.skull");
        setAliases(List.of("head"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if (args.length < 1) {
            p.sendMessage(Utils.cmdMsg("<red>You need to provide a player's name"));
            return true;
        }
        OfflinePlayer tarj = Bukkit.getOfflinePlayer(args[0]);
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
        meta.setOwningPlayer(tarj);
        skull.setItemMeta(meta);
        p.getInventory().addItem(skull);
        p.sendMessage(Utils.cmdMsg("<green>Head of <yellow>" + tarj.getName() + "<green> given"));
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