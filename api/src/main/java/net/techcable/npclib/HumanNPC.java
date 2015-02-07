package net.techcable.npclib;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface HumanNPC extends NPC {
	
	@Override
	public Player getEntity();
	
	/**
	 * Return this npc's skin
	 * 
	 * A value of null represents a steve skin
	 * 
	 * @return this npc's skin
	 */
	public UUID getSkin();
	
	/**
	 * Set the npc's skin
	 * 
	 * A value of null represents a steve skin
	 * 
	 * @param skin the player id with the skin you want
	 * 
	 * @throws UnsupportedOperationException if skins aren't supported
	 */
	public void setSkin(UUID skin);
	
	/**
	 * Set the npc's skin
	 * 
	 * A value of null represents a steve skin
	 * 
	 * @param skin the player name with the skin you want
	 * 
	 * @throws UnsupportedOperationException if skins aren't supported
	 */
	public void setSkin(String skin);
	
	/**
	 * Have the npc lie down at it's current location
	 * 
	 */
	public void sleep();
	
	/**
	 * Have the npc lie down at the specified location
	 * 
	 * The location may or may not be a bed
	 * 
	 * @param l the location to lie down at
	 */
	public void sleep(Location l);
	
	/**
	 * Stop the npc from lying down
	 * 
	 */
	public void wake();
	
	/**
	 * Play the eat animation
	 * 
	 */
	public void eat();

	/**
	 * Play the arm swing animation
	 * 
	 */
	public void swing();
}
