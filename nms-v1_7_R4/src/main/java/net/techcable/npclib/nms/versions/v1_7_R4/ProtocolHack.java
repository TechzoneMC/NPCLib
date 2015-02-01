package net.techcable.npclib.nms.versions.v1_7_R4;

import java.lang.reflect.Method;

import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.Packet;
import net.techcable.npclib.util.ReflectUtil;

public class ProtocolHack {
    private ProtocolHack() {}
    
    private static EntityPlayer getHandle(Player bukkitPlayer) {
    	if (!(bukkitPlayer instanceof CraftPlayer)) return null;
    	return ((CraftPlayer)bukkitPlayer).getHandle();
    }

    private static EntityPlayer[] getHandles(Player[] bukkitPlayers) {
    	EntityPlayer[] handles = new EntityPlayer[bukkitPlayers.length];
    	for (int i = 0; i < bukkitPlayers.length; i++) {
    		handles[i] = getHandle(bukkitPlayers[i]);
    	}
    	return handles;
    }
    
    public static boolean isProtocolHack() {
        try {
            Class.forName("org.spigotmc.ProtocolData");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }
    
    public static void notifyOfSpawn(Player[] toNotify, Player... npcs) {
        Method addPlayer = ReflectUtil.makeMethod(getPlayerInfoClass(), "addPlayer", EntityPlayer.class);
        EntityPlayer[] handles = getHandles(npcs);
        Packet[] packets = new Packet[handles.length];
        for (int i = 0; i < handles.length; i++) {
            EntityPlayer handle = handles[i];
            Packet packet = ReflectUtil.callMethod(addPlayer, null, handle);
            packets[i] = packet;
        }
        sendPacketsTo(toNotify, packets);
    }
    
    public static void notifyOfDespawn(Player[] toNotify, Player... npcs) {
        Method removePlayer = ReflectUtil.makeMethod(getPlayerInfoClass(), "removePlayer", EntityPlayer.class);
        EntityPlayer[] handles = getHandles(npcs);
        Packet[] packets = new Packet[handles.length];
        for (int i = 0; i < handles.length; i++) {
            EntityPlayer handle = handles[i];
            Packet packet = ReflectUtil.callMethod(removePlayer, null, handle);
            packets[i] = packet;
        }
        sendPacketsTo(toNotify, packets);
    }
    
    public static Class <?> getPlayerInfoClass() {
        try {
            return Class.forName("net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static void sendPacketsTo(Player[] recipients, Packet... packets) {
	    EntityPlayer[] nmsRecipients = getHandles(recipients);
		for (EntityPlayer recipient : nmsRecipients) {
			if (recipient == null) continue;
			for (Packet packet : packets) {
			    if (packet == null) continue;
			    recipient.playerConnection.sendPacket(packet);
			}
		}
	}
}
