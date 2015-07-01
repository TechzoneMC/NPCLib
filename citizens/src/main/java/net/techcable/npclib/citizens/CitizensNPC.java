package net.techcable.npclib.citizens;


import lombok.*;

import java.lang.ref.WeakReference;
import java.util.UUID;

import net.citizensnpcs.api.npc.NPC;
import net.techcable.npclib.ai.AITask;
import net.techcable.npclib.citizens.ai.CitizensAIEnvironment;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.google.common.base.Preconditions;

public class CitizensNPC implements net.techcable.npclib.NPC {

    public CitizensNPC(NPC handle) {
        this.handle = new WeakReference<NPC>(handle);
    }

    public NPC getHandle() {
        return handle.get();
    }

    private final WeakReference<NPC> handle;

    private boolean destroyed;

    @Override
    public void despawn() {
        Preconditions.checkState(isSpawned(), "Already despawned");
        getHandle().despawn();
        getHandle().destroy();
        destroyed = true;
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
    public boolean isDestroyed() {
        return getHandle() == null || destroyed;
    }

    @Override
    public void spawn(Location toSpawn) {
        Preconditions.checkNotNull(toSpawn, "Null location");
        Preconditions.checkState(getHandle() != null, "This npc has been destroyed");
        Preconditions.checkState(!isSpawned(), "Already spawned");
        getHandle().spawn(toSpawn);
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

    @Override
    public void addTask(AITask task) {
        getAIEnvironment().addTask(task);
    }

    @Getter(lazy = true)
    private final CitizensAIEnvironment aIEnvironment = new CitizensAIEnvironment(this);
}
