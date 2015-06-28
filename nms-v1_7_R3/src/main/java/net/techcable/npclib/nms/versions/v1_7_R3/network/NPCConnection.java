package net.techcable.npclib.nms.versions.v1_7_R3.network;

import lombok.*;

import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PlayerConnection;
import net.techcable.npclib.nms.versions.v1_7_R3.NMS;

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
