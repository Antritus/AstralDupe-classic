package me.antritus.astraldupe;

import me.antritus.astral.cosmiccapital.api.IEconomy;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AstralDupeEconomy implements IEconomy {
	private final AstralDupe astralDupe;

	public AstralDupeEconomy(AstralDupe astralDupe) {
		this.astralDupe = astralDupe;
	}

	@Override
	public @NotNull String getName() {
		return "astraldupe";
	}

	@Override
	public @NotNull String getVersion() {
		return astralDupe.getPluginMeta().getVersion();
	}

	@Override
	public @Nullable Plugin getPlugin() {
		return astralDupe;
	}
}
