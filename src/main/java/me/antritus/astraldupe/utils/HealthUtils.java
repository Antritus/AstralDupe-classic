package me.antritus.astraldupe.utils;

import org.bukkit.entity.Player;

import java.text.DecimalFormat;

/**
 * @since 1.0.0-snapshot
 * @author antritus
 */
public final class HealthUtils {
	private HealthUtils() {}

	public static double getFormattedHealth(Player player, int decimalPlaces) {
		if (decimalPlaces == 0) {
			return Double.parseDouble(new DecimalFormat("").format(player.getHealth()));
		}

		DecimalFormat numberFormat = new DecimalFormat("." + "0".repeat(Math.max(0, decimalPlaces)));
		return Double.parseDouble(numberFormat.format(player.getHealth()).replace(",", "."));
	}

	public static double getFormattedHearts(Player player, int decimalPlaces) {
		return getFormattedHealth(player, decimalPlaces) / 2;
	}

	public static double getFormattedHealth(Player player) {
		return getFormattedHealth(player, 1);
	}

	public static double getFormattedHearts(Player player) {
		return getFormattedHealth(player, 1) / 2;
	}
}