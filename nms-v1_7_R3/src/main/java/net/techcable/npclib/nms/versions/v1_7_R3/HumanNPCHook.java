package net.techcable.npclib.nms.versions.v1_7_R3;

import lombok.*;

import java.lang.reflect.Field;
import java.util.UUID;

import net.minecraft.server.v1_7_R3.PacketPlayOutPlayerInfo;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.nms.IHumanNPCHook;
import net.techcable.npclib.nms.versions.v1_7_R3.entity.EntityNPCPlayer;
import net.techcable.npclib.utils.Reflection;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HumanNPCHook extends LivingNPCHook implements IHumanNPCHook {

    public HumanNPCHook(HumanNPC npc, Location toSpawn) {
        super(npc);
        this.nmsEntity = spawn(npc, toSpawn);
        getNmsEntity().setHook(this);
        showInTablist();
    }

    @Override
    public void setSkin(UUID id) {
        boolean wasHidden = shownInTabList;
        NMS.setSkin(getNmsEntity().getProfile(), id);
        respawn();
    }

    private boolean shownInTabList;
    private final Object packetLock = new Object();

    @Synchronized("packetLock")
    @Override
    public void showInTablist() {
        if (shownInTabList) return;
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(getNmsEntity().getProfile().getName(), true, 0);
        NMS.sendToAll(packet);
        shownInTabList = true;
    }

    @Override
    @Synchronized("packetLock")
    public void hideFromTablist() {
        if (!shownInTabList) return;
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(getNmsEntity().getProfile().getName(), false, 0);
        NMS.sendToAll(packet);
        shownInTabList = false;
    }

    public EntityNPCPlayer getNmsEntity() {
        return (EntityNPCPlayer) super.getNmsEntity();
    }

    @Override
    public HumanNPC getNpc() {
        return (HumanNPC) super.getNpc();
    }

    private static final Field nameField = Reflection.makeField(GameProfile.class, "name");
    private static final Field modifiersField = Reflection.makeField(Field.class, "modifiers");

    @Override
    public void setName(String s) {
        respawn();
    }

    public void respawn() {
        Location lastLocation = getEntity().getLocation();
        boolean wasShown = shownInTabList;
        hideFromTablist();
        getNmsEntity().setHook(null);
        this.nmsEntity = spawn(getNpc(), lastLocation);
        getNmsEntity().setHook(this);
        showInTablist();
        if (!wasShown) hideFromTablist();
    }

    @Override
    public void onDespawn() {
        super.onDespawn();
        hideFromTablist();
    }

    private static EntityNPCPlayer spawn(HumanNPC npc, Location toSpawn) {
        return new EntityNPCPlayer(npc, toSpawn);
    }

    public void onJoin(Player joined) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(getNmsEntity().getProfile().getName(), true, 0);
        NMS.getHandle(joined).playerConnection.sendPacket(packet);
        if (!shownInTabList) {
            PacketPlayOutPlayerInfo removePacket = new PacketPlayOutPlayerInfo(getNmsEntity().getProfile().getName(), false, 0);
            NMS.getHandle(joined).playerConnection.sendPacket(packet);
        }
    }
}
