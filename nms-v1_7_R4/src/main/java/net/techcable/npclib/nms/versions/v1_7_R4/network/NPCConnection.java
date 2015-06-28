package net.techcable.npclib.nms.versions.v1_7_R4.network;

import lombok.*;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import net.techcable.npclib.nms.versions.v1_7_R4.NMS;

@Getter
public class NPCConnection extends PlayerConnection {

    public NPCConnection(EntityPlayer npc) {
        super(NMS.getServer(), new NPCNetworkManager(), npc);
    }

    @Override
    public void sendPacket(Packet packet) {
        //Don't send packets to an npc
    }
}
