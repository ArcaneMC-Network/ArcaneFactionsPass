package it.arcanemc.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Timer {
    public static String getVerbose(long timeInMilliseconds) {
        if (timeInMilliseconds <= 0L){
            return "0s";
        }
        timeInMilliseconds = timeInMilliseconds / 1000L;

        long days = 0;
        long hours = 0;
        long minutes = 0;

        long duration = timeInMilliseconds;

        if (duration / 86400 > 0) {
            days = duration / 86400;
            duration = duration % 86400;
        }

        if (duration / 3600 > 0) {
            hours = duration / 3600;
            duration = duration % 3600;
        }

        if (duration / 60 > 0) {
            minutes = duration / 60;
            duration = duration % 60;
        }

        long seconds = duration;


        String output = "";

        if (days > 0) {
            output += days + "d ";
        }
        if (hours > 0) {
            output += hours + "h ";
        }
        if (minutes > 0) {
            output += minutes + "m ";
        }
        if (seconds > 0) {
            output += seconds + "s ";
        }
        return output.substring(0, output.length() - 1);
    }

    public static long convertVerbose(String input) {
        long totalSeconds = 0;

        Pattern pattern = Pattern.compile("(\\d+)([dhms])", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            char unit = Character.toLowerCase(matcher.group(2).charAt(0));

            switch (unit) {
                case 'd':
                    totalSeconds += value * 86400L;
                    break;
                case 'h':
                    totalSeconds += value * 3600L;
                    break;
                case 'm':
                    totalSeconds += value * 60L;
                    break;
                case 's':
                    totalSeconds += value;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown time unit: " + unit);
            }
        }

        return totalSeconds * 1000;
    }
}
