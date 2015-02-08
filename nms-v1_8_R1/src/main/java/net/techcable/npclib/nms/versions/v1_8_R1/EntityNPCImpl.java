package net.techcable.npclib.nms.versions.v1_8_R1;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R1.DamageSource;
import net.minecraft.server.v1_8_R1.EntityHuman;
import net.minecraft.server.v1_8_R1.EntityLiving;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.Packet;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityEquipment;
import net.techcable.npclib.NPC;
import net.techcable.npclib.nms.EntityNPC;

public class EntityNPCImpl extends EntityLiving implements EntityNPC {

	@Getter
	private final NPC npc;
	
	public EntityNPCImpl(NPC npc, Location initial) {
		super(NMS.getHandle(initial.getWorld()));
		this.npc = npc;
	}
	
	public static final int[] UPDATE_ALL = new int[] {0, 1, 2, 3, 4};
	@Override
	public void notifyOfEquipmentChange(Player[] toNotify, int... slots) {
	    slots = slots.length == 0 ? UPDATE_ALL : slots;
	    List<Packet> packets = new ArrayList<>();
	    for (int slot : slots) {
	        packets.add(new PacketPlayOutEntityEquipment(npc.getId(), slot, npc.getEquipment(slot)));
	    }
	    sendPacketsTo(toNotify, (Packet[])packets.toArray());
	}
	
	public Entity getEntity() {
	    return getBukkitEntity();
	}
	public void look(double pitch, double yaw) {
	    this.pitch = pitch;
	    this.yaw = yaw;
	    setHeadYaw(yaw);
	}

	//Event Hooks
	public void onDespawn() {}
	public void onJoin(Player joined) {}
	public void onTick() {}
	
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
