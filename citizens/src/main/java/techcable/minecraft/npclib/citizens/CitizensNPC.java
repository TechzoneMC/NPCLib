package techcable.minecraft.npclib.citizens;

import java.util.UUID;

import techcable.minecraft.npclib.util.EasyCache;
import techcable.minecraft.npclib.util.EasyCache.Loader;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import net.citizensnpcs.api.npc.NPC;

public class CitizensNPC implements techcable.minecraft.npclib.NPC {
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

    private static EasyCache<NPC, CitizensNPC> cache;

    public static CitizensNPC createNPC(NPC backing) {
	    if (cache == null) {
	        cache = new EasyCache<>(new Loader<NPC, CitizensNPC>() {
	            @Override
	            public CitizensNPC load(NPC backing) {
	                return new CitizensNPC(backing);
	            }
	        });
	    }
	    return cache.get(backing);
    }
    
    //Implementation
    
	public boolean despawn() {
	    return getBacking().despawn();
	}

	public void faceLocation(Location toFace) {
	    getBacking().faceLocation(toFace);
	}
	
	public Entity getEntity() {
	    return getBacking().getEntity();
	}

	public String getName() {
	    return getBacking().getName();
	}

	public UUID getUUID() {
	    return getBacking().getUniqueId();
	}

	public boolean isSpawned() {
	    return getBacking().isSpawned();
	}

	public void setName(String name) {
	    getBacking().setName(name);
	}

	public boolean spawn(Location toSpawn) {
	    return getBacking().spawn(toSpawn);
	}

	public void destroy() {
	    getBacking().destroy();
	}
	@Override
	public void setProtected(boolean protect) {
		getBacking().setProtected(protect);
	}
	@Override
	public boolean isProtected() {
		return getBacking().isProtected();
	}
}