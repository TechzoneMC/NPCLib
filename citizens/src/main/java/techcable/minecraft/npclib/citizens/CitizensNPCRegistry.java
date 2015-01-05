package techcable.minecraft.npclib.citizens;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.npc.SimpleNPCDataStore;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.api.util.MemoryDataKey;
import net.citizensnpcs.api.util.Storage;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.google.common.collect.Sets;

import techcable.minecraft.npclib.NPC;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CitizensNPCRegistry implements techcable.minecraft.npclib.NPCRegistry {
	@Getter
	private final NPCRegistry backing;
	private IDTracker idTracker = new IDTracker();

	public NPC convertNPC(net.citizensnpcs.api.npc.NPC citizensNPC) {
		return CitizensNPC.createNPC(citizensNPC);
	}
	public net.citizensnpcs.api.npc.NPC convertNPC(NPC techcableNPC) {
		return ((CitizensNPC)techcableNPC).getBacking();
	}
	
	public NPC createNPC(EntityType type, String name) {
		return createNPC(type, UUID.randomUUID(), name);
	}

	public NPC createNPC(EntityType type, UUID uuid, String name) {
		if (getByUUID(uuid) != null) throw new IllegalArgumentException("uuid is already in use");
		return convertNPC(getBacking().createNPC(type, uuid, idTracker.getNextId(), name));
	}

	public void deregister(NPC npc) {
		if (npc.isSpawned()) throw new IllegalStateException("Npc is spawned");
		getBacking().deregister(convertNPC(npc));
	}

	public void deregisterAll() {
		getBacking().deregisterAll();
	}

	public NPC getByUUID(UUID uuid) {
		return convertNPC(getBacking().getByUniqueId(uuid));
	}

	public NPC getAsNPC(Entity entity) {
		return convertNPC(getBacking().getNPC(entity));
	}

	public boolean isNPC(Entity entity) {
		return getBacking().isNPC(entity);
	}

	public Set<NPC> listNpcs() {
		Set<NPC> npcs = new HashSet<>();
		for (net.citizensnpcs.api.npc.NPC oldNPC : getBacking()) {
			npcs.add(convertNPC(oldNPC));
		}
		return npcs;
	}
	
	public static CitizensNPCRegistry getRegistry() {
       	if (CitizensAPI.getNamedNPCRegistry("NPCLib") == null) {
	        CitizensAPI.createNamedNPCRegistry("NPCLib", makeDataStore());
	    }
	    return new CitizensNPCRegistry(CitizensAPI.getNamedNPCRegistry("NPCLib"));
	}
	
	public static CitizensNPCRegistry getRegistry(String registryName) {
	    if (CitizensAPI.getNamedNPCRegistry("NPCLib." + registryName) == null) {
	        CitizensAPI.createNamedNPCRegistry("NPCLib." + registryName, makeDataStore());
	    }
	    return new CitizensNPCRegistry(CitizensAPI.getNamedNPCRegistry("NPCLib." + registryName));
	}
	
	private static NPCDataStore makeDataStore() {
	    Storage storage = new MemoryStorage();
	    return SimpleNPCDataStore.create(storage);
	}
	
	public static class MemoryStorage implements Storage {
	    
	    public DataKey dataKey = new MemoryDataKey();
	    
	    @Override
	    public DataKey getKey(String root) {
	        return dataKey.getRelative(root);
	    }
	    
	    //NO Ops
	    @Override
	    public boolean load() {
	        return true;
	    }
	    
	    @Override
	    public void save() {}
	}
	
	private static class IDTracker {
		private int nextId;
		private Set<Integer> usedIds = new HashSet<>();
		
		public int getNextId() {
			if (isUsed(nextId)) computeNextId();
			int id = nextId;
			useId(id);
			nextId++;
			return id;
		}
		
		public void removeId(int id) {
			usedIds.remove(id);
		}
		public void useId(int id) {
			if (isUsed(id)) throw new RuntimeException("id is already in use");
			usedIds.add(id);
		}
		
		public void computeNextId() {
			for (int i = 0; i < Integer.MAX_VALUE; i++) {
				if (!isUsed(i)) { 
					this.nextId = i;
					return;
				}
			}
			throw new RuntimeException("Ran out of ids");
		}
		public boolean isUsed(int id) {
			return usedIds.contains(id);
		}
	}
}
