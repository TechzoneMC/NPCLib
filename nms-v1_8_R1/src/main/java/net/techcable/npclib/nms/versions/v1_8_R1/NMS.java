package net.techcable.npclib.nms.versions.v1_8_R1;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.server.v1_8_R1.Entity;
import net.minecraft.server.v1_8_R1.EntityHuman;
import net.minecraft.server.v1_8_R1.EntityLiving;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.MinecraftServer;
import net.minecraft.server.v1_8_R1.Packet;
import net.minecraft.server.v1_8_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R1.World;
import net.minecraft.server.v1_8_R1.WorldServer;
import net.techcable.npclib.NPC;
import net.techcable.npclib.nms.OptionalFeature;
import net.techcable.npclib.util.ReflectUtil;

import com.mojang.authlib.GameProfile;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R1.CraftServer;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class NMS implements net.techcable.npclib.nms.NMS {
	
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
    
    public static EntityPlayer getHandle(Player bukkitPlayer) {
    	if (!(bukkitPlayer instanceof CraftPlayer)) return null;
    	return ((CraftPlayer)bukkitPlayer).getHandle();
    }
    
    public static EntityPlayer[] getHandles(Player[] bukkitPlayers) {
    	EntityPlayer[] handles = new EntityPlayer[bukkitPlayers.length];
    	for (int i = 0; i < bukkitPlayers.length; i++) {
    		handles[i] = getHandle(bukkitPlayers[i]);
    	}
    	return handles;
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
	public void notifyOfSpawn(Player[] toNotify, Player... npcs) {
		EntityPlayer[] nmsNpcs = getHandles(npcs);
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, nmsNpcs);
		sendPacketsTo(toNotify, packet);
	}

	@Override
	public void notifyOfDespawn(Player[] toNotify, Player... npcs) {
		EntityPlayer[] nmsNpcs = getHandles(npcs);
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, nmsNpcs);
		sendPacketsTo(toNotify, packet);
	}
	
	public static final int[] UPDATE_ALL_SLOTS = new int[] {0, 1, 2, 3, 4};
	@Override
	public void notifyOfEquipmentChange(Player[] toNotify, Player rawNpc, int... slots) {
	    EntityPlayer npc = getHandle(rawNpc);
	    slots = slots.length == 0 ? UPDATE_ALL_SLOTS : slots;
	    List<Packet> packets = new ArrayList<>();
	    for (int slot : slots) {
	        packets.add(new PacketPlayOutEntityEquipment(npc.getId(), slot, npc.getEquipment(slot)));
	    }
	    sendPacketsTo(toNotify, (Packet[])packets.toArray());
	}
	
	public void sendPacketsTo(Player[] recipients, Packet... packets) {
	    EntityPlayer[] nmsRecipients = getHandles(recipients);
		for (EntityPlayer recipient : nmsRecipients) {
			if (recipient == null) continue;
			for (Packet packet : packets) {
			    if (packet == null) continue;
			    recipient.playerConnection.sendPacket(packet);
			}
		}
	}

	@Override
	public boolean isSupported(OptionalFeature feature) {
		switch (feature) {
		case SKINS :
			return true;
		default :
			return false;
		}
	}
}