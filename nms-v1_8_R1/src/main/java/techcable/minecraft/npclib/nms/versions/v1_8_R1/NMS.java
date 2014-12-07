package techcable.minecraft.npclib.nms.versions.v1_8_R1;

import java.lang.reflect.Field;
import java.util.UUID;

import net.minecraft.server.v1_8_R1.Entity;
import net.minecraft.server.v1_8_R1.EntityHuman;
import net.minecraft.server.v1_8_R1.EntityLiving;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.MinecraftServer;
import net.minecraft.server.v1_8_R1.World;
import net.minecraft.server.v1_8_R1.WorldServer;
import com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R1.CraftServer;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import techcable.minecraft.npclib.NPC;
import techcable.minecraft.npclib.util.ReflectUtil;

public class NMS implements techcable.minecraft.npclib.nms.NMS {
	
	private static NMS instance;
	public NMS() {
		if (instance == null) instance = this;
	}
	
	public static NMS getInstance() {
		return instance;
	}
	
    public void look(org.bukkit.entity.Entity entity, float pitch, float yaw) {
        Entity handle = getHandle(entity);
        if (handle == null) return;
        handle.yaw = yaw;
        setHeadYaw(getHandle(entity), yaw);
        handle.pitch = pitch;
    }
    
    /**
     * This is copied from citizens
     * I added comments giving mcp mappings
     * @param entity
     * @param yaw the
     */
    public static void setHeadYaw(Entity entity, float yaw) {
        if (!(entity instanceof EntityLiving))
            return;
        EntityLiving handle = (EntityLiving) entity;
        while (yaw < -180.0F) {
            yaw += 360.0F;
        }

        while (yaw >= 180.0F) {
            yaw -= 360.0F;
        }
        handle.aI = yaw; //MCP = roatationYawHead ---- SRG=field_70759_as
        if (!(handle instanceof EntityHuman))
            handle.aG = yaw; //MCP = renderYawOffset ---- SRG=field_70761_aq
        handle.aJ = yaw; //MCP = prevRotationYawHead ---- SRG=field_70758_at
    }
    
    @Override
    public Player spawnPlayer(Location toSpawn, String name, NPC npc) {
    	EntityNPCPlayer player = new EntityNPCPlayer(npc, name, toSpawn);
    	WorldServer world = getHandle(toSpawn.getWorld());
    	world.addEntity(player);
    	look(player.getBukkitEntity(), toSpawn.getPitch(), toSpawn.getYaw());
    	return player.getBukkitEntity();
    }
    
    public static Entity getHandle(org.bukkit.entity.Entity bukkitEntity) {
        if (!(bukkitEntity instanceof CraftEntity))
            return null;
        return ((CraftEntity)bukkitEntity).getHandle();
    }

    public static EntityHuman getHandle(HumanEntity bukkitHuman) {
    	if (!(bukkitHuman instanceof CraftHumanEntity)) return null;
    	return ((CraftHumanEntity)bukkitHuman).getHandle();
    }
    
    public static MinecraftServer getHandle(org.bukkit.Server bukkitServer) {
    	if (bukkitServer instanceof CraftServer) {
    		return ((CraftServer)bukkitServer).getServer();
    	} else {
    		return null;
    	}
    }
    
    public static WorldServer getHandle(org.bukkit.World bukkitWorld) {
    	if (bukkitWorld instanceof CraftWorld) {
    		return ((CraftWorld)bukkitWorld).getHandle();
    	} else {
    		return null;
    	}
    }
	
	public static MinecraftServer getServer() {
		return getHandle(Bukkit.getServer());
	}

	@Override
	public NPC getAsNPC(org.bukkit.entity.Entity entity) {
		if (getHandle(entity) instanceof EntityNPCPlayer) {
			EntityNPCPlayer player = (EntityNPCPlayer) getHandle(entity);
			return player.getNpc();
		} else {
			return null;
		}
	}
	
	
}