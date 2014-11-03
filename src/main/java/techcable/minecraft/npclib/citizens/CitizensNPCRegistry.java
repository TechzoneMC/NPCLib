package techcable.minecraft.npclib.citizens;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.citizensnpcs.api.npc.NPCRegistry;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import techcable.minecraft.npclib.NPC;

public class CitizensNPCRegistry implements techcable.minecraft.npclib.NPCRegistry {
	private NPCRegistry backing;
	public CitizensNPCRegistry(NPCRegistry backing) {
		setBacking(backing);
	}
	public void setBacking(NPCRegistry backing) {
		this.backing = backing;
	}
	public NPCRegistry getBacking() {
		return backing;
	}
	public NPC convertNPC(net.citizensnpcs.api.npc.NPC citizensNPC) {
		return null;
	}
	
	public NPC createNPC(EntityType type, String name) {
		return convertNPC(getBacking().createNPC(type, name));
	}

	public NPC createNPC(EntityType type, UUID uuid, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deregister(NPC npc) {
		// TODO Auto-generated method stub
		
	}

	public void deregisterAll() {
		// TODO Auto-generated method stub
		
	}

	public NPC getByUUID(UUID uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	public NPC getAsNPC(Entity entity) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isNPC(Entity entity) {
		// TODO Auto-generated method stub
		return false;
	}

	public Set<NPC> listNpcs() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static class IDTracker {
		private int nextId;
		private Set<Integer> usedIds = new HashSet<>();
		
		public void computeNextId() {
			nextId
		}
	}
}
