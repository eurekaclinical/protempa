package org.arp.javautil.log;

/**
 * Utilities for elapsed time.
 * 
 * @author Michael Brown
 * 
 */
public class ElapsedTime {

    public static long getElapsedMilliseconds(long now, long then) {
        return now - then;
    }

    public static String getElapsedTime(long now, long then) {
        if (getElapsedSeconds(now, then) > 60F) {
            if (getElapsedMinutes(now, then) > 60)
                return String.format("%.2f hours", getElapsedHours(now, then));
            else
                return String.format("%.2f minutes",
                        getElapsedMinutes(now, then));
        } else
            return String.format("%.2f seconds", getElapsedSeconds(now, then));
    }

    public static float getElapsedHours(long now, long then) {
        return getElapsedMilliseconds(now, then) / 1000F / 60 / 60;
    }

    public static float getElapsedMinutes(long now, long then) {
        return getElapsedMilliseconds(now, then) / 1000F / 60;
    }

    public static float getElapsedSeconds(long now, long then) {
        return getElapsedMilliseconds(now, then) / 1000F;
    }
}
