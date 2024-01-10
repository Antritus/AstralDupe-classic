package me.antritus.astraldupe;

import me.antritus.astral.cosmiccapital.api.IEconomyProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AstralDupeEconomy implements IEconomyProvider<AstralDupe> {
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
		return "1.0";
	}

	@Override
	public @Nullable AstralDupe getPlugin() {
		return astralDupe;
	}
}
