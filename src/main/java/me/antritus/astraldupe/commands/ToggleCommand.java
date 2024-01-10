package me.antritus.astraldupe.commands;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.entity.AstralPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.ClassicDupe;

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
		if (astralPlayer.isToggleEnabled())
			ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(player.getUniqueId()).enableRandomItem();
		else
			ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(player.getUniqueId()).disableRandomItem();
		return true;
	}
}
