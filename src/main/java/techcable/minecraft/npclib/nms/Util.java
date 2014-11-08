package techcable.minecraft.npclib.nms;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;

public class Util {
    private Util() {}
    
    public static NMS getNMS() {
        throw new UnsupportedOperationException();
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
    
    public static void setName(Entity entity, String name) {
        if (entity instanceof LivingEntity) {
            ((LivingEntity)entity).setCustomName(name);
            if (entity instanceof HumanEntity) {
                getNMS().setHumanName((HumanEntity) entity, name);
            }
        }
        
    }
    
    public static Entity spawn(Location location, EntityType type, String name, UUID uuid) {
        if (type.equals(EntityType.PLAYER)) {
            return getNMS().spawnHuman(location, uuid, name);
        }
        if (!type.isSpawnable()) throw new UnsupportedOperationException();
        Entity entity = location.getWorld().spawnEntity(location, type);
	    setName(entity, name);
	    return entity;
    }
}
