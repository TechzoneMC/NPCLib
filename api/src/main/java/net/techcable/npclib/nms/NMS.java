package net.techcable.npclib.nms;

import java.util.UUID;

import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.NPC;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface NMS {
	public boolean isSupported(OptionalFeature feature);
    public EntityHumanNPC createPlayer(HumanNPC npc, Location initialLocation);
    public EntityNPC getAsNPC(Entity entity);
}