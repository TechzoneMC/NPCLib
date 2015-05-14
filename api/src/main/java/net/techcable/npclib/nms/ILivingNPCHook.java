package net.techcable.npclib.nms;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface ILivingNPCHook extends INPCHook{
    @Override
    public LivingEntity getEntity();
    public void look(float pitch, float yaw);
    public void onTick();
}
