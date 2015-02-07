package net.techcable.npclib.citizens;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.citizensnpcs.api.npc.NPC;
import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.util.ProfileUtils;
import net.techcable.npclib.util.ProfileUtils.PlayerProfile;

public class CitizensHumanNPC extends CitizensNPC implements HumanNPC {
	public CitizensHumanNPC(UUID id, CitizensNPCRegistry registry) {
		super(id, registry);
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

	@Override
	public Player getEntity() {
		return (Player) super.getEntity();
	}

	//Unsupported
	
	@Override
	public void sleep() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sleep(Location l) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void wake() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void eat() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void swing() {
		throw new UnsupportedOperationException();
	}
}
