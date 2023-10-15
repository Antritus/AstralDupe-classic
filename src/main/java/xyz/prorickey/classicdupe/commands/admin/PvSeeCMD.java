package xyz.prorickey.classicdupe.commands.admin;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.utils.PlayerUtils;
import me.antritus.astraldupe.commands.AstralCommand; // Updated import
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PvSeeCMD extends AstralCommand implements Listener {

    public static final Map<String, Inventory> vaultGuis = new HashMap<>();

    public PvSeeCMD(AstralDupe main) {
        super(main, "pvsee");
        setPermission("astraldupe.admin.pvsee");
        main.register(this);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute that command from console"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Utils.cmdMsg("<red>You must specify a PV number & player"));
            return true;
        }

        Player target = Bukkit.getServer().getPlayer(args[1]);
        if (target != null && target.hasPlayedBefore()) {
            Map<Integer, ItemStack> vaultMap = ClassicDupe.getPVDatabase().getVault(target.getUniqueId().toString(), Integer.parseInt(args[0]));
            if (vaultMap == null) {
                sender.sendMessage(Utils.cmdMsg("<red>That player doesn't have a vault under that ID!"));
                return true;
            }
            sender.sendMessage(Utils.cmdMsg("<green>Opening vault #" + args[0] + " for " + args[1]));

            Inventory vaultGUI = Bukkit.createInventory(new CustomInvData(target.getUniqueId().toString(), Integer.parseInt(args[0])), 54, Utils.cmdMsg("<green>Opening vault #" + args[0] + " for " + args[1]));
            vaultMap.forEach(vaultGUI::setItem);
            p.openInventory(vaultGUI);
            vaultGuis.put(target.getUniqueId().toString(), vaultGUI);
            return true;
        }
        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("1", "2"); // Add more options as needed
        } else if (args.length == 2) {
            return PlayerUtils.getVisiblePlayerNames(sender);
        }
        return new ArrayList<>();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof CustomInvData)) return;
        if (!e.getInventory().equals(vaultGuis.get(e.getPlayer().getUniqueId().toString()))) return;

        vaultGuis.remove(((CustomInvData) e.getInventory().getHolder()).uuid);
        String name = PlainTextComponentSerializer.plainText().serialize(e.getView().title());
        int vault = ((CustomInvData) e.getInventory().getHolder()).vault;
        ClassicDupe.getPVDatabase().setVault(e.getPlayer().getUniqueId().toString(), vault, e.getInventory());
        e.getPlayer().sendMessage(Utils.cmdMsg("<green>Saved vault #" + vault + " for " + name));
    }

    public static class CustomInvData implements InventoryHolder {
        public String uuid;
        public String type = "pvsee";
        public int vault;

        public CustomInvData(String uuid, int vault) {
            this.uuid = uuid;
            this.vault = vault;
        }

        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }
}
