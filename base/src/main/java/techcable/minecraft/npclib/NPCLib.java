package techcable.minecraft.npclib;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

import techcable.minecraft.npclib.citizens.CitizensNPCRegistry;
import techcable.minecraft.npclib.nms.NMSRegistry;

public class NPCLib {
	private NPCLib() {};
	
	private static NMSRegistry defaultNMS;
	private static Map<String, NMSRegistry> registryMap = new HashMap<>();
	
	public static NPCRegistry getNPCRegistry() {
	    if (hasCitizens()) {
	        return CitizensNPCRegistry.getRegistry();
	    } else {
	        if (defaultNMS == null) {
	        	defaultNMS = new NMSRegistry();
	        }
	        return defaultNMS;
	    }
	}
	
	public static NPCRegistry getNPCRegistry(String name) {
	    if (hasCitizens()) {
	        return CitizensNPCRegistry.getRegistry(name);
	    } else {
	        if (!registryMap.containsKey(name)) {
	        	registryMap.put(name, new NMSRegistry());
	        }
	        return registryMap.get(name);
	    }
	}
	
	public static boolean hasCitizens() {
		try {
			Class.forName("net.citizensnpcs.api.CitizensAPI");
		} catch (ClassNotFoundException e) {
			return false;
		}
		return Bukkit.getPluginManager().isPluginEnabled("Citizens");
	}
	public static boolean hasNMS() {
		return false;
	}
}
