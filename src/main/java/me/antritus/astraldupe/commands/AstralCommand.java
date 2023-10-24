package me.antritus.astraldupe.commands;


import me.antritus.astraldupe.AstralDupe;
import org.bukkit.command.Command;
import org.jetbrains.annotations.NotNull;

public abstract class AstralCommand extends Command {
	protected final AstralDupe main;
	protected AstralCommand(AstralDupe main, @NotNull String name) {
		super(name);
		this.main = main;
	}
}
