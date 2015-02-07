package net.techcable.npclib.nms.versions.v1_7_R3;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;

import net.minecraft.server.v1_7_R3.DamageSource;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.EnumGamemode;
import net.minecraft.server.v1_7_R3.PlayerInteractManager;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.techcable.npclib.NPC;
import net.techcable.npclib.nms.versions.v1_7_R3.network.NPCConnection;
import lombok.*;

@Getter
public class EntityNPCPlayer extends EntityPlayer {
	private final NPC npc;
	public EntityNPCPlayer(NPC npc, String name, Location location) {
		super(NMS.getServer(), NMS.getHandle(location.getWorld()), makeProfile(name, npc.getSkin()), new PlayerInteractManager(NMS.getHandle(location.getWorld())));
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