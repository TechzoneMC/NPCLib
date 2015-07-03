package net.techcable.npclib.nms;

import lombok.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.LivingNPC;
import net.techcable.npclib.NPC;
import net.techcable.npclib.NPCRegistry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

@Getter
@RequiredArgsConstructor
public class NMSRegistry implements NPCRegistry, Listener {

    private final Plugin plugin;

    public static final String METADATA_KEY = "NPCLib";

    @Override
    public HumanNPC createHumanNPC(String name) {
        return createHumanNPC(UUID.randomUUID(), name);
    }

    @Override
    public HumanNPC createHumanNPC(UUID uuid, String name) {
        Preconditions.checkNotNull(uuid, "Cant have null id");
        Preconditions.checkNotNull(name, "Cant have null name");
        NMSHumanNPC npc = new NMSHumanNPC(this, uuid, name);
        npcs.put(uuid, npc);
        return npc;
    }

    @Override
    public LivingNPC createLivingNPC(String name, EntityType type) {
        return createLivingNPC(UUID.randomUUID(), name, type);
    }

    @Override
    public LivingNPC createLivingNPC(UUID uuid, String name, EntityType type) {
        Preconditions.checkNotNull(uuid, "Cant have null id");
        Preconditions.checkNotNull(name, "Cant have null name");
        if (type == EntityType.PLAYER) return createHumanNPC(uuid, name);
        NMSLivingNPC npc = new NMSLivingNPC(this, uuid, name, type);
        npcs.put(uuid, npc);
        return npc;
    }

    @Getter(AccessLevel.NONE)
    private final Map<UUID, NMSNPC> npcs = new HashMap<>();

    @Override
    public void deregister(NPC npc) {
        Preconditions.checkState(!npc.isSpawned(), "NPC is Spawned");
        npcs.remove(npc.getUUID());
    }

    @Override
    public void deregisterAll() {
        for (NPC npc : npcs.values()) {
            deregister(npc);
        }
    }

    @Override
    public NPC getByUUID(UUID uuid) {
        return npcs.get(uuid);
    }

    @Override
    public NPC getByName(String name) {
        for (NPC npc : npcs.values()) {
            if (npc instanceof LivingNPC) {
                if (((LivingNPC) npc).getName().equals(name)) {
                    return npc;
                }
            }
        }
        return null;
    }

    @Override
    public NPC getAsNPC(Entity entity) {
        List<MetadataValue> metadataList = entity.getMetadata(METADATA_KEY);
        for (MetadataValue metadata : metadataList) {
            if (metadata.value() instanceof NPC) {
                return (NPC) metadata.value();
            }
        }
        return null;
    }

    @Override
    public boolean isNPC(Entity entity) {
        return entity.hasMetadata(METADATA_KEY) && getAsNPC(entity) != null;
    }

    @Override
    public ImmutableCollection<? extends NPC> listNpcs() {
        return ImmutableSet.copyOf(npcs.values());
    }

    @Getter(lazy = true)
    private final static NMS nms = makeNms();

    private static NMS makeNms() {
        try {
            if (Bukkit.getServer() == null) return null;
            String packageName = Bukkit.getServer().getClass().getPackage().getName();
            String version = packageName.substring(packageName.lastIndexOf(".") + 1);
            if (!version.startsWith("v")) return null;
            Class<?> rawClass = Class.forName("net.techcable.npclib.nms.versions." + version + ".NMS");
            Class<? extends NMS> nmsClass = rawClass.asSubclass(NMS.class);
            Constructor<? extends NMS> constructor = nmsClass.getConstructor();
            return constructor.newInstance();
        } catch (ClassNotFoundException ex) {
            throw new UnsupportedOperationException("Unsupported nms version", ex);
        } catch (InvocationTargetException ex) {
            throw Throwables.propagate(ex.getTargetException());
        } catch (Exception ex) {
            throw Throwables.propagate(ex);
        }
    }
}