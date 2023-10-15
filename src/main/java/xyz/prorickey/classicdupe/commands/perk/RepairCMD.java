package xyz.prorickey.classicdupe.commands.perk;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.Utils;

import java.util.Collections;
import java.util.List;

public class RepairCMD extends AstralCommand {

    public RepairCMD(AstralDupe main) {
        super(main, "repair");
        setPermission("astraldupe.perk.repair");
        setAliases(List.of("fix"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        p.getInventory().forEach(item -> {
            if (item != null) {
                ItemMeta meta = item.getItemMeta();
                if (meta instanceof Damageable dmeta) dmeta.setDamage(0);
                item.setItemMeta(meta);
            }
        });
        p.sendMessage(Utils.cmdMsg("<green>Repaired all items in your inventory"));
        return true;
    }

    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
