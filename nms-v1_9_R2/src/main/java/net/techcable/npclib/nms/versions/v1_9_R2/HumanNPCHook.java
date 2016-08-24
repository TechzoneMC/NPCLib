package net.techcable.npclib.nms.versions.v1_9_R2;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.UUID;

import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.Packet;
import net.minecraft.server.v1_9_R2.PacketPlayOutAnimation;
import net.minecraft.server.v1_9_R2.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_9_R2.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.techcable.npclib.Animation;
import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.nms.IHumanNPCHook;
import net.techcable.npclib.nms.versions.v1_9_R2.entity.EntityNPCPlayer;
import net.techcable.npclib.utils.Reflection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;

public class HumanNPCHook extends LivingNPCHook implements IHumanNPCHook {

    public HumanNPCHook(HumanNPC npc, Location toSpawn) {
        super(npc, toSpawn, EntityType.PLAYER);
        getNmsEntity().setHook(this);
    }

    @Override
    public void setSkin(UUID id) {
        boolean wasHidden = shownInTabList;
        NMS.setSkin(getNmsEntity().getProfile(), id);
        refreshPlayerInfo();
    }

    private boolean shownInTabList;

    @Override
    public void showInTablist() {
        if (shownInTabList) return;
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, getNmsEntity());
        NMS.sendToAll(packet);
        shownInTabList = true;
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

    @Override
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

    public void onJoin(Player joined) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, getNmsEntity());
        NMS.getHandle(joined).playerConnection.sendPacket(packet);
        if (!shownInTabList) {
            PacketPlayOutPlayerInfo removePacket = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, getNmsEntity());
            NMS.getHandle(joined).playerConnection.sendPacket(packet);
        }
    }
}
