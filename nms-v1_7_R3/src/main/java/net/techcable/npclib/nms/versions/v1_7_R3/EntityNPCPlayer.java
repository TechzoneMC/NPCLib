package net.techcable.npclib.nms.versions.v1_7_R3;

import java.util.UUID;

import net.minecraft.server.v1_7_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;

import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.techcable.npclib.NPC;
import net.techcable.npclib.nms.versions.v1_7_R3.network.NPCConnection;
import lombok.*;

@Getter
public class EntityNPCPlayer extends EntityPlayer {
	private final NPC npc;
    public EntityNPCPlayer(NPC npc, String name, Location location, GameProfile profile) {
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