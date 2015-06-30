package net.techcable.npclib.nms;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public interface ILivingNPCHook extends INPCHook {

    public LivingEntity getEntity();

    public void look(float pitch, float yaw);

    public void onTick();

    public void setName(String s);

    public void navigateTo(Location l);

}
