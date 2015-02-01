package net.techcable.npclib.nms.versions.v1_7_R4.network;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.NetworkManager;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import net.techcable.npclib.nms.versions.v1_7_R4.EntityNPCPlayer;
import net.techcable.npclib.nms.versions.v1_7_R4.NMS;
import lombok.*;

@Getter
public class NPCConnection extends PlayerConnection {

	public NPCConnection(EntityNPCPlayer npc) {
		super(NMS.getServer(), new NPCNetworkManager(), npc);
	}
	
	@Override
	public void sendPacket(Packet packet) {
		//Don't send packets to an npc
	}
}
