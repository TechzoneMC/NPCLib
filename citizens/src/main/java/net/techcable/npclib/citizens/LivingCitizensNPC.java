package net.techcable.npclib.citizens;

import com.google.common.base.Preconditions;
import net.citizensnpcs.api.npc.NPC;
import net.techcable.npclib.LivingNPC;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class LivingCitizensNPC extends CitizensNPC implements LivingNPC {
    public LivingCitizensNPC(NPC handle) {
        super(handle);
    }

    @Override
    public void setName(String name) {
        Preconditions.checkState(getHandle() != null, "NPC has been destroyed");
        getHandle().setName(name);
    }

    @Override
    public String getName() {
        if (getHandle() == null) return ""; //I'm a nice guy
        return getHandle().getName();
    }

    @Override
    public void faceLocation(Location toFace) {
        Preconditions.checkState(getHandle() != null, "NPC has been destroyed");
        Preconditions.checkState(isSpawned(), "NPC has been despawned");
        getHandle().faceLocation(toFace);
    }

    @Override
    public boolean isAbleToWalk() {
        Preconditions.checkState(getHandle() != null, "NPC has been destroyed");
        return isSpawned();
    }

    @Override
    public void walkTo(Location l) {
        Preconditions.checkState(isAbleToWalk(), "Unable to walk");
        getHandle().getNavigator().setTarget(l);
    }

    @Override
    public LivingEntity getEntity() {
        if (super.getEntity() == null) return null;
        return (LivingEntity) super.getEntity();
    }
}
