package net.techcable.npclib.nms.versions.v1_7_R4;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;

import net.minecraft.server.v1_7_R4.DamageSource;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.EnumGamemode;
import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.techcable.npclib.NPC;
import net.techcable.npclib.nms.versions.v1_7_R4.network.NPCConnection;
import lombok.*;

@Getter
public class EntityNPCPlayer extends EntityPlayer {
	private final NPC npc;
	public EntityNPCPlayer(NPC npc, GameProfile profile, Location location) {
		super(NMS.getServer(), NMS.getHandle(location.getWorld()), profile, new PlayerInteractManager(NMS.getHandle(location.getWorld())));
		playerInteractManager.b(EnumGamemode.SURVIVAL); //MCP = initializeGameType
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
