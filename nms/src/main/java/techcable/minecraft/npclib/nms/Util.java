package techcable.minecraft.npclib.nms;

import techcable.minecraft.npclib.NPC;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;

public class Util {
    private Util() {}
    
    public static NMS getNMS() {
    	try {
    		return NMSVersion.getVersion(NMSVersion.determineCurrentVersion()).getNMS();
    	} catch (UnknownNMSVersionException ex) {
    		throw new UnsupportedOperationException("This version of minecraft isn't supported by NPCLib nms implementation. Please use citizens");
    	}
    }
    
    public static void look(Entity entity, Location toLook) {
        if (!entity.getWorld().equals(toLook.getWorld()))
            return;
        Location fromLocation = entity.getLocation();
        double xDiff, yDiff, zDiff;
        xDiff = toLook.getX() - fromLocation.getX();
        yDiff = toLook.getY() - fromLocation.getY();
        zDiff = toLook.getZ() - fromLocation.getZ();

        double distanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        double distanceY = Math.sqrt(distanceXZ * distanceXZ + yDiff * yDiff);

        double yaw = Math.toDegrees(Math.acos(xDiff / distanceXZ));
        double pitch = Math.toDegrees(Math.acos(yDiff / distanceY)) - 90;
        if (zDiff < 0.0)
            yaw += Math.abs(180 - yaw) * 2;

        getNMS().look(entity, (float) yaw - 90, (float) pitch);
    }
    
    public static void setName(NPC npc, String name) {
        Entity entity = npc.getEntity();
        if (!setName(entity, name) && entity instanceof HumanEntity) {
        	try {
        		getNMS().setName((HumanEntity)entity, name);
        	} catch (Exception ex) {
        		//Squishity squash
        	}
        }
        
    }
    
    /**
     * Set the name of a living entity
     * @param entity the entity that might be living
     * @param name the new name of the entity
     * @return true if successful
     */
    private static boolean setName(Entity entity, String name) {
    	if (entity instanceof LivingEntity) {
    		try {
    			((LivingEntity)entity).setCustomName(name);
    			return true;
    		} catch (Exception ex) {
    			return false;
    		}
    	} else return false;
    }
    
    public static Entity spawn(Location location, EntityType type, String name, UUID uuid) {
        if (type.equals(EntityType.PLAYER)) {
            return getNMS().spawnHuman(location, uuid, name);
        }
        if (!type.isSpawnable()) throw new UnsupportedOperationException();
        Entity entity = location.getWorld().spawnEntity(location, type);
	    
	    return entity;
    }
}
