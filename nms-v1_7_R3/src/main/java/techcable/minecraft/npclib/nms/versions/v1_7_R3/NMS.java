package techcable.minecraft.npclib.nms.versions.v1_7_R3;

import java.lang.reflect.Field;
import java.util.UUID;

import net.minecraft.server.v1_7_R3.ChunkCoordinates;
import net.minecraft.server.v1_7_R3.Entity;
import net.minecraft.server.v1_7_R3.EntityHuman;
import net.minecraft.server.v1_7_R3.EntityLiving;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.IChatBaseComponent;
import net.minecraft.server.v1_7_R3.MinecraftServer;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_7_R3.World;
import net.minecraft.server.v1_7_R3.WorldServer;
import net.minecraft.util.com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_7_R3.CraftServer;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
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
        handle.aO = yaw; //MCP = roatationYawHead
        if (!(handle instanceof EntityHuman))
            handle.aM = yaw; //MCP = renderYawOffset
        handle.aP = yaw; //MCP = prevRotationYawHead
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
    
    public static EntityPlayer getHandle(Player bukkitPlayer) {
    	if (!(bukkitPlayer instanceof CraftPlayer)) return null;
    	return ((CraftPlayer)bukkitPlayer).getHandle();
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

	@Override
	public void notifyOfSpawn(Player[] toNotify, Player... players) {}

	@Override
	public void notifyOfDespawn(Player[] toNotify, Player... npcs) {}
	
	@Override
	public void notifyOfEquipmentChange(Player[] toNotify, Player rawNpc) {
	    EntityPlayer npc = getHandle(rawNpc);
	    Packet[] packets = new Packet[5];
	    for (int i = 0; i < 5; i++) {
	        packets[i] = new PacketPlayOutEntityEquipment(npc.getId(), i, npc.getEquipment(i));
	    }
	    sendPacketsTo(toNotify, packets);
	}
	
	public void sendPacketsTo(Player[] recipients, Packet... packets) {
	    EntityPlayer[] nmsToSend = new EntityPlayer[recipients.length];
	    for (int i = 0; i < recipients.length; i++) {
	        nmsToSend[i] = getHandle(recipients[i]);
	    }
		for (EntityPlayer recipient : nmsToSend) {
			if (recipient == null) continue;
			for (Packet packet : packets) {
			    if (packet == null) continue;
			    recipient.playerConnection.sendPacket(packet);
			}
		}
	}
}