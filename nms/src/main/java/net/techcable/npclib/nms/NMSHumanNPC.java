package net.techcable.npclib.nms;

import java.util.UUID;

import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.utils.uuid.UUIDUtils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class NMSHumanNPC extends NMSLivingNPC<IHumanNPCHook> implements HumanNPC {

    public NMSHumanNPC(NMSRegistry registry, UUID id, String name) {
        super(registry, id, name);
    }

    @Override
    protected IHumanNPCHook doSpawn(Location toSpawn) {
        return NMSRegistry.getNms().spawnHumanNPC(toSpawn, this);
    }

    private UUID skin;

    @Override
    public UUID getSkin() {
        return skin;
    }

    @Override
    public void setSkin(UUID skin) {
        this.skin = skin;
        getHook().setSkin(skin);
    }

    @Override
    public void setSkin(String skin) {
        UUID id = UUIDUtils.getId(skin);
        setSkin(id);
    }

    private boolean showInTabList = true;

    @Override
    public void setShowInTabList(boolean show) {
        boolean wasShownInTabList = showInTabList;
        this.showInTabList = show;
        if (showInTabList != wasShownInTabList) {
            if (showInTabList) {
                getHook().showInTablist();
            } else {
                getHook().hideFromTablist();
            }
        }
    }

    @Override
    public boolean isShownInTabList() {
        return showInTabList;
    }

    @Override
    public Player getEntity() {
        return (Player) getHook().getEntity();
    }
}
