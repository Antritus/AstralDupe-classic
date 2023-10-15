package xyz.prorickey.classicdupe.metrics;


import java.time.Duration;

public class ServerMetrics {

    private final Long serverStartTime;

    public ServerMetrics() {
        serverStartTime = System.currentTimeMillis();
    }

    public Long getServerUptime() { return System.currentTimeMillis()-serverStartTime; }

    public String getServerUptimeFormatted() {
        Duration duration = Duration.ofMillis(System.currentTimeMillis() - serverStartTime);

        long days = duration.toDays();
        duration = duration.minusDays(days);

        long hours = duration.toHours();
        duration = duration.minusHours(hours);

        long minutes = duration.toMinutes();
        duration = duration.minusMinutes(minutes);

        long seconds = duration.toSeconds();
        if (days>0){
            return String.format("%dd, %dh, %dm", days, hours, minutes);
        } else if (hours>0){
            return String.format("%dh, %dm", hours, minutes);
        } else if (minutes>0){
            return String.format("%dm, %ds", minutes, seconds);
        } else if (seconds>0){
            return String.format("%ds", seconds);
        } else {
            return "~0s";
        }
    }
}
