package me.antritus.astraldupe.commands;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.entity.AstralPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ToggleCommand extends AstralCommand{
	public ToggleCommand(AstralDupe main) {
		super(main, "toggle");
		setAliases(List.of("random"));
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
		if (!(sender instanceof Player player)){
			main.messageManager().message(sender, "command-parse.player-only");
			return true;
		}
		AstralPlayer astralPlayer = main.astralPlayer(player);
		astralPlayer.setToggleEnabled(!astralPlayer.isToggleEnabled());
		main.messageManager().message(sender, "toggle."+astralPlayer.isToggleEnabled());
		return true;
	}
}
