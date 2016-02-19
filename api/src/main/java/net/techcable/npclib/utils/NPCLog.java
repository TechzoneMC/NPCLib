package net.techcable.npclib.utils;

import lombok.*;

import java.util.logging.Level;

import org.bukkit.Bukkit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NPCLog {

    public static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("npclib.debug", "false"));

    public static final String PREFIX = "NPCLib";

    public static void info(String msg) {
        log(Level.INFO, msg);
    }

    public static void info(String msg, Object... args) {
        log(Level.INFO, msg, args);
    }

    public static void warn(String msg) {
        log(Level.WARNING, msg);
    }

    public static void warn(String msg, Object... args) {
        log(Level.WARNING, msg, args);
    }

    public static void warn(String msg, Throwable t) {
        log(Level.WARNING, msg, t);
    }

    public static void severe(String msg, Throwable t) {
        log(Level.SEVERE, msg, t);
    }

    public static void debug(String msg) {
        if (DEBUG) {
            log(Level.INFO, msg);
        }
    }

    public static void debug(String msg, Throwable t) {
        if (DEBUG) {
            log(Level.INFO, msg, t);
        }
    }

    private static void log(Level level, String msg, Throwable t) {
        Bukkit.getLogger().log(level, PREFIX + " " + msg, t);
    }

    private static void log(Level level, String msg, Object... args) {
        Bukkit.getLogger().log(level, String.format(msg, args));
    }

    private static void log(Level level, String msg) {
        Bukkit.getLogger().log(level, msg);
    }
}
