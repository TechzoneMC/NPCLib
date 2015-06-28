package net.techcable.npclib.nms.versions.v1_8_R2.network;

import lombok.*;

import net.minecraft.server.v1_8_R2.EntityPlayer;
import net.minecraft.server.v1_8_R2.Packet;
import net.minecraft.server.v1_8_R2.PlayerConnection;
import net.techcable.npclib.nms.versions.v1_8_R2.NMS;

@Getter
public class NPCConnection extends PlayerConnection {

    public NPCConnection(EntityPlayer player) {
        super(NMS.getServer(), new NPCNetworkManager(), player);
    }

    @Override
    public void sendPacket(Packet packet) {
        //Don't send packets to an npc
    }
}
