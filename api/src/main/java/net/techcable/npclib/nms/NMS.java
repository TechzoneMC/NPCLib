package net.techcable.npclib.nms;

import java.util.Collection;

import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.LivingNPC;
import net.techcable.npclib.NPC;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public interface NMS {

    public IHumanNPCHook spawnHumanNPC(Location toSpawn, HumanNPC npc);

    //Events
    public void onJoin(Player joined, Collection<? extends NPC> npcs);
}