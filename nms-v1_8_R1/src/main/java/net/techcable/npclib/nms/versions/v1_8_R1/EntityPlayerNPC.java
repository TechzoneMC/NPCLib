package net.techcable.npclib.nms.versions.v1_8_R1;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.server.v1_8_R1.BlockPosition;
import net.minecraft.server.v1_8_R1.DamageSource;
import net.minecraft.server.v1_8_R1.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R1.EnumGamemode;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.Packet;
import net.minecraft.server.v1_8_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_8_R1.PacketPlayOutBed;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R1.PlayerInteractManager;
import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.nms.EntityHumanNPC;
import net.techcable.npclib.nms.versions.v1_8_R1.network.NPCConnection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import lombok.*;

public class EntityPlayerNPC extends EntityPlayer implements EntityHumanNPC {
    
    public EntityPlayerNPC(HumanNPC npc, Location toSpawn) {
        super(NMS.getServer(), NMS.getHandle(toSpawn.getWorld()), makeProfile(npc.getName(), npc.getSkin()), new PlayerInteractManager(NMS.getHandle(toSpawn.getWorld())));
        
        playerInteractManager.b(EnumGamemode.SURVIVAL); //MCP = initializeGameType
		this.npc = npc;
		playerConnection = new NPCConnection(this);
		
		setPosition(toSpawn.getX(), toSpawn.getY(), toSpawn.getZ());
    }
    
    @Override
    public Player getEntity() {
        return getBukkitEntity();
    }
    
    private boolean lying = false;
    
    public void lie(Location toLie) {
        if (!lying) {
            Packet packet = new PacketPlayOutBed(this, new BlockPosition(this));
            sendPacketsTo(Bukkit.getOnlinePlayers(), packet);
            lying = true;
        }
    }
    
	public void wake() {
	    if (lying) {
	        playAnimation(2);
	        lying = false;
	    }
	}
	public void eat() {
	    playAnimation(3);
	}
	public void swing() {
	    playAnimation(0);
	}
	
	@Override
	public void onJoin(Player player) {
	    sendPacketsTo(player, new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, this));
	    if (lying) {
	        Packet packet = new PacketPlayOutBed(this, new BlockPosition(this));
            sendPacketsTo(player, packet);
	    }
	}
	
	@Override
	public void onDespawn() {
	    sendPacketsTo(Bukkit.getOnlinePlayers(), new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, this));
	}
	
	@Override
	public void onTick() {}
	
	private void playAnimation(int id) {
	    sendPacketsTo(Bukkit.getOnlinePlayers(), new PacketPlayOutAnimation(this, id));
	}
	
	public static GameProfile makeProfile(String name, UUID skinId) {
		GameProfile profile = new GameProfile(UUID.randomUUID(), name);
		if (skinId != null) {
			GameProfile skin = new GameProfile(skinId, null);
			skin = NMS.getServer().aB().fillProfileProperties(skin, true); //Srg = func_147130_as
			if (skin.getProperties().get("textures") == null || !skin.getProperties().get("textures").isEmpty()) {
				Property textures = skin.getProperties().get("textures").iterator().next();
				profile.getProperties().put("textures", textures);
			}
		}
		return profile;	
	}

	@Getter
	private final HumanNPC npc;
	
	public static final int[] UPDATE_ALL = new int[] {0, 1, 2, 3, 4};
	@Override
	public void notifyOfEquipmentChange(Player[] toNotify, int... slots) {
	    slots = slots.length == 0 ? UPDATE_ALL : slots;
	    List<Packet> packets = new ArrayList<>();
	    for (int slot : slots) {
	        packets.add(new PacketPlayOutEntityEquipment(getId(), slot, getEquipment(slot)));
	    }
	    sendPacketsTo(toNotify, (Packet[])packets.toArray());
	}
	
	public void look(double pitch, double yaw) {
	    this.pitch = (float) pitch;
	    this.yaw = (float)yaw;
	    setHeadYaw((float)yaw);
	}
	
	//Overrides
	
	@Override
	public boolean damageEntity(DamageSource source, float damage) {
		if (getNpc().isProtected()) {
			return false;
		}
		return super.damageEntity(source, damage);
	}
	
	//Utils
	public void sendPacketsTo(Player[] recipients, Packet... packets) {
	    EntityPlayer[] nmsRecipients = NMS.getHandles(recipients);
		for (EntityPlayer recipient : nmsRecipients) {
			if (recipient == null) continue;
			for (Packet packet : packets) {
			    if (packet == null) continue;
			    recipient.playerConnection.sendPacket(packet);
			}
		}
	}
	
	public void sendPacketsTo(Player recipient, Packet... packets) {
	    sendPacketsTo(new Player[] {recipient}, packets);
	}
	
    /**
     * This is copied from citizens
     * I added comments giving mcp mappings
     * @param entity
     * @param yaw the
     */
    public void setHeadYaw(float yaw) {
        while (yaw < -180.0F) {
            yaw += 360.0F;
        }

        while (yaw >= 180.0F) {
            yaw -= 360.0F;
        }
        this.aI = yaw; //MCP = roatationYawHead ---- SRG=field_70759_as
        if (!(getNpc().getEntity() instanceof Player)) this.aG = yaw; //MCP = renderYawOffset ---- SRG=field_70761_aq
        this.aJ = yaw; //MCP = prevRotationYawHead ---- SRG=field_70758_at
    }
}