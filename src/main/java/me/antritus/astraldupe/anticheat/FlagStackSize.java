package me.antritus.astraldupe.anticheat;

import me.antritus.astraldupe.AstralDupe;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class FlagStackSize extends Flag implements Listener {
	public FlagStackSize() {
		super("Stack Item", 20, false);
		AstralDupe.getInstance().register(this);
	}


	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		if (event.getInventory() instanceof PlayerInventory inventory) {
			if (inventory.getItemInOffHand().getAmount() > inventory.getItemInOffHand().getMaxStackSize()) {
				ItemStack item = inventory.getItemInOffHand();
				item.setAmount(inventory.getItemInOffHand().getMaxStackSize());
				inventory.setItemInOffHand(item);
				flag((Player) event.getPlayer());
			}
		}
		Inventory inventory = event.getInventory();
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack item = inventory.getItem(i);
			if (item == null) {
				continue;
			}
			item = item.clone();
			if (item.getAmount() > item.getMaxStackSize()) {
				item.setAmount(item.getMaxStackSize());
				inventory.setItem(i, item);
				flag((Player) event.getPlayer());
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		PlayerInventory inventory = event.getPlayer().getInventory();
		if (inventory.getItemInOffHand().getAmount() > inventory.getItemInOffHand().getMaxStackSize()) {
			ItemStack item = inventory.getItemInOffHand();
			item.setAmount(inventory.getItemInOffHand().getMaxStackSize());
			inventory.setItemInOffHand(item);
			flag(event.getPlayer());
		}
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack item = inventory.getItem(i);
			if (item == null) {
				continue;
			}
			item = item.clone();
			if (item.getAmount() > item.getMaxStackSize()) {
				item.setAmount(item.getMaxStackSize());
				inventory.setItem(i, item);
				flag(event.getPlayer());
			}
		}
	}
}