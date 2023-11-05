package me.antritus.astraldupe.commands;

import me.antritus.astral.fluffycombat.manager.CombatManager;
import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.utils.ColorUtils;
import me.antritus.astraldupe.utils.ShulkerBoxUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DupeCommand extends AstralCommand {
	private final static Map<UUID, Long> cooldown = new LinkedHashMap<>();
	public final static NamespacedKey UNDUPABLE = new NamespacedKey("astraldupe", "undupable");
	public DupeCommand(AstralDupe main) {
		super(main, "dupe");
		setAliases(Collections.singletonList("d"));
		setPermission("astraldupe.dupe");
		permissionMessage(
				ColorUtils.translateComp("Hello!"));
		setUsage("/dupe");
		setDescription("A simple dupe command!");
	}


	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
		if(commandSender instanceof Player player) {
			CombatManager combatManager = AstralDupe.fluffyCombat.getCombatManager();
			if (combatManager.hasTags(player)){
				cooldown.putIfAbsent(player.getUniqueId(), 0L);
				if (cooldown.get(player.getUniqueId())>System.currentTimeMillis()){
					main.getMessageManager().message(player, "dupe.cooldown");
					return true;
				}
				cooldown.put(player.getUniqueId(), System.currentTimeMillis()+333L);
			}
			ItemStack hand = player.getInventory().getItemInMainHand();
			if (hand.getType()==Material.AIR){
				main.messageManager().message(player, "dupe.illegal-item.hand");
				return true;
			}
			if (!checkItem(hand, false)){
				main.getMessageManager().message(player, "dupe.illegal-item.hand");
				return true;
			}
			if (combatManager.hasTags(player)) {
				if (!checkItem(hand, false)) {
					main.getMessageManager().message(player, "dupe.illegal-item.combat");
					return true;
				}
			}
			int amount = 1;
			if(strings.length>0 && isNumeric(strings[0]))
				amount = Integer.parseInt(strings[0]);
			if (amount<1){
				main.getMessageManager().message(commandSender, "dupe.negative");
				return true;
			}
			if (amount>8){
				main.getMessageManager().message(commandSender, "dupe.over-limit");
				return true;
			}
			for (int i = 0; i < amount; i++){
				ItemStack item = player.getInventory().getItemInMainHand().clone();
				player.getInventory().addItem(item);
			}
			if (amount>1){
				main.getMessageManager().message(player, "dupe.super-duper","%times%="+amount);
			}
		} else {
			main.getMessageManager().message(commandSender, "command-parse.player-only");
		}
		return true;
	}


	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {

		List<String> completions = new ArrayList<>();

		completions.add("1");
		completions.add("2");
		completions.add("3");
		completions.add("4");
		completions.add("5");
		completions.add("6");
		completions.add("7");
		completions.add("8");

		return completions;
	}


	private boolean checkCombat(ItemStack itemStack){
		Material material = itemStack.getType();
		return material != Material.TOTEM_OF_UNDYING
				&& material != Material.POTION
				&& material != Material.SPLASH_POTION
				&& material != Material.LINGERING_POTION
				&& material != Material.EXPERIENCE_BOTTLE
				&& material != Material.ENDER_PEARL;
	}
	private boolean checkPotion(ItemStack hand) {
		return true;
	}
	private boolean checkItem(ItemStack itemStack, boolean combat){
		if (itemStack.getType().equals(Material.BUNDLE)){
			return checkBundle(itemStack, combat);
		} else if (ShulkerBoxUtils.getShulkerBox(itemStack) != null){
			return checkShulker(itemStack, combat);
		} else if (combat && !checkCombat(itemStack)) {
			return false;
		}
		return AstralDupe.illegalDupes.stream().noneMatch(material -> material.equals(itemStack.getType()));
	}
	private boolean checkBundle(ItemStack itemStack, boolean combat) {
		if (itemStack.getType() != Material.BUNDLE) {
			return true;
		}
		BundleMeta bundleMeta = (BundleMeta) itemStack.getItemMeta();
		return bundleMeta.getItems().stream().filter(Objects::nonNull).noneMatch(item->this.checkItem(item, combat));
	}
	private boolean checkShulker(ItemStack itemStack, boolean combat){
		// It gets the block form of the item.
		// If the item is not shulker a box, it returns null
		if (itemStack == null){
			return true;
		}
		if (ShulkerBoxUtils.getShulkerBox(itemStack)==null)
			return true;
		List<ItemStack> items = ShulkerBoxUtils.getItemsIgnoreAir(itemStack);
		if (items==null)
			return true;
		for (ItemStack item : items) {
			if (item == null){
				continue;
			}
			if (!checkItem(item, combat)){
				return false;
			}
		}
		return true;
	}



	private static boolean isNumeric(String str){
		return str != null && str.matches("[0-9.]+");
	}
}
