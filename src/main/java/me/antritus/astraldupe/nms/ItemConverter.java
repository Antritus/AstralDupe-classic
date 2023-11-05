package me.antritus.astraldupe.nms;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ItemConverter {
	private static final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	private static final Class<?> craftItemStack;
	private static final Method asNMSCopy;
	private static final Method asBukkitCopy;

	static {
		try {
			craftItemStack = Class.forName("org.bukkit.craftbukkit."+getVersion()+".inventory.CraftItemStack");
			asNMSCopy = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
			asBukkitCopy = craftItemStack.getMethod("asBukkitCopy", net.minecraft.world.item.ItemStack.class);
		} catch (ClassNotFoundException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public static CompoundTag tagsNBT(ItemStack itemStack){
		net.minecraft.world.item.ItemStack nmsItem = null;
		try {
			nmsItem = (net.minecraft.world.item.ItemStack) asNMSCopy.invoke(null, itemStack);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Insufficient permissions to use "+craftItemStack.getName()+"#"+asNMSCopy.getName()+"(ItemStack.class)", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Couldn't invoke "+craftItemStack.getName()+"#"+asNMSCopy.getName()+"(ItemStack.class)", e);
		}
		CompoundTag itemTag = nmsItem.save(new CompoundTag());
		CompoundTag tags = (CompoundTag) itemTag.get("tag");
		if (tags==null){
			tags = new CompoundTag();
		}
		return tags;
	}

	public static CompoundTag convertToNBT(ItemStack itemStack){
		net.minecraft.world.item.ItemStack nmsItem = null;
		try {
			nmsItem = (net.minecraft.world.item.ItemStack) asNMSCopy.invoke(null, itemStack);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Insufficient permissions to use "+craftItemStack.getName()+"#"+asNMSCopy.getName()+"(ItemStack.class)", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Couldn't invoke "+craftItemStack.getName()+"#"+asNMSCopy.getName()+"(ItemStack.class)", e);
		}
		return nmsItem.save(new CompoundTag());
	}
	public static String convertToNBTString(ItemStack itemStack){
		return convertToNBT(itemStack).getAsString();
	}

	public static ItemStack convertToItemStack(CompoundTag compound){
		net.minecraft.world.item.ItemStack nmsItem = net.minecraft.world.item.ItemStack.of(compound);
		ItemStack item;
		try {
			item = (ItemStack) asBukkitCopy.invoke(null, nmsItem);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Insufficient permissions to use "+craftItemStack.getName()+"#"+asBukkitCopy.getName()+"(ItemStack.class)", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Couldn't invoke "+craftItemStack.getName()+"#"+asBukkitCopy.getName()+"(ItemStack.class)", e);
		}
		return item;
	}


	public static ItemStack convertToItemStack(String stringCompound){
		CompoundTag compoundTag = null;
		try {
			compoundTag = TagParser.parseTag(stringCompound);
			return convertToItemStack(compoundTag);
		} catch (CommandSyntaxException e) {
			throw new RuntimeException(e);
		}
	}


	private static String getVersion() {
		return version;
	}
}
