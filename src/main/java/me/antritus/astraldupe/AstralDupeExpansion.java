package me.antritus.astraldupe;

import me.antritus.astraldupe.entity.AstralPlayer;
import me.antritus.astraldupe.utils.PingUtils;
import me.antritus.astraldupe.utils.TPSUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
	public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
		AstralPlayer astralPlayer = astralDupe.astralPlayer(player.getUniqueId());
		switch (params.toLowerCase()){
			case "tps" -> { return TPSUtils.getFormattedTPS(TPSUtils.getTPS()); }
			case "tps_percentage" -> { return String.valueOf(TPSUtils.getTPSPercent(TPSUtils.getTPS())); }
			case "duped" -> {
				return ""+astralPlayer.getDuped();
			}
			case "received" ->{
				return ""+astralPlayer.getReceived();
			}
			case "global_duped" -> {
				return ""+AstralDupe.globalDuped;
			}
			case "global_received" ->{
				return ""+AstralDupe.globalReceived;
			}
			case "ping" ->{
				if (player instanceof Player oPlayer) {
					return PingUtils.getPingFormatMs(oPlayer);
				}
				return "<red>OFFLINE";
			}
		}
		return super.onRequest(player, params);
	}

	@Override
	public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
		return onRequest(player, params);
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

}
