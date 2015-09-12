package net.techcable.npclib.citizens;

import net.citizensnpcs.api.npc.NPC;
import net.techcable.npclib.Animation;
import net.techcable.npclib.LivingNPC;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.google.common.base.Preconditions;

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
        return isSpawned();
    }

    @Override
    public void walkTo(Location l) {
        Preconditions.checkNotNull(l, "Null destination");
        Preconditions.checkState(isAbleToWalk(), "Unable to walk");
        getHandle().getNavigator().setTarget(l);
    }

    /**
     * {@inhertDoc}
     * <p>
     * Doesn't handle {@link net.techcable.npclib.Animation#HURT} and {@link net.techcable.npclib.Animation#DEAD}
     * </p>
     *
     */
    @Override
    public void animate(Animation animation) {
        Preconditions.checkArgument(animation.getAppliesTo().isInstance(this), "%s can't be applied to living npcs", animation.toString());
        Preconditions.checkState(isSpawned(), "NPC isn't spawned");
        throw new UnsupportedOperationException(animation.toString() + " is unimplemented for living npcs");
    }

    @Override
    public LivingEntity getEntity() {
        return (LivingEntity) super.getEntity();
    }
}
