package net.techcable.npclib.nms.versions.v1_8_R2;

import java.lang.reflect.Field;
import java.util.*;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R2.*;
import net.minecraft.server.v1_8_R2.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;

import net.techcable.npclib.NPC;
import net.techcable.npclib.nms.OptionalFeature;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

import net.techcable.npclib.util.ProfileUtils;
import net.techcable.npclib.util.ReflectUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R2.CraftServer;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
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
        handle.aI = yaw; //MCP = roatationYawHead ---- SRG=field_70759_as
        if (!(handle instanceof EntityHuman))
            handle.aG = yaw; //MCP = renderYawOffset ---- SRG=field_70761_aq
        handle.aJ = yaw; //MCP = prevRotationYawHead ---- SRG=field_70758_at
    }
    
    @Override
    public Player spawnPlayer(Location toSpawn, String name, NPC npc) {
        GameProfile profile = skinMap.get(npc.getUUID()) == null ? new GameProfile(npc.getUUID(), npc.getName()) : skinMap.get(npc.getUUID());
    	EntityNPCPlayer player = new EntityNPCPlayer(npc, profile, toSpawn);
    	sendPacketsTo(Bukkit.getOnlinePlayers(), new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, player));
    	WorldServer world = getHandle(toSpawn.getWorld());
    	world.addEntity(player);
    	look(player.getBukkitEntity(), toSpawn.getPitch(), toSpawn.getYaw());
    	return player.getBukkitEntity();
    }


    private Map<UUID, GameProfile> skinMap = new HashMap<>();
    private static final Field playerInfoActionField = PacketPlayOutPlayerInfo.class.getDeclaredFields()[0];
    private static final Field playerInfoDataListField = PacketPlayOutPlayerInfo.class.getDeclaredFields()[1];
    @Override
    public boolean setSkin(NPC npc, ProfileUtils.PlayerProfile skinProfile) {
        GameProfile profile = makeProfile(npc, skinProfile);
        if (npc.isSpawned()) {
            PacketPlayOutPlayerInfo removePacket = new PacketPlayOutPlayerInfo();
            ReflectUtil.setField(playerInfoActionField, removePacket, EnumPlayerInfoAction.REMOVE_PLAYER);
            List<PacketPlayOutPlayerInfo.PlayerInfoData> removePacketDataList = ReflectUtil.getField(playerInfoDataListField, removePacket);
            removePacketDataList.add(removePacket.new PlayerInfoData(profile, 0, null, null));
            PacketPlayOutPlayerInfo addPacket = new PacketPlayOutPlayerInfo();
            ReflectUtil.setField(playerInfoActionField, addPacket, EnumPlayerInfoAction.ADD_PLAYER);
            List<PacketPlayOutPlayerInfo.PlayerInfoData> addPlayerDataList = ReflectUtil.getField(playerInfoDataListField, removePacket);
            EntityPlayer handle = getHandle((Player) npc.getEntity());
            WorldSettings.EnumGamemode gamemode = handle.playerInteractManager.getGameMode();
            IChatBaseComponent displayName = handle.getPlayerListName();
            addPlayerDataList.add(addPacket.new PlayerInfoData(profile, 0, gamemode, displayName));
        } else skinMap.put(npc.getUUID(), profile);
        return false;
    }

    private static GameProfile makeProfile(NPC npc, ProfileUtils.PlayerProfile skinProfile) {
        GameProfile profile = new GameProfile(npc.getUUID(), npc.getName());
        if (skinProfile != null && skinProfile.getProperties() != null) {
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
	
	public static final int[] UPDATE_ALL_SLOTS = new int[] {0, 1, 2, 3, 4};
	@Override
	public void notifyOfEquipmentChange(Player[] toNotify, Player rawNpc, int... slots) {
	    EntityPlayer npc = getHandle(rawNpc);
	    slots = slots.length == 0 ? UPDATE_ALL_SLOTS : slots;
	    List<Packet> packets = new ArrayList<>();
	    for (int slot : slots) {
	        packets.add(new PacketPlayOutEntityEquipment(npc.getId(), slot, npc.getEquipment(slot)));
	    }
	    sendPacketsTo(Arrays.asList(toNotify), packets.toArray(new Packet[packets.size()]));
	}
        
        public void sendPacketsTo(Iterable<? extends Player> recipients, Packet... packets) {
            Iterable<EntityPlayer> nmsRecipients = Iterables.transform(recipients, new Function<Player, EntityPlayer>() {
                @Override
                public EntityPlayer apply(Player input) {
                    return getHandle(input);
                }
            });
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

	@Override
	public void onJoin(Player joined, Collection<? extends NPC> npcs) {
		npcs = Collections2.filter(npcs, new Predicate<NPC>() {
			@Override
			public boolean apply(NPC npc) {
				return npc.isSpawned() && npc instanceof Player;
			}
		});
		Collection<? extends EntityPlayer> npcEntities = Collections2.transform(npcs, new Function<NPC, EntityPlayer>() {
			@Override
			public EntityPlayer apply(NPC arg0) {
				return getHandle((Player)arg0.getEntity());
			}
		});
		sendPacketsTo(Arrays.asList(joined), new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npcEntities.toArray(new EntityPlayer[npcEntities.size()])));
	}

	@Override
	public void onDespawn(NPC npc) {
            sendPacketsTo(Bukkit.getOnlinePlayers(), new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, getHandle((Player)npc.getEntity())));
	    WorldServer world = getHandle(npc.getEntity().getLocation().getWorld());
            world.removeEntity(getHandle(npc.getEntity()));
	}
}
