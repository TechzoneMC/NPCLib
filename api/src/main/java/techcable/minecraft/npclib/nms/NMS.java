package techcable.minecraft.npclib.nms;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import techcable.minecraft.npclib.NPC;

public interface NMS {
    public void look(Entity entity, float pitch, float yaw);
    public Player spawnPlayer(Location toSpawn, String name, NPC npc);
    public void setName(HumanEntity human, String name);
    public NPC getAsNPC(Entity entity);
}
