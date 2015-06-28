package net.techcable.npclib.nms.versions.v1_8_R2;

import lombok.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.UUID;

import net.minecraft.server.v1_8_R2.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R2.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.nms.IHumanNPCHook;
import net.techcable.npclib.nms.versions.v1_8_R2.entity.EntityNPCPlayer;
import net.techcable.npclib.utils.Reflection;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

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
        refreshPlayerInfo();
    }

    private boolean shownInTabList;
    private final Object packetLock = new Object();

    @Synchronized("packetLock")
    @Override
    public void showInTablist() {
        if (shownInTabList) return;
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, getNmsEntity());
        NMS.sendToAll(packet);
        shownInTabList = true;
    }

    @Override
    @Synchronized("packetLock")
    public void hideFromTablist() {
        if (!shownInTabList) return;
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, getNmsEntity());
        NMS.sendToAll(packet);
        shownInTabList = false;
    }

    public void refreshPlayerInfo() {
        boolean wasShownInTabList = shownInTabList;
        hideFromTablist();
        showInTablist();
        if (!wasShownInTabList) hideFromTablist();
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
        if (s.length() > 16) s = s.substring(0, 16);
        GameProfile profile = getNmsEntity().getProfile();
        // Pro reflection hax
        nameField.setAccessible(true); // Allow access to private
        int modifiers = nameField.getModifiers();
        modifiers = modifiers & ~Modifier.FINAL;
        modifiersField.setAccessible(true);
        Reflection.setField(modifiersField, nameField, modifiers); // Make Field.class think it isn't final
        Reflection.setField(nameField, profile, s);
        refreshPlayerInfo();
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
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, getNmsEntity());
        NMS.getHandle(joined).playerConnection.sendPacket(packet);
        if (!shownInTabList) {
            PacketPlayOutPlayerInfo removePacket = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, getNmsEntity());
            NMS.getHandle(joined).playerConnection.sendPacket(packet);
        }
    }
}
