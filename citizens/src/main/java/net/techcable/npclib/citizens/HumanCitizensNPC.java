package net.techcable.npclib.citizens;

import java.util.UUID;

import net.citizensnpcs.api.npc.NPC;
import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.utils.uuid.UUIDUtils;

import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

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
        if (skin == null) return;
        UUID id = UUIDUtils.getId(skin);
        if (id == null) return;
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
}
