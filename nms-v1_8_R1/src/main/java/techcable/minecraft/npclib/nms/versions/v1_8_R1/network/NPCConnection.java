package techcable.minecraft.npclib.nms.versions.v1_8_R1.network;

import techcable.minecraft.npclib.nms.versions.v1_8_R1.EntityNPCPlayer;
import techcable.minecraft.npclib.nms.versions.v1_8_R1.NMS;

import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.MinecraftServer;
import net.minecraft.server.v1_8_R1.NetworkManager;
import net.minecraft.server.v1_8_R1.Packet;
import net.minecraft.server.v1_8_R1.PlayerConnection;

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
