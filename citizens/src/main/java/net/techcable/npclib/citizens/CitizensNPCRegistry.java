package net.techcable.npclib.citizens;

import lombok.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.npc.SimpleNPCDataStore;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.api.util.MemoryDataKey;
import net.citizensnpcs.api.util.Storage;
import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.LivingNPC;
import net.techcable.npclib.NPC;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

@RequiredArgsConstructor
public class CitizensNPCRegistry implements net.techcable.npclib.NPCRegistry {

    @Getter
    private final NPCRegistry backing;
    private final Map<UUID, net.techcable.npclib.NPC> npcMap = new WeakHashMap<>();
    @Getter
    private final Plugin plugin;
    private IDTracker idTracker = new IDTracker();

    public net.citizensnpcs.api.npc.NPC convertNPC(NPC npc) {
        return ((CitizensNPC) npc).getHandle();
    }


    public NPC convertNPC(net.citizensnpcs.api.npc.NPC npc) {
        return npcMap.get(npc.getUniqueId());
    }

    @Override
    public HumanNPC createHumanNPC(String name) {
        return createHumanNPC(UUID.randomUUID(), name);
    }

    @Override
    public HumanNPC createHumanNPC(UUID uuid, String name) {
        net.citizensnpcs.api.npc.NPC citizens = getBacking().createNPC(EntityType.PLAYER, uuid, idTracker.getNextId(), name);
        HumanNPC npc = new HumanCitizensNPC(citizens);
        npcMap.put(npc.getUUID(), npc);
        return npc;
    }

    @Override
    public LivingNPC createLivingNPC(String name, EntityType type) {
        return createLivingNPC(UUID.randomUUID(), name, type);
    }

    @Override
    public LivingNPC createLivingNPC(UUID uuid, String name, EntityType type) {
        if (type == EntityType.PLAYER) return createHumanNPC(uuid, name);
        Preconditions.checkArgument(type.isAlive(), "This npc type isn't alive");
        net.citizensnpcs.api.npc.NPC citizens = getBacking().createNPC(type, uuid, idTracker.getNextId(), name);
        LivingNPC npc = new LivingCitizensNPC(citizens);
        npcMap.put(npc.getUUID(), npc);
        return npc;
    }

    public void deregister(NPC npc) {
        if (npc.isSpawned()) throw new IllegalStateException("Npc is spawned");
        getBacking().deregister(convertNPC(npc));
        idTracker.removeId(convertNPC(npc).getId());
        npcMap.remove(npc.getUUID());
    }

    public void deregisterAll() {
        getBacking().deregisterAll();
        npcMap.clear();
    }

    public NPC getByUUID(UUID uuid) {
        return npcMap.get(uuid);
    }

    @Override
    public NPC getByName(String name) {
        for (NPC npc : npcMap.values()) {
            if (npc instanceof LivingNPC && ((LivingNPC) npc).getName().equals(name)) return npc;
        }
        return null;
    }

    public NPC getAsNPC(Entity entity) {
        return convertNPC(getBacking().getNPC(entity));
    }

    public boolean isNPC(Entity entity) {
        return getBacking().isNPC(entity);
    }

    public ImmutableCollection<NPC> listNpcs() {
        return ImmutableSet.copyOf(npcMap.values());
    }

    private static final Map<String, CitizensNPCRegistry> registryMap = new WeakHashMap<>();
    private static CitizensNPCRegistry defaultRegistry;

    public static CitizensNPCRegistry getRegistry(Plugin plugin) {
        if (defaultRegistry == null) {
            NPCRegistry citizensRegistry = CitizensAPI.createNamedNPCRegistry("NPCLib", makeDataStore());
            CitizensNPCRegistry wrapper = new CitizensNPCRegistry(citizensRegistry, plugin);
            defaultRegistry = wrapper;
        }
        return defaultRegistry;
    }

    public static CitizensNPCRegistry getRegistry(String registryName, Plugin plugin) {
        if (!registryMap.containsKey(registryName)) {
            NPCRegistry citizensRegistry = CitizensAPI.createNamedNPCRegistry("NPCLib." + registryName, makeDataStore());
            CitizensNPCRegistry wrapper = new CitizensNPCRegistry(citizensRegistry, plugin);
            registryMap.put(registryName, wrapper);
        }
        return registryMap.get(registryName);
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
        public void save() {
        }
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

    public static Collection<CitizensNPCRegistry> getRegistries() {
        return registryMap.values();
    }
}
