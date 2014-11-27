package techcable.minecraft.npclib.nms.versions.v1_7_R4.network;

import techcable.minecraft.npclib.nms.versions.v1_7_R4.EntityNPCPlayer;
import techcable.minecraft.npclib.nms.versions.v1_7_R4.NMS;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.NetworkManager;
import net.minecraft.server.v1_7_R4.PlayerConnection;

import lombok.*;

@Getter
public class NPCConnection extends PlayerConnection {

	public NPCConnection(EntityNPCPlayer npc) {
		super(NMS.getServer(), new NPCNetworkManager(), npc);
	}

}
