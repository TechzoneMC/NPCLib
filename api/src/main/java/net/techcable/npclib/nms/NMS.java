package net.techcable.npclib.nms;

import java.util.Collection;
import java.util.UUID;

import net.techcable.npclib.NPC;

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
    public NPC getAsNPC(Entity entity);
    public void notifyOfEquipmentChange(Player[] toNotify, Player npc, int... slot);

    //Events
    public void onJoin(Player joined, Collection<? extends NPC> npcs);
    public void onDespawn(NPC npc);
}