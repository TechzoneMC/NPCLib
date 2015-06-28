package net.techcable.npclib.nms.versions.v1_7_R4;

import java.lang.reflect.Method;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.Packet;
import net.techcable.npclib.utils.Reflection;

public class ProtocolHack {

    private ProtocolHack() {
    }

    public static boolean isProtocolHack() {
        try {
            Class.forName("org.spigotmc.ProtocolData");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    private static final Method addPlayerMethod = Reflection.makeMethod(getPlayerInfoClass(), "addPlayer", EntityPlayer.class);

    public static Packet newPlayerInfoDataAdd(EntityPlayer player) {
        return Reflection.callMethod(addPlayerMethod, null, player);
    }

    private static final Method removePlayerMethod = Reflection.makeMethod(getPlayerInfoClass(), "removePlayer", EntityPlayer.class);

    public static Packet newPlayerInfoDataRemove(EntityPlayer player) {
        return Reflection.callMethod(removePlayerMethod, null, player);
    }

    public static Class<?> getPlayerInfoClass() {
        try {
            return Class.forName("net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
