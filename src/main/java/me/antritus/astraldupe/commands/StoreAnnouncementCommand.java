package me.antritus.astraldupe.commands;

import me.antritus.astraldupe.AstralDupe;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class StoreAnnouncementCommand extends AstralCommand{
	protected StoreAnnouncementCommand(AstralDupe main, @NotNull String name) {
		super(main, "console-store-broadcast");
		setPermission("astraldupe.internal.console");
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
		if (sender instanceof ConsoleCommandSender){
		} else {
			main.getMessageManager().message(sender, "command-parse.console-only", "%command%=console-store-broadcast");
		}
		return true;
	}
}
