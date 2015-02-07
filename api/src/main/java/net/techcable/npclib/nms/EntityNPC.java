package net.techcable.npclib.nms;

import net.techcable.npclib.NPC;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public interface EntityNPC {
    public void notifyOfEquipmentChange(Player[] toNotify, int... slot);
	public void spawn(Location toSpawn);
	public void despawn();
	public EntityType getType();
	public NPC getNpc();
	public Entity getEntity();
	public void look(double pitch, double yaw);

	//Event Hooks
	public void onJoin(Player joined);
	public void onDamage(Entity damaged, Entity damager);
	public void onDamage(Entity damaged, Block damager);
	public void onTick();
}
