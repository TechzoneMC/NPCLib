package net.techcable.npclib;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

/**
 * Represents a non player controlled nmsEntity
 *
 * @author Techcable
 * @version 2.0
 * @since 1.0
 */
public interface NPC {

    /**
     * Despawn this npc
     * <p/>
     * Once despawned it can not be respawned
     * It will be deregistered from the registry
     *
     * @return true if was able to despawn
     *
     * @throws java.lang.IllegalStateException if the npc is already despawned
     */
    public void despawn();

    /**
     * Get the nmsEntity associated with this npc
     *
     * @return the nmsEntity
     */
    public Entity getEntity();

    /**
     * Get this npc's uuid
     *
     * @return the uuid of this npc
     */
    public UUID getUUID();

    /**
     * Returns weather the npc is spawned
     *
     * @return true if the npc is spawned
     */
    public boolean isSpawned();

    /**
     * Spawn this npc
     *
     * @param toSpawn location to spawn this npc
     *
     * @return true if the npc was able to spawn
     *
     * @throws java.lang.NullPointerException if location is null
     * @throws java.lang.IllegalArgumentException if already or spawned or {@link #despawn()} has been called
     */
    public void spawn(Location toSpawn);

    /**
     * Set the protected status of this NPC
     * true by default
     *
     * @param protect whether or not this npc is invincible
     */
    public void setProtected(boolean protect);

    /**
     * Check if the NPC is protected from damage
     *
     * @return The protected status of the NPC
     */
    public boolean isProtected();
}
