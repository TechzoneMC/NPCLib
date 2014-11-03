package techcable.minecraft.npclib;

public interface NPC {
	/**
	 * Despawn this npc
	 * 
	 * Once despawned it can't be respawned
	 * 
	 * @return true if was able to despawn
	 */
	public boolean despawn();
	/**
	 * The npc's head will look in this direction
	 * @param toFace the direction to look
	 */
	public void faceLocation(Location toFace);
	
}
