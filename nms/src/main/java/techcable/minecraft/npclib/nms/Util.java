package techcable.minecraft.npclib.nms;

import techcable.minecraft.npclib.NPC;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;

import com.google.common.base.Throwables;

public class Util {
    private Util() {}
    
    
    private static NMS nms;
    public static NMS getNMS() {
    	if (nms == null) {
    		try {
        		String version = getVersion();
        		Class<?> rawClass = Class.forName("techcable.minecraft.npclib.nms.versions." + version + ".NMS");
        		Class<? extends NMS> nmsClass = rawClass.asSubclass(NMS.class);
        		Constructor<? extends NMS> constructor = nmsClass.getConstructor();
        		return constructor.newInstance();
        	} catch (ClassNotFoundException ex) {
        		throw new UnsupportedOperationException("Unsupported nms version", ex);
        	} catch (InvocationTargetException ex) {
        		throw Throwables.propagate(ex.getTargetException());
        	} catch (Exception ex) {
        		throw Throwables.propagate(ex);
        	}
    	}
    	return nms;
    }
    
    public static String getVersion() {
    	String packageName = Bukkit.getServer().getClass().getPackage().getName();
    	return packageName.substring(packageName.lastIndexOf(".") + 1);
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
   
    public static Entity spawn(Location location, EntityType type, String name, NPC npc) {
        if (type.equals(EntityType.PLAYER)) {
            return getNMS().spawnPlayer(location, name, npc);
        } else throw new UnsupportedOperationException();
    }
}
