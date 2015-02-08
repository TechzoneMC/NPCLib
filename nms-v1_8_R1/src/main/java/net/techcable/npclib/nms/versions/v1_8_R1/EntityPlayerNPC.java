package net.techcable.npclib.nms.versions.v1_8_R1;

import java.util.UUID;

import net.minecraft.server.v1_8_R1.BlockPosition;
import net.minecraft.server.v1_8_R1.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R1.Packet;
import net.minecraft.server.v1_8_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_8_R1.PacketPlayOutBed;
import net.minecraft.server.v1_8_R1.PacketPlayOutPlayerInfo
import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.nms.EntityHumanNPC;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class EntityPlayerNPC extends EntityNPCImpl implements EntityHumanNPC {
    
    public EntityPlayerNPC(HumanNPC npc, Location toSpawn) {
        super(NMS.getServer(), NMS.getHandle(location.getWorld()), makeProfile(npc.getName(), npc.getSkin()), new PlayerInteractManager(NMS.getHandle(location.getWorld())));
    }
    
    @Override
    public HumanNPC getNpc() {
        return (HumanNPC) super.getNpc();
    }
    @Override
    public Player getEntity() {
        return (Player) super.getEntity();
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
	
	private void playAnimation(int id) {
	    sendPacketsTo(Bukkit.getOnlinePlayers(), new PacketPlayOutAnimation(this, id));
	}
	
	public static GameProfile makeProfile(String name, UUID skinId) {
		GameProfile profile = new GameProfile(UUID.randomUUID(), name);
		if (skinId != null) {
			GameProfile skin = new GameProfile(skinId, null);
			skin = NMS.getServer().av().fillProfileProperties(skin, true); //Srg = func_147130_as
			if (skin.getProperties().get("textures") == null || !skin.getProperties().get("textures").isEmpty()) {
				Property textures = skin.getProperties().get("textures").iterator().next();
				profile.getProperties().put("textures", textures);
			}
		}
		return profile;	
	}
}