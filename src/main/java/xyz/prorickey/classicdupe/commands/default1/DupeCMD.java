package xyz.prorickey.classicdupe.commands.default1;

import me.antritus.astral.fluffycombat.FluffyCombat;
import me.antritus.astral.fluffycombat.manager.CombatManager;
import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.ForRemoval;
import me.antritus.astraldupe.commands.AstralCommand;
import me.antritus.astraldupe.commands.DupeCommand;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


@Deprecated
@ForRemoval(reason = "Implement older astral command")
public class DupeCMD extends AstralCommand {

    public static final List<Material> forbiddenDupes = new ArrayList<>();
    public static final List<Material> forbiddenDupesInCombat = new ArrayList<>();
    public static final NamespacedKey undupableKey = DupeCommand.UNDUPABLE;

    public DupeCMD(AstralDupe astralDupe) {
        super(astralDupe, "dupe");
    }

    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        int dupeNum = 1;
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if (!checkDupable(p.getInventory().getItemInMainHand())) {
            p.sendMessage(Utils.cmdMsg("<red>That item is undupable"));
            return true;
        }
        FluffyCombat fluffyCombat = AstralDupe.fluffyCombat;
        CombatManager combatManager = fluffyCombat.getCombatManager();
        if (combatManager.hasTags(p) && forbiddenDupesInCombat.contains(p.getInventory().getItemInMainHand().getType())) {
            p.sendMessage(Utils.cmdMsg("<red>You cannot dupe that item while in combat"));
            return true;
        }
        if (combatManager.hasTags(p) &&
                p.getInventory().getItemInMainHand().getItemMeta() instanceof PotionMeta potionMeta &&
                (potionMeta.getBasePotionData().getType().equals(PotionType.INSTANT_HEAL) ||
                        potionMeta.getBasePotionData().getType().equals(PotionType.REGEN))) {
            p.sendMessage(Utils.cmdMsg("<red>You cannot dupe that item while in combat"));
            return true;
        }
        if (args.length > 0) {
            try {
                dupeNum = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
            }
            if (dupeNum < 1) dupeNum = 1;
            if (dupeNum > 6) dupeNum = 6;
        }
        for (int i = 0; i < dupeNum; i++) p.getInventory().addItem(p.getInventory().getItemInMainHand());
        return true;
    }

    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("1", "2", "3", "4", "5", "6");
        }
        return new ArrayList<>();
    }

    public static final List<Material> shulkerBoxes = Arrays.stream(Material.values())
            .filter(material -> material.isBlock() && material.name().contains("SHULKER_BOX"))
            .collect(Collectors.toList());

    public static Boolean checkDupable(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return true;
        if (forbiddenDupes.contains(item.getType())) return false;
        if (Boolean.TRUE.equals(item.getItemMeta().getPersistentDataContainer().get(undupableKey, PersistentDataType.BOOLEAN)))
            return false;
        if (shulkerBoxes.contains(item.getType())) {
            AtomicBoolean illegal = new AtomicBoolean(false);
            ShulkerBox box = (ShulkerBox) ((BlockStateMeta) item.getItemMeta()).getBlockState();
            box.getInventory().forEach(itemStack -> {
                if (!checkDupable(itemStack)) illegal.set(true);
            });
            return !illegal.get();
        }
        if (item.getType().equals(Material.BUNDLE)) {
            AtomicBoolean illegal = new AtomicBoolean(false);
            BundleMeta bundle = (BundleMeta) item.getItemMeta();
            bundle.getItems().forEach(itemStack -> {
                if (!checkDupable(itemStack)) illegal.set(true);
            });
            return !illegal.get();
        }
        return !(item.getItemMeta() instanceof ArmorMeta armorMeta) || !armorMeta.hasTrim();
    }
}
