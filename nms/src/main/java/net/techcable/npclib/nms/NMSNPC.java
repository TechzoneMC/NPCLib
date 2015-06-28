package net.techcable.npclib.nms;

import lombok.*;

import java.util.UUID;

import net.techcable.npclib.NPC;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Preconditions;

@Getter
public abstract class NMSNPC<T extends INPCHook> extends BukkitRunnable implements NPC {

    private final NMSRegistry registry;
    private T hook;
    private final UUID id;
    private String name;

    private boolean spawned;

    public NMSNPC(NMSRegistry registry, UUID id, String name) {
        this.registry = registry;
        this.id = id;
        this.name = name;
        runTaskTimer(registry.getPlugin(), 0, 1);
    }

    @Override
    public void despawn() {
        Preconditions.checkState(hook != null, "NPC has already been destroyed");
        Preconditions.checkState(isSpawned(), "NPC is not spawned");
        hook.getEntity().remove();
        hook.onDespawn();
        hook = null;
        spawned = false;
        cancel();
        getRegistry().deregister(this);
    }

    @Override
    public Entity getEntity() {
        return hook.getEntity();
    }

    @Override
    public UUID getUUID() {
        return id;
    }

    @Override
    public boolean isSpawned() {
        return hook != null && spawned;
    }

    @Override
    public void spawn(Location toSpawn) {
        Preconditions.checkNotNull(toSpawn, "Location may not be null");
        Preconditions.checkState(hook != null, "NPC has been destroyed");
        Preconditions.checkState(!isSpawned(), "NPC is already spawned");
        hook = doSpawn(toSpawn);
        hook.getEntity().setMetadata(NMSRegistry.METADATA_KEY, new FixedMetadataValue(getRegistry().getPlugin(), this));
        spawned = true;
    }

    protected abstract T doSpawn(Location toSpawn);

    @Getter(AccessLevel.NONE)
    private boolean protect;

    @Override
    public void setProtected(boolean protect) {
        this.protect = protect;
    }

    @Override
    public boolean isProtected() {
        return protect;
    }

    @Override
    public void run() {
    }
}
