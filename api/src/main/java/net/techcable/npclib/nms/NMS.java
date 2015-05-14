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
    public IHumanNPCHook spawnHumanNPC(Location toSpawn, String name, NPC npc);
    public NPC getAsNPC(Entity entity);

    //Events
    public void onJoin(Player joined, Collection<? extends NPC> npcs);
}