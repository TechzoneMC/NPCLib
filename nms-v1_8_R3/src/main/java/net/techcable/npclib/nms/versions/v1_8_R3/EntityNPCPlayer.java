package net.techcable.npclib.nms.versions.v1_8_R3;

import com.mojang.authlib.GameProfile;

import lombok.*;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode;
import net.techcable.npclib.NPC;
import net.techcable.npclib.nms.versions.v1_8_R3.network.NPCConnection;
import org.bukkit.Location;

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
