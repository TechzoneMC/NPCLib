package net.techcable.npclib.nms.versions.v1_8_R1;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;

import net.minecraft.server.v1_8_R1.DamageSource;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.EnumGamemode;
import net.minecraft.server.v1_8_R1.PlayerInteractManager;
import com.mojang.authlib.properties.Property;
import net.techcable.npclib.NPC;
import net.techcable.npclib.nms.versions.v1_8_R1.network.NPCConnection;

import com.mojang.authlib.GameProfile;

import lombok.*;

@Getter
public class EntityNPCPlayer extends EntityPlayer {
	private final NPC npc;
	public EntityNPCPlayer(NPC npc, GameProfile profile, Location location) {
		super(NMS.getServer(), NMS.getHandle(location.getWorld()), profile, new PlayerInteractManager(NMS.getHandle(location.getWorld())));
		playerInteractManager.b(EnumGamemode.SURVIVAL); //MCP = initializeGameType ---- SRG=func_73077_b
		this.npc = npc;
		playerConnection = new NPCConnection(this);
		
		setPosition(location.getX(), location.getY(), location.getZ());
	}
	
	@Override
	public boolean damageEntity(DamageSource source, float damage) {
		if (npc.isProtected()) {
			return false;
		}
		return super.damageEntity(source, damage);
	}
}
