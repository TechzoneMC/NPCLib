package net.techcable.npclib.nms;

import java.util.Collection;
import java.util.UUID;

import net.techcable.npclib.NPC;

import net.techcable.npclib.nms.skins.RateLimitedException;
import net.techcable.npclib.util.ProfileUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface NMS {
	public boolean isSupported(OptionalFeature feature);
    public void look(Entity entity, float pitch, float yaw);
    public Player spawnPlayer(Location toSpawn, String name, NPC npc);

    /**
     * Sets the skin of the npc
     *
     * <p>
     * It is up to the implementation to choose whether or not to refresh the player's skin
     * </p>
     *
     * @param npc the npc to set the skin of
     * @param skinProfile the npc's new skin
     * @return true if the npc is needed to respawn
     */
    public boolean setSkin(NPC npc, ProfileUtils.PlayerProfile skinProfile);
    public NPC getAsNPC(Entity entity);
    public void notifyOfEquipmentChange(Player[] toNotify, Player npc, int... slot);

    //Events
    public void onJoin(Player joined, Collection<? extends NPC> npcs);
    public void onDespawn(NPC npc);
}