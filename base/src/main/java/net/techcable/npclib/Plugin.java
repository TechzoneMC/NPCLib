package net.techcable.npclib;

import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

    @Override
    public void onEnable() {
        NPCMetrics.start(this);
    }
}