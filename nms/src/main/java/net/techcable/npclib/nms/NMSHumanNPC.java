package net.techcable.npclib.nms;

import java.util.UUID;

import lombok.Getter;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.util.ProfileUtils;
import net.techcable.npclib.util.ProfileUtils.PlayerProfile;

public class NMSHumanNPC extends NMSNPC implements HumanNPC {
	public NMSHumanNPC(java.util.UUID uuid,
			NMSRegistry registry) {
		super(uuid, EntityType.PLAYER, registry);
	}
	
	@Getter
	private UUID skin;
	
	@Override
	public void setSkin(UUID skin) {
		if (!Util.getNMS().isSupported(OptionalFeature.SKINS)) throw new UnsupportedOperationException();
		this.skin = skin;
		if (isSpawned()) {
			Location last = getEntity().getLocation();
			despawn();
			spawn(last);
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
	public Player getEntity() {
		return (Player) super.getEntity();
	}
	
	@Override
	public EntityHumanNPC getNpc() {
		return (EntityHumanNPC) super.getNpc();
	}
	
	//Animations
	
	@Override
	public void sleep() {
		sleep(getEntity().getLocation());
	}

	@Override
	public void sleep(Location l) {
		getNpc().lie(l);
	}

	@Override
	public void wake() {
		getNpc().wake();
	}

	@Override
	public void eat() {
		getNpc().eat();
	}

	@Override
	public void swing() {
		getNpc().swing();
	}
	
	//Overides
	
	@Override
	public boolean spawn(Location toSpawn) {
		if (isSpawned()) throw new IllegalStateException("Already spawned");
		EntityHumanNPC npc = Util.getNMS().createPlayer(this);
		setNpc(npc);
		try {
			npc.spawn(toSpawn);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
