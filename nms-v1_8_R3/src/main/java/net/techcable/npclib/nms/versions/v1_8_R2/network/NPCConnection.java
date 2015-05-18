package net.techcable.npclib.nms.versions.v1_8_R2.network;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.techcable.npclib.nms.versions.v1_8_R2.EntityNPCPlayer;
import net.techcable.npclib.nms.versions.v1_8_R2.NMS;
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
