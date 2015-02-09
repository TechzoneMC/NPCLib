package net.techcable.npclib.nms.versions.v1_8_R1.network;

import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.MinecraftServer;
import net.minecraft.server.v1_8_R1.NetworkManager;
import net.minecraft.server.v1_8_R1.Packet;
import net.minecraft.server.v1_8_R1.PlayerConnection;
import net.techcable.npclib.nms.versions.v1_8_R1.EntityPlayerNPC;
import net.techcable.npclib.nms.versions.v1_8_R1.NMS;
import lombok.*;

@Getter
public class NPCConnection extends PlayerConnection {

	public NPCConnection(EntityPlayerNPC npc) {
		super(NMS.getServer(), new NPCNetworkManager(), npc);
	}
	@Override
	public void sendPacket(Packet packet) {
		//Don't send packets to an npc
	}
}
