package me.antritus.astraldupe.utils;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ShulkerBoxUtils {
	/**
	 * Gets shulker block state of given shulker box
	 * @param itemStack item
	 * @return returns shulker state if item is a shulker, else null
	 */
	@Nullable
	public static ShulkerBox getShulkerBox(ItemStack itemStack){
		if (!itemStack.getType().toString().endsWith("SHULKER_BOX")){
			return null;
		}
		BlockStateMeta meta = (BlockStateMeta) itemStack.getItemMeta();
		BlockState state = meta.getBlockState();
		return (ShulkerBox) state;
	}

	/**
	 * Gets inventory of given shulker box
	 * @param itemStack item
	 * @return returns shulker inventory if item is a shulker, else null
	 */
	@SuppressWarnings("DataFlowIssue")
	@Nullable
	public static Inventory getInventory(ItemStack itemStack){
		return getShulkerBox(itemStack) != null ? getShulkerBox(itemStack).getInventory() : null;
	}
	/**
	 * Gets items of given shulker box
	 * @param itemStack item
	 * @return returns shulker inventory items if item is a shulker, else null
	 */
	@Nullable
	public static List<ItemStack> getItems(ItemStack itemStack){
		ShulkerBox box = getShulkerBox(itemStack);
		if (box == null){
			return null;
		}
		return Arrays.stream(box.getInventory().getContents()).filter(Objects::nonNull).toList();
	}
	/**
	 * Gets items of given shulker box. Ignoring air
	 * @param itemStack item
	 * @return returns shulker inventory items if item is a shulker, else null
	 */
	@Nullable
	public static List<ItemStack> getItemsIgnoreAir(ItemStack itemStack){
		ShulkerBox box = getShulkerBox(itemStack);
		if (box == null){
			return null;
		}
		List<ItemStack> items = Arrays.stream(box.getInventory().getContents()).filter(Objects::nonNull).toList();
		items = items.stream().filter(item->item.getType()==Material.AIR).collect(Collectors.toList());
		return items;
	}
}
