package net.techcable.npclib.nms.versions.v1_7_R4;

import java.lang.reflect.Field;
import java.util.*;

import net.minecraft.server.v1_7_R4.ChunkCoordinates;
import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.IChatBaseComponent;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_7_R4.World;
import net.minecraft.server.v1_7_R4.WorldServer;
import net.minecraft.util.com.google.common.base.Function;
import net.minecraft.util.com.google.common.base.Predicate;
import net.minecraft.util.com.google.common.collect.Collections2;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.techcable.npclib.NPC;
import net.techcable.npclib.nms.OptionalFeature;
import net.techcable.npclib.util.ProfileUtils;
import net.techcable.npclib.util.ReflectUtil;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

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
        handle.aO = yaw; //MCP = roatationYawHead
        if (!(handle instanceof EntityHuman))
            handle.aM = yaw; //MCP = renderYawOffset
        handle.aP = yaw; //MCP = prevRotationYawHead
    }

    private Map<UUID, GameProfile> profileMap = new HashMap<>();
    @Override
    public Player spawnPlayer(Location toSpawn, String name, NPC npc) {
        GameProfile profile = profileMap.get(npc.getUUID()) != null ? profileMap.get(npc.getUUID()) : new GameProfile(npc.getUUID(), npc.getName());
    	EntityNPCPlayer player = new EntityNPCPlayer(npc, profile, toSpawn);
    	if (ProtocolHack.isProtocolHack()) {
    		ProtocolHack.notifyOfSpawn(Bukkit.getOnlinePlayers(), player.getBukkitEntity());
    	}
    	WorldServer world = getHandle(toSpawn.getWorld());
        world.addEntity(player);
    	look(player.getBukkitEntity(), toSpawn.getPitch(), toSpawn.getYaw());
    	return player.getBukkitEntity();
    }

    @Override
    public boolean setSkin(NPC npc, ProfileUtils.PlayerProfile skinProfile) {
        GameProfile profile = makeProfile(npc, skinProfile);
        profileMap.put(npc.getUUID(), profile);
        return true;
    }

    private static GameProfile makeProfile(NPC npc, ProfileUtils.PlayerProfile skinProfile) {
        GameProfile profile = new GameProfile(npc.getUUID(), npc.getName());
        if (skinProfile.getProperties() != null) {
            for (Object obj : skinProfile.getProperties()) {
                JSONObject jsonProperty = (JSONObject) obj;
                String name = (String) jsonProperty.get("name");
                String value = (String) jsonProperty.get("value");
                String signature = jsonProperty.containsKey("signature") ? (String) jsonProperty.get("signature") : null;
                Property property = signature == null ? new Property(name, value) : new Property(name, value, signature);
                profile.getProperties().put(name, property);
            }
        }
        return profile;
    }


    public static Entity getHandle(org.bukkit.entity.Entity bukkitEntity) {
        if (!(bukkitEntity instanceof CraftEntity))
            return null;
        return ((CraftEntity)bukkitEntity).getHandle();
    }

    public static EntityPlayer getHandle(Player bukkitPlayer) {
    	if (!(bukkitPlayer instanceof CraftPlayer)) return null;
    	return ((CraftPlayer)bukkitPlayer).getHandle();
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

	public static final int[] UPDATE_ALL_SLOTS = new int[] {0, 1, 2, 3, 4};
	@Override
	public void notifyOfEquipmentChange(Player[] toNotify, Player rawNpc, int... slots) {
	    EntityPlayer npc = getHandle(rawNpc);
	    slots = slots.length == 0 ? UPDATE_ALL_SLOTS : slots;
	    List<Packet> packets = new ArrayList<>();
	    for (int slot : slots) {
	        packets.add(new PacketPlayOutEntityEquipment(npc.getId(), slot, npc.getEquipment(slot)));
	    }
	    sendPacketsTo(toNotify, packets.toArray(new Packet[packets.size()]));
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

	@Override
	public boolean isSupported(OptionalFeature feature) {
		switch (feature) {
			case SKINS :
				return true;
			default :
				return false;
		}
	}

	@Override
	public void onJoin(Player joined, Collection<? extends NPC> npcs) {
		if (ProtocolHack.isProtocolHack()) {
			npcs = Collections2.filter(npcs, new Predicate<NPC>() {
				@Override
				public boolean apply(NPC arg0) {
					return arg0.isSpawned() && arg0 instanceof Player;
				}
			});
			Collection<? extends Player> npcEntities = Collections2.transform(npcs, new Function<NPC, Player>() {
				@Override
				public Player apply(NPC npc) {
					return (Player) npc.getEntity();
				}
			});
			ProtocolHack.notifyOfSpawn(Arrays.asList(joined), npcEntities.toArray(new Player[npcEntities.size()]));
		}
	}

	@Override
	public void onDespawn(NPC npc) {
		if (ProtocolHack.isProtocolHack()) {
			ProtocolHack.notifyOfDespawn(Bukkit.getOnlinePlayers(), (Player)npc.getEntity());
		}
	}
}
