package net.techcable.npclib;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

/**
 * Represents a living non-player controlled nmsEntity
 *
 * @author Techcable
 * @version 2.0
 * @since 2.0
 */
public interface LivingNPC extends NPC {

    /**
     * Set the current name of the npc
     *
     * @param name the new name
     */
    public void setName(String name);

    /**
     * Retrieve the name of this npc
     *
     * @return this npc's name
     */
    public String getName();

    /**
     * Get the nmsEntity associated with this npc
     *
     * @return the nmsEntity
     */
    @Override
    public LivingEntity getEntity();

    /**
     * The npc's head will look in this direction
     *
     * @param toFace the direction to look
     */
    public void faceLocation(Location toFace);

    /**
     * Returns if the npc is an a condition to walk
     *
     * @return if the npc is an a condition to walk
     */
    public boolean isAbleToWalk();

    /**
     * Walk to the specified location
     *
     * @param l the location to walk to
     *
     * @throws java.lang.IllegalStateException if not in a condition to walk
     */
    public void walkTo(Location l);

    /**
     * Play the specified animation to all clients
     *
     * @param animation the animation to play
     *
     * @throws java.lang.IllegalArgumentException if the annotation can't be played on this type of npc
     * @throws java.lang.IllegalStateException if the npc is not spawned
     * @throws java.lang.UnsupportedOperationException if this implementation doesn't support the specified animation
     */
    public void animate(Animation animation);

}
