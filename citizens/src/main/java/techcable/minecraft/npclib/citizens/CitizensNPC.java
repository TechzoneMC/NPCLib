package techcable.minecraft.npclib.citizens;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import lombok.RequiredArgsConstructor;
import net.citizensnpcs.api.npc.NPC;

@RequiredArgsConstructor
public class CitizensNPC implements techcable.minecraft.npclib.NPC {
    private final NPC backing;
    public NPC getBacking() {
    	return backing;
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

	@Override
	public void update() {}

	@Override
	public void update(Player... players) {}
}