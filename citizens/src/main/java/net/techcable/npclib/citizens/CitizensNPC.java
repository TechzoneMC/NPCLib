package net.techcable.npclib.citizens;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.citizensnpcs.api.npc.NPC;

import lombok.*;

@Getter
@RequiredArgsConstructor
public class CitizensNPC implements net.techcable.npclib.NPC {
    private final UUID id;
    private final CitizensNPCRegistry registry;
    public NPC getBacking() {
    	return getRegistry().getBacking().getByUniqueId(getId());
    }

    public static CitizensNPC createNPC(NPC backing, CitizensNPCRegistry registry) {
    	return new CitizensNPC(backing.getUniqueId(), registry);
    }
    
    //Implementation
    @Override
	public boolean despawn() {
	    if (isSpawned()) getBacking().despawn();
	    getBacking().destroy();
	    getRegistry().deregister(this);
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

	@Override
	public void update() {}
}