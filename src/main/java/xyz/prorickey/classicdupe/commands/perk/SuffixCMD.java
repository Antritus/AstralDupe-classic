package xyz.prorickey.classicdupe.commands.perk;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.commands.AstralCommand;
import net.luckperms.api.node.types.SuffixNode;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SuffixCMD extends AstralCommand implements Listener {

    public static final Map<String, String> suffixes = new HashMap<>();
    public static final Map<String, Inventory> guis = new HashMap<>();
    public static final Map<Inventory, Map<Integer, String>> guiData = new HashMap<>();

    public SuffixCMD(AstralDupe main) {
        super(main, "suffix");
        setPermission("astraldupe.perk.suffix");
        setAliases(List.of("suffixes", "tag", "tags"));
        main.register(this);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("clear")) {
                if(ClassicDupe.getLuckPerms().getUserManager().getUser(p.getUniqueId()).getCachedData().getMetaData().getSuffix() == null) {
                    p.sendMessage(Utils.cmdMsg("<red>You have no suffix to clear"));
                    return true;
                }
                ClassicDupe.getLuckPerms().getUserManager().modifyUser(p.getUniqueId(), user -> user.data().remove(SuffixNode.builder(user.getCachedData().getMetaData().getSuffix(), 250).build()));
                p.sendMessage(Utils.cmdMsg("<green>Disabled your suffix"));
                return true;
            }
            if(!suffixes.containsKey(args[0].toLowerCase()) || !p.hasPermission("perks.suffix." + args[0].toLowerCase())) {
                p.sendMessage(Utils.cmdMsg("<red>You do not have that suffix or it does not exist"));
                return true;
            }
            ClassicDupe.getLuckPerms().getUserManager().modifyUser(p.getUniqueId(), user -> {
                if(user.getCachedData().getMetaData().getSuffix() != null) user.data().remove(SuffixNode.builder(user.getCachedData().getMetaData().getSuffix(), 250).build());
                user.data().add(SuffixNode.builder(suffixes.get(args[0].toLowerCase()), 250).build());
            });
            p.sendMessage(Utils.cmdMsg("<green>Enabled the " + suffixes.get(args[0].toLowerCase()) + "<green> suffix"));
        } else {
            Map<String, String> suffixesAccessTo = new HashMap<>();
            suffixes.forEach((name, value) -> {
                if(p.hasPermission("perks.suffix." + name)) suffixesAccessTo.put(name, value);
            });
            int invNum;
            if(suffixesAccessTo.size() <= 9) invNum = 9;
            else if(suffixesAccessTo.size() <= 27) invNum = 27;
            else invNum = 54;
            Inventory gui = Bukkit.createInventory(null, invNum, Utils.format("<blue><b>Suffix Menu"));
            for(int i = 0; i < invNum; i++) gui.setItem(i, new ItemStack(Material.RED_STAINED_GLASS_PANE));
            Map<Integer, String> slotData = new HashMap<>();

            ItemStack clear = new ItemStack(Material.BARRIER);
            clear.editMeta(meta -> meta.displayName(Utils.format("<red>Clear Suffix")));
            gui.setItem(0, clear);

            AtomicInteger i = new AtomicInteger(1);
            suffixesAccessTo.forEach((name, value) -> {
                ItemStack item = new ItemStack(Material.NAME_TAG);
                item.editMeta(meta -> meta.displayName(Utils.format(Utils.convertColorCodesToAdventure(value))));
                gui.setItem(i.get(), item);
                slotData.put(i.get(), name);
                i.getAndIncrement();
            });
            guis.put(p.getUniqueId().toString(), gui);
            guiData.put(gui, slotData);
            p.openInventory(gui);
        }
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(!e.getInventory().equals(guis.get(e.getWhoClicked().getUniqueId().toString()))) return;
        e.setCancelled(true);
        Map<Integer, String> slotData = guiData.get(e.getInventory());
        if(e.getRawSlot() == 0) {
            if(ClassicDupe.getLuckPerms().getUserManager().getUser(e.getWhoClicked().getUniqueId()).getCachedData().getMetaData().getSuffix() == null) return;
            ClassicDupe.getLuckPerms().getUserManager().modifyUser(e.getWhoClicked().getUniqueId(), user -> user.data().remove(SuffixNode.builder(user.getCachedData().getMetaData().getSuffix(), 250).build()));
            e.getWhoClicked().sendMessage(Utils.cmdMsg("<green>Disabled your suffix"));
            e.getInventory().close();
        } else if(slotData.containsKey(e.getRawSlot())) {
            String suffixName = slotData.get(e.getRawSlot());
            ClassicDupe.getLuckPerms().getUserManager().modifyUser(e.getWhoClicked().getUniqueId(), user -> {
                if(user.getCachedData().getMetaData().getSuffix() != null) user.data().remove(SuffixNode.builder(user.getCachedData().getMetaData().getSuffix(), 250).build());
                user.data().add(SuffixNode.builder(suffixes.get(suffixName), 250).build());
            });
            e.getWhoClicked().sendMessage(Utils.cmdMsg("<green>Enabled the " + Utils.convertColorCodesToAdventure(suffixes.get(suffixName)) + "<green> suffix"));
            e.getInventory().close();
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if(!e.getInventory().equals(guis.get(e.getPlayer().getUniqueId().toString()))) return;
        guiData.remove(guis.get(e.getPlayer().getUniqueId().toString()));
        guis.remove(e.getPlayer().getUniqueId().toString());
    }
    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>(suffixes.keySet());
        list.add("clear");
        if(args.length == 1) return list;
        return new ArrayList<>();
    }
}
