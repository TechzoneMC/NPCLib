package techcable.minecraft.npclib.citizens;

import techcable.minecraft.npclib.util.EasyCache;

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

    private EasyCache<NPC, CitizensNPC> cache;

    public CitizensNPC createNPC(NPC backing) {
	    if (cache == null) {
	        cache = new EasyCache(new Loader<NPC, CitizensNPC>() {
	            @Override
	            public CitizensNPC load(NPC backing) {
	                return new CitizensNPC(backing);
	            }
	        });
	    }
	    return cache.get(backing);
    }
}