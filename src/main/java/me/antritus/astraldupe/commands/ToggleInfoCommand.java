package me.antritus.astraldupe.commands;

import bet.astral.messagemanager.placeholder.LegacyPlaceholder;
import me.antritus.astraldupe.AstralDupe;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static xyz.prorickey.classicdupe.ClassicDupe.randomItems;

public class ToggleInfoCommand extends AstralCommand{
	public ToggleInfoCommand(AstralDupe main) {
		super(main, "toggleinfo");
		setAliases(List.of("randominfo"));
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
		main.messageManager().message(sender, "toggle.info", new LegacyPlaceholder("items", String.valueOf(randomItems.size())));
		return false;
	}
}
