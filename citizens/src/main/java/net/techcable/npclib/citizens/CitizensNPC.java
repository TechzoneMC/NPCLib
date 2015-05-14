package net.techcable.npclib.citizens;


import com.google.common.base.Preconditions;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class CitizensNPC implements net.techcable.npclib.NPC {
    public CitizensNPC(NPC handle) {
        this.handle = new WeakReference<NPC>(handle);
    }

    public NPC getHandle() {
        return handle.get();
    }
    private final WeakReference<NPC> handle;

    @Override
    public boolean despawn() {
        if (getHandle() == null || !isSpawned()) throw new IllegalStateException("Already despawned");
        getHandle().despawn();
        getHandle().destroy();
        return true;
    }

    @Override
    public Entity getEntity() {
        if (getHandle() == null) return null;
        return getHandle().getEntity();
    }

    @Override
    public UUID getUUID() {
        return getHandle().getUniqueId();
    }

    @Override
    public boolean isSpawned() {
        return getHandle() != null && getHandle().isSpawned();
    }

    @Override
    public boolean spawn(Location toSpawn) {
        Preconditions.checkState(getHandle() != null, "This npc has been destroyed");
        Preconditions.checkState(!isSpawned(), "Already spawned");
        return getHandle().spawn(toSpawn);
    }

    @Override
    public void setProtected(boolean protect) {
        Preconditions.checkState(getHandle() != null, "This npc has been destroyed");
        getHandle().setProtected(protect);
    }

    @Override
    public boolean isProtected() {
        if (getHandle() == null) return false;
        return getHandle().isProtected();
    }
}
