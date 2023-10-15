package me.antritus.astraldupe.utils;

import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import org.bukkit.Bukkit;

import java.text.DecimalFormat;
import java.util.Objects;

public class TPSUtils {
	private static final DecimalFormat decimalFormat = new DecimalFormat(".00");
	public static double getTPS() {
		try {
			Class.forName("me.lucko.spark.api.Spark");
			Spark spark = SparkProvider.get();
			return Objects.requireNonNull(spark.tps()).poll(StatisticWindow.TicksPerSecond.SECONDS_5);
		} catch (ClassNotFoundException e) {
			return Bukkit.getTPS()[0];
		}
	}

	private static String formatDouble(double tps){
		return decimalFormat.format(tps);
	}
	public static String getFormattedTPS(double tps){
		String formatTPS = formatDouble(tps);
		if (tps>19.85){
			return "<dark_green>"+20;
		} else if (tps>18){
			return "<green>"+formatTPS;
		} else if (tps>12){
			return "<yellow>"+formatTPS;
		}else if (tps>6){
			return "<red>"+formatTPS;
		} else {
			return "<dark_red>"+formatTPS;
		}
	}
	public static double getTPSPercent(double tps){
		if (tps>19.85){
			tps = 20.0;
		}
		return 1-(tps/20.0);
	}
}
