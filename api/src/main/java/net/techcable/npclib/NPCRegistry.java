package net.techcable.npclib;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

public interface NPCRegistry {

	/**
	 * Creates a despawned human npc
     *
	 * @param name the name of the npc
	 * @return the new npc
	 */
	public HumanNPC createHumanNPC(String name);

	/**
	 * Creates a despawned human npc
     *
	 * @param uuid the uuid of the new npc
	 * @param name the name of the npc
	 * @return the new npc
	 */
	public HumanNPC createHumanNPC(UUID uuid, String name);

	/**
	 * Removes this npc from the registry
	 * Be careful not to remove despawned npcs
	 * @throws IllegalStateException if the npc is spawned
	 * @param npc the npc to deregister
	 */
	public void deregister(NPC npc);

	/**
	 * Deregisters every npc in the registry
	 */
	public void deregisterAll();
	/**
	 * Retreives the npc with the given UUID
	 * @param uuid the uuid to get the npc for
	 * @return the npc with this uuid
	 */
	public NPC getByUUID(UUID uuid);

    /**
     * Retreives a npc with the given name
     *
     * <p>
     * There may be multiple npcs with a name, so this method may not return the one you want
     * If you want to guarentee unqiueness, use uuids
     * </p>
     *
     * @deprecated there may be multiple npcs with the given name
     * @param name the name to get a npc with
     * @since 2.0
     * @return a npc with the given name
     */
    @Deprecated
    public NPC getByName(String name);

	/**
	 * Converts an Entity to an NPC
	 * @param entity the npc to get as an entity
	 * @return the NPC that represents this entity
	 */
	public NPC getAsNPC(Entity entity);
	/**
	 * Checks if the entity can be converted to an npc
	 * @param entity the entity to check
	 * @return true if the entity is an npc
	 */
	public boolean isNPC(Entity entity);
	
	/**
	 * Retreive every npc in the registry
	 * @return all the npcs in the registry
	 */
	public Collection<? extends NPC> listNpcs();
	
	/**
	 * Get this registry's owning plugin
	 * 
	 * @return the registry's plugin
	 */
	public Plugin getPlugin();

}
