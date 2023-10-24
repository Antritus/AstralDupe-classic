package me.antritus.astraldupe.commands;

import me.antritus.astraldupe.AstralDupe;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class StoreAnnouncementCommand extends AstralCommand{
	public StoreAnnouncementCommand(AstralDupe main) {
		super(main, "console-store-broadcast");
		setPermission("astraldupe.internal.console");
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
		if (sender instanceof ConsoleCommandSender){
			MiniMessage miniMessage = MiniMessage.miniMessage();
			assert args.length>=1;
			switch (args[1]) {
				case "quasar" -> {
					for (int i = 0; i < 5; i++) {
						Bukkit.broadcast(miniMessage.deserialize("<light_purple><b>STORE PURCHASE <reset><dark_gray>| <white>" + args[0] + " <yellow>has purchased <b>QUASAR RANK<yellow>!!!"));
					}
				}
				case "rename" -> {
					for (int i = 0; i < 5; i++) {
						Bukkit.broadcast(miniMessage.deserialize("<light_purple><b>STORE PURCHASE <reset><dark_gray>| <white>" + args[0] + " <yellow>has purchased <b>RENAME PERK<yellow>!!!"));
					}
				}
				case "suffix" -> {
					for (int i = 0; i < 5; i++) {
						Bukkit.broadcast(miniMessage.deserialize("<light_purple><b>STORE PURCHASE <reset><dark_gray>| <white>" + args[0] + " <yellow>has purchased <b>SUFFIX BUNDLE<yellow>!!!"));
					}
				}
			}
		} else {
			main.getMessageManager().message(sender, "command-parse.console-only", "%command%=console-store-broadcast");
		}
		return true;
	}
}
