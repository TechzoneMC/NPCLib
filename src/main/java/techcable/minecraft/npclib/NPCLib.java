package techcable.minecraft.npclib;

import net.citizensnpcs.api.CitizensAPI;

public class NPCLib {
	private NPCLib() {};
	
	public static NPCRegistry getNPCRegistry() {
		if (hasCitizens());
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
