package techcable.minecraft.npclib.citizens;

import net.citizensnpcs.api.npc.NPC;

public class CitizensNPC {
    private NPC backing;
    public NPC getBacking() {
	return backing;
    }
    public void setBacking(NPC backing) {
	this.backing = backing;
    }
    private CitizensNPC(NPC backing) {
	setBacking(backing);
    }

    private Map<NPC, CitizensNPC> cache = new WeakHashMap<>();

    public CitizensNPC createNPC(NPC backing) {
	if (cache.contains(backing)) return cache.get(backing);
	else {
	    CitizensNPC npc = new CitizensNPC(backing);
	    cache.put(backing, npc);
	    return npc;
	}
    }
}