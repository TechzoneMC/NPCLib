package net.techcable.npclib;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import net.techcable.npclib.citizens.CitizensNPCRegistry;
import net.techcable.npclib.nms.NMSRegistry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class NPCLib {

    public static final String VERSION = "2.0.0-beta1-SNAPSHOT";
    public static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    private NPCLib() {
    }

    private static NMSRegistry defaultNMS;
    private static Map<String, NMSRegistry> registryMap = new HashMap<>();

    public static NPCRegistry getNPCRegistry(Plugin plugin) {
        if (hasCitizens()) {
            NPCMetrics.addUsingPlugin(plugin);
            return CitizensNPCRegistry.getRegistry(plugin);
        } else if (hasNMS()) {
            NPCMetrics.addUsingPlugin(plugin);
            if (defaultNMS == null) {
                defaultNMS = new NMSRegistry(plugin);
            }
            return defaultNMS;
        } else {
            throw new UnsupportedVersionException("This version of minecraft isn't supported, please install citizens");
        }
    }

    public static NPCRegistry getNPCRegistry(String name, Plugin plugin) {
        if (hasCitizens()) {
            NPCMetrics.addUsingPlugin(plugin);
            return CitizensNPCRegistry.getRegistry(name, plugin);
        } else if (hasNMS()) {
            NPCMetrics.addUsingPlugin(plugin);
            if (!registryMap.containsKey(name)) {
                registryMap.put(name, new NMSRegistry(plugin));
            }
            return registryMap.get(name);
        } else {
            throw new UnsupportedVersionException("This version of minecraft isn't supported, please install citizens");
        }
    }

    public static boolean isSupported() {
        return hasCitizens() || hasNMS();
    }

    private static boolean hasCitizens() {
        try {
            Class.forName("net.citizensnpcs.api.CitizensAPI");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return Bukkit.getPluginManager().isPluginEnabled("Citizens");
    }

    private static boolean hasNMS() {
        try {
            return NMSRegistry.getNms() != null;
        } catch (UnsupportedOperationException ex) {
            return false;
        }
    }

    private static Field storedRegistriesField;

    public static boolean isNPC(Entity e) {
        if (hasNMS()) {
            if (e.hasMetadata(NMSRegistry.METADATA_KEY)) {
                List<MetadataValue> metadataList = e.getMetadata(NMSRegistry.METADATA_KEY);
                for (MetadataValue metadataValue : metadataList) {
                    if (metadataValue.value() instanceof NPC) return true;
                }
            }
            return false;
        } else if (hasCitizens()) {
            for (CitizensNPCRegistry registry : CitizensNPCRegistry.getRegistries()) {
                if (registry.isNPC(e)) return true;
            }
        }
        return false;
    }
}
