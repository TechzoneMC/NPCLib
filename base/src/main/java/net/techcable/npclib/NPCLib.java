package net.techcable.npclib;

import java.util.HashMap;
import java.util.Map;

import net.techcable.npclib.citizens.CitizensNPCRegistry;
import net.techcable.npclib.nms.NMSRegistry;
import net.techcable.npclib.nms.Util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class NPCLib {
	private NPCLib() {};
	
	private static NMSRegistry defaultNMS;
	private static Map<String, NMSRegistry> registryMap = new HashMap<>();
	
	public static NPCRegistry getNPCRegistry(Plugin plugin) {
	    if (hasCitizens()) {
	        return CitizensNPCRegistry.getRegistry(plugin);
	    } else if (hasNMS()) {
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
	        return CitizensNPCRegistry.getRegistry(name, plugin);
	    } else if (hasNMS()) {
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
			Util.getNMS();
			return true;
		} catch (UnsupportedOperationException ex) {
			return false;
		}
	}
}
