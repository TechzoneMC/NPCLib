package techcable.minecraft.npclib.nms.versions.v1_7_R3.network;

import techcable.minecraft.npclib.nms.versions.v1_7_R3.EntityNPCPlayer;
import techcable.minecraft.npclib.nms.versions.v1_7_R3.NMS;

import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.MinecraftServer;
import net.minecraft.server.v1_7_R3.NetworkManager;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PlayerConnection;

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
