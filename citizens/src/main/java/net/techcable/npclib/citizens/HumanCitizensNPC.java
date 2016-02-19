package net.techcable.npclib.citizens;

import java.util.UUID;

import com.google.common.base.Preconditions;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.util.PlayerAnimation;
import net.techcable.npclib.Animation;
import net.techcable.npclib.HumanNPC;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HumanCitizensNPC extends LivingCitizensNPC implements HumanNPC {

    public HumanCitizensNPC(NPC handle) {
        super(handle);
    }

    @Override
    public UUID getSkin() {
        if (getHandle() == null) return null; //I'm too nice
        if (!getHandle().data().has(NPC.PLAYER_SKIN_UUID_METADATA)) return null;
        return UUID.fromString((String) getHandle().data().get(NPC.PLAYER_SKIN_UUID_METADATA));
    }

    @Override
    public void setSkin(UUID skin) {
        Preconditions.checkState(getHandle() != null, "NPC has been destroyed");
        getHandle().data().set(NPC.PLAYER_SKIN_UUID_METADATA, skin.toString());
        if (isSpawned()) {
            despawn();
            spawn(getHandle().getStoredLocation());
        }
    }

    @Override
    public void setSkin(String skin) {
        Preconditions.checkNotNull(skin, "Skin is null");
        // Hacky uuid load
        UUID id = Bukkit.getOfflinePlayer(skin).getUniqueId();
        // If the uuid's variant is '3' than it must be an offline uuid
        Preconditions.checkArgument(id.variant() != 3, "Invalid player name %s", skin);
        setSkin(id);
    }

    @Override
    public Player getEntity() {
        if (super.getEntity() == null) return null;
        return (Player) super.getEntity();

    }

    public static final String REMOVE_PLAYER_LIST_META = "removefromplayerlist";

    @Override
    public void setShowInTabList(boolean show) {
        Preconditions.checkState(isSpawned(), "Can not set shown in tablist if not spawned");
        getHandle().data().set(REMOVE_PLAYER_LIST_META, !show);
    }

    @Override
    public boolean isShownInTabList() {
        return !((Boolean) getHandle().data().get(REMOVE_PLAYER_LIST_META));
    }

    @Override
    public void animate(Animation animation) {
        Preconditions.checkArgument(animation.getAppliesTo().isInstance(this), "%s can't be applied to human npcs", animation.toString());
        Preconditions.checkState(isSpawned(), "NPC isn't spawned");
        PlayerAnimation citizensAnimation;
        switch (animation) {
            case HURT :
                citizensAnimation = PlayerAnimation.HURT;
                break;
            // case DEAD - Unsupported
            case ARM_SWING :
                citizensAnimation = PlayerAnimation.ARM_SWING;
                break;
            case EAT :
                citizensAnimation = PlayerAnimation.EAT_FOOD;
                break;
            case CRITICAL :
                citizensAnimation = PlayerAnimation.CRIT;
                break;
            case MAGIC_CRITICAL :
                citizensAnimation = PlayerAnimation.MAGIC_CRIT;
                break;
            default :
                super.animate(animation);
                return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            citizensAnimation.play(player);
        }
    }
}
