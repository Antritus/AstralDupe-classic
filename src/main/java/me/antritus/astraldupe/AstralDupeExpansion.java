package me.antritus.astraldupe;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.ClassicDupeExpansion;

public class AstralDupeExpansion extends ClassicDupeExpansion {
	private final AstralDupe astralDupe;

	public AstralDupeExpansion(){
		super(ClassicDupe.getInstance());
		astralDupe = AstralDupe.getInstance();
	}
	public AstralDupeExpansion(AstralDupe plugin) {
		super(plugin);
		astralDupe = plugin;
	}

	@Override
	public @NotNull String getIdentifier() {
		return "astraldupe";
	}

	@Override
	public @NotNull String getAuthor() {
		return super.getAuthor()+", Antritus";
	}

	@Override
	public @NotNull String getVersion() {
		return astralDupe.getPluginMeta().getVersion();
	}

	@Override
	public String onRequest(OfflinePlayer player, String params) {
		return super.onRequest(player, params);
	}
}
