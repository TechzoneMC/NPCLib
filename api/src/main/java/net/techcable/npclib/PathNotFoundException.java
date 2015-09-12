package net.techcable.npclib;

import org.bukkit.Location;

public class PathNotFoundException extends Exception {
    public PathNotFoundException(Location to, Location from) {
        super(String.format("Could not find a path to %s, %s, %s from %s, %s, %s", to.getX(), to.getY(), to.getZ(), from.getX(), from.getY(), from.getZ()));
    }
}
