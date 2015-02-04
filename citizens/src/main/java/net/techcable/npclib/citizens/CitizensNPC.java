package net.techcable.npclib.citizens;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.citizensnpcs.api.npc.NPC;
import net.techcable.npclib.util.ProfileUtils;
import net.techcable.npclib.util.ProfileUtils.PlayerProfile;

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
	public void setSkin(UUID skin) {
		if (skin == null) return;
		getBacking().data().set(NPC.PLAYER_SKIN_UUID_METADATA, skin);
		if (isSpawned()) {
			despawn();
			spawn(getBacking().getStoredLocation());
		}
	}
	
	@Override
	public void setSkin(String skin) {
	    if (skin == null) return;
	    PlayerProfile profile = ProfileUtils.lookup(skin);
	    if (profile == null) return;
	    setSkin(profile.getId());
	}
	
	@Override
	public UUID getSkin() {
		if (!getBacking().data().has(NPC.PLAYER_SKIN_UUID_METADATA)) return null;
		return getBacking().data().get(NPC.PLAYER_SKIN_UUID_METADATA);
	}
}