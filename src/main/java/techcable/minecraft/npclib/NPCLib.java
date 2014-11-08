package techcable.minecraft.npclib;

import techcable.minecraft.npclib.citizens.CitizensNPCRegistry;

import net.citizensnpcs.api.CitizensAPI;

public class NPCLib {
	private NPCLib() {};
	
	public static NPCRegistry getNPCRegistry() {
	    if (hasCitizens()) {
	        return CitizensNPCRegistry.getRegistry();
	    } else {
	        throw new UnsupportedOperationException();
	    }
	}
	
	public static NPCRegistry getNPCRegistry(String name) {
	    if (hasCitizens()) {
	        return CitizensNPCRegistry.getRegistry(name);
	    } else {
	        throw new UnsupportedOperationException();
	    }
	}
	
	public static boolean hasCitizens() {
		try {
			Class.forName("net.citizensnpcs.api.CitizensAPI");
		} catch (ClassNotFoundException e) {
			return false;
		}
		return CitizensAPI.hasImplementation();
	}
	public static boolean hasNMS() {
		return false;
	}
}
