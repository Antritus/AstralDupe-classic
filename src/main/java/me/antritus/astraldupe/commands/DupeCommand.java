package me.antritus.astraldupe.commands;

import com.github.antritus.astral.utils.ColorUtils;
import me.antritus.astral.fluffycombat.manager.CombatManager;
import me.antritus.astraldupe.AstralDupe;
import me.antritus.astrolapi.minecraft.ShulkerBoxUtils;
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
			if (!checkItem(hand)){
				main.getMessageManager().message(player, "dupe.illegal-item.hand");
				return true;
			}
			if (!checkBundle(hand)){
				main.getMessageManager().message(player, "dupe.illegal-item.bundle");
				return true;
			}
			if (!checkShulker(hand)){
				main.getMessageManager().message(player, "dupe.illegal-item.shulker");
				return true;
			}
			if (combatManager.hasTags(player)) {
				if (!checkCombat(hand)) {
					main.getMessageManager().message(player, "dupe.illegal-item.combat");
					return true;
				}
			}
			int cAmount = hand.getAmount();
			if(strings.length == 0) {
				ItemStack newHand = hand.clone();
				int newAmount = getDupeAmount(1, cAmount);
				if (cAmount<64){
					if (cAmount+newAmount>64){
						int left = 64-cAmount;
						newAmount-=left;
						player.getInventory().setItemInMainHand(hand.clone());
						player.getInventory().getItemInMainHand().setAmount(64);
					}
				}
				newHand.setAmount(newAmount);
				player.getInventory().addItem(newHand);
			}
			else {
				if(!isNumeric(strings[0])) return true;
				int dupeAmount = Integer.parseInt(strings[0]);
				if (dupeAmount<1){
					main.getMessageManager().message(commandSender, "dupe.negative");
					return true;
				}
				if (dupeAmount>8){
					main.getMessageManager().message(commandSender, "dupe.over-limit");
					return true;
				}
				ItemStack newHand = hand.clone();
				int newAmount = getDupeAmount(Integer.parseInt(strings[0]), cAmount);
				if (cAmount<64){
					if (cAmount+newAmount>64){
						int left = 64-cAmount;
						newAmount-=left;
						player.getInventory().setItemInMainHand(hand.clone());
						player.getInventory().getItemInMainHand().setAmount(64);
					}
				}
				newHand.setAmount(newAmount);
				player.getInventory().addItem(newHand);
				main.getMessageManager().message(player, "dupe.super-duper","%times%="+dupeAmount);
			}
		} else {
			main.getMessageManager().message(commandSender, "command-parse.player-only");
		}
		//main.getMessageManager().message(commandSender, "example!");
		//main.getMessageManager().message(commandSender, "Hey %placeholder%! This is built-in placeholder from messages.yml: %prefix%", "%placeholder%=Antritus");
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
		return true;
	}
	private boolean checkPotion(ItemStack hand) {
		return true;
	}
	private boolean checkItem(ItemStack itemStack){
		if (itemStack.getType().equals(Material.BUNDLE)){
			return checkBundle(itemStack);
		} else if (ShulkerBoxUtils.getShulkerBox(itemStack) != null){
			return checkShulker(itemStack);
		}
		return AstralDupe.illegalDupes.stream().noneMatch(material -> material.equals(itemStack.getType()));
	}
	private boolean checkBundle(ItemStack itemStack) {
		if (itemStack.getType() != Material.BUNDLE) {
			return true;
		}
		BundleMeta bundleMeta = (BundleMeta) itemStack.getItemMeta();
		return bundleMeta.getItems().stream().filter(Objects::nonNull).noneMatch(this::checkItem);
	}
	private boolean checkShulker(ItemStack itemStack){
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
			if (!checkItem(item)){
				return false;
			}
		}
		return true;
	}


	private int getDupeAmount(int timesDupe, int cAmount) {
		int result = cAmount;

		for (int i = 0; i < timesDupe; i++) {
			result = result * 2;
		}

		result = result - cAmount;

		return result;
	}

	private static boolean isNumeric(String str){
		return str != null && str.matches("[0-9.]+");
	}
}
