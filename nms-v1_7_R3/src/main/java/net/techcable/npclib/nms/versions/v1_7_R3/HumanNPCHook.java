package net.techcable.npclib.nms.versions.v1_7_R3;

import lombok.*;

import java.lang.reflect.Field;
import java.util.UUID;

import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutAnimation;
import net.minecraft.server.v1_7_R3.PacketPlayOutPlayerInfo;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.techcable.npclib.Animation;
import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.nms.IHumanNPCHook;
import net.techcable.npclib.nms.versions.v1_7_R3.entity.EntityNPCPlayer;
import net.techcable.npclib.utils.Reflection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

public class HumanNPCHook extends LivingNPCHook implements IHumanNPCHook {

    public HumanNPCHook(HumanNPC npc, Location toSpawn) {
        super(npc, toSpawn, EntityType.PLAYER);
        getNmsEntity().setHook(this);
    }

    @Override
    public void setSkin(UUID id) {
        NMS.setSkin(getNmsEntity().getProfile(), id);
        respawn();
    }

    private boolean shownInTabList;

    @Override
    public void showInTablist() {
        if (shownInTabList) return;
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(getNmsEntity().getProfile().getName(), true, 0);
        NMS.sendToAll(packet);
        shownInTabList = true;
    }

    @Override
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
        getNmsEntity().dead = true; // Kill old entity
        this.nmsEntity = spawn(lastLocation, EntityType.PLAYER);
        getNmsEntity().setHook(this);
        showInTablist();
        if (!wasShown) hideFromTablist();
    }

    @Override
    public void onDespawn() {
        hideFromTablist();
        super.onDespawn();
    }

    @Override
    protected EntityNPCPlayer spawn(Location toSpawn, EntityType type) {
        Preconditions.checkArgument(type == EntityType.PLAYER, "HumanNPCHook can only handle players");
        EntityNPCPlayer entity = new EntityNPCPlayer(getNpc(), toSpawn);
        this.nmsEntity = entity;
        showInTablist();
        this.nmsEntity = null;
        return entity;
    }

    @Override
    public void animate(Animation animation) {
        Packet packet;
        switch (animation) {
            case ARM_SWING :
                packet = new PacketPlayOutAnimation(getNmsEntity(), 0);
                break;
            case HURT :
                packet = new PacketPlayOutAnimation(getNmsEntity(), 1);
                break;
            case EAT :
                packet = new PacketPlayOutAnimation(getNmsEntity(), 3);
                break;
            case CRITICAL :
                packet = new PacketPlayOutAnimation(getNmsEntity(), 4);
                break;
            case MAGIC_CRITICAL :
                packet = new PacketPlayOutAnimation(getNmsEntity(), 5);
                break;
            default :
                super.animate(animation);
                return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            EntityPlayer handle = NMS.getHandle(player);
            handle.playerConnection.sendPacket(packet);
        }
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
