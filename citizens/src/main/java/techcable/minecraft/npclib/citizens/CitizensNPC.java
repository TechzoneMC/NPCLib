package techcable.minecraft.npclib.citizens;

import java.util.UUID;

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

    public static CitizensNPC createNPC(NPC backing) {
    	return new CitizensNPC(backing);
    }
    
    //Implementation
    @Override
	public boolean despawn() {
	    if (isSpawned()) getBacking().despawn();
	    getBacking().destroy();
	    return true;
    }
	@Override
	public void faceLocation(Location toFace) {
	    getBacking().faceLocation(toFace);
	}
	@Override
	public Entity getEntity() {
	    return getBacking().getEntity();
	}
	@Override
	public String getName() {
	    return getBacking().getName();
	}
	@Override
	public UUID getUUID() {
	    return getBacking().getUniqueId();
	}
	@Override
	public boolean isSpawned() {
	    return getBacking().isSpawned();
	}
	@Override
	public void setName(String name) {
	    getBacking().setName(name);
	}
	@Override
	public boolean spawn(Location toSpawn) {
	    return getBacking().spawn(toSpawn);
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