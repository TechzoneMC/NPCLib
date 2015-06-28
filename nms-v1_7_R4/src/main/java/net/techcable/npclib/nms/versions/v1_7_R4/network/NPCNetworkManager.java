package net.techcable.npclib.nms.versions.v1_7_R4.network;

import lombok.*;

import java.lang.reflect.Field;

import net.minecraft.server.v1_7_R4.NetworkManager;
import net.techcable.npclib.utils.Reflection;

@Getter
public class NPCNetworkManager extends NetworkManager {

    public NPCNetworkManager() {
        super(false); //MCP = isClientSide

        Field channel = Reflection.makeField(NetworkManager.class, "m"); //MCP = channel
        Field address = Reflection.makeField(NetworkManager.class, "n"); //MCP = address

        Reflection.setField(channel, this, new NullChannel());
        Reflection.setField(address, this, new NullSocketAddress());

    }

}
