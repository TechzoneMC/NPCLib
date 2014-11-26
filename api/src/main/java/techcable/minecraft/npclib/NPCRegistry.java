package techcable.minecraft.npclib;

import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public interface NPCRegistry {
	/**
	 * Creates a despawned npc
	 * @param type {@link EntityType} of the NPC
	 * @param name the name of the npc
	 * @return the new npc
	 */
	public NPC createNPC(EntityType type, String name);
	/**
	 * Creates a despawned npc
	 * @param type {@link EntityType} of the NPC
	 * @param uuid the uuid of the new npc
	 * @param name the name of the npc
	 * @return the new npc
	 */
	public NPC createNPC(EntityType type, UUID uuid, String name);
	/**
	 * Removes all data from the npc and deregisters it
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
	public Set<NPC> listNpcs();
}
