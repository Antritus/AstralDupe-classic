package xyz.prorickey.classicdupe.commands.perk;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

public class HatCMD extends AstralCommand {
    public HatCMD(AstralDupe main) {
        super(main, "hat");
        setPermission("astraldupe.perk.hat");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if(p.getInventory().getHelmet() != null) {
            p.sendMessage(Utils.cmdMsg("<red>You cannot wear a hat if you have a helmet on"));
            return true;
        }
        if(p.getInventory().getItemInMainHand() == null || p.getInventory().getItemInMainHand().getType() == Material.AIR) {
            p.sendMessage(Utils.cmdMsg("<red>You must be holding the item you would like to put onto your head"));
            return true;
        }
        p.getInventory().setHelmet(p.getInventory().getItemInMainHand());
        p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        p.sendMessage(Utils.cmdMsg("<green>You are now wearing a hat!"));
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
