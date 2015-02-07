package net.techcable.npclib.nms;

import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.NPC;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface EntityHumanNPC extends EntityNPC {
    @Override
    public HumanNPC getNpc();
    @Override
    public Player getEntity();
    
    public void lie(Location toLie);
	public void wake();
	public void eat();
	public void swing();
}
