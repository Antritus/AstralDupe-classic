package me.antritus.astraldupe.commands;


import com.github.antritus.astral.commands.CoreCommand;
import me.antritus.astraldupe.AstralDupe;
import org.jetbrains.annotations.NotNull;

public abstract class AstralCommand extends CoreCommand {
	protected AstralCommand(AstralDupe main, @NotNull String name) {
		super(main, name);
	}
}
