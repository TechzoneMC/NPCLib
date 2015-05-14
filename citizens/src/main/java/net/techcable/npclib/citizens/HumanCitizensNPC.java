package net.techcable.npclib.citizens;

import com.google.common.base.Preconditions;
import net.citizensnpcs.api.npc.NPC;
import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.LivingNPC;
import net.techcable.npclib.util.ProfileUtils;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HumanCitizensNPC extends LivingCitizensNPC implements HumanNPC {
    public HumanCitizensNPC(NPC handle) {
        super(handle);
    }

    @Override
    public UUID getSkin() {
        if (getHandle() == null) return null; //I'm too nice
        if (!getHandle().data().has(NPC.PLAYER_SKIN_UUID_METADATA)) return null;
        return UUID.fromString((String)getHandle().data().get(NPC.PLAYER_SKIN_UUID_METADATA));
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
        ProfileUtils.PlayerProfile profile = ProfileUtils.lookup(skin);
        if (profile == null) return;
        setSkin(profile.getId());
    }

    @Override
    public Player getEntity() {
        if (super.getEntity() == null) return null;
        return (Player) super.getEntity();

    }

    @Override
    public void setShowInTabList(boolean show) {

    }

    @Override
    public boolean isShownInTabList() {
        return false;
    }
}
