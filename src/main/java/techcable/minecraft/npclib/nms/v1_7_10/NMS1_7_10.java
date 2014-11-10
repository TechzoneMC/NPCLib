package techcable.minecraft.npclib.nms.v1_7_10;

import java.lang.reflect.Field;
import java.util.UUID;

import net.minecraft.server.v1_7_R4.ChunkCoordinates;
import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.IChatBaseComponent;
import net.minecraft.server.v1_7_R4.World;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

import techcable.minecraft.npclib.nms.NMS;
import techcable.minecraft.npclib.util.versioning.Version;

public class NMS1_7_10 implements NMS {
    public void look(org.bukkit.entity.Entity entity, float pitch, float yaw) {
        Entity handle = getHandle(entity);
        if (handle == null) return;
        handle.yaw = yaw;
        setHeadYaw(getHandle(entity), yaw);
        handle.pitch = pitch;
    }
    
    public void setHeadYaw(Entity entity, float yaw) {
        if (!(entity instanceof EntityLiving))
            return;
        EntityLiving handle = (EntityLiving) entity;
        while (yaw < -180.0F) {
            yaw += 360.0F;
        }

        while (yaw >= 180.0F) {
            yaw -= 360.0F;
        }
        handle.aO = yaw;
        if (!(handle instanceof EntityHuman))
            handle.aM = yaw;
        handle.aP = yaw;
    }
    
    public HumanEntity spawnHuman(Location toSpawn, UUID uuid, String name) {
    	//TODO spawn stuff
        EntityHuman human = null;
        World world = getHandle(toSpawn.getWorld());
        human.setLocation(toSpawn.getX(), toSpawn.getY(), toSpawn.getZ(), toSpawn.getYaw(), toSpawn.getPitch());
        world.addEntity(human);
        human.setLocation(toSpawn.getX(), toSpawn.getY(), toSpawn.getZ(), toSpawn.getYaw(), toSpawn.getPitch());
        return (HumanEntity) human.getBukkitEntity();
    }
    
    private Entity getHandle(org.bukkit.entity.Entity bukkitEntity) {
        if (!(bukkitEntity instanceof CraftEntity))
            return null;
        return ((CraftEntity)bukkitEntity).getHandle();
    }
    
    private World getHandle(org.bukkit.World bukkitWorld) {
        if (!(bukkitWorld instanceof CraftWorld)) return null;
        return ((CraftWorld)bukkitWorld).getHandle();
    }

    private EntityHuman getHandle(HumanEntity bukkitHuman) {
    	if (!(bukkitHuman instanceof CraftHumanEntity)) return null;
    	return ((CraftHumanEntity)bukkitHuman).getHandle();
    }
	@Override
	public boolean isCompatable(Version version) {
		if (version.getId() == "1.7.10-R0.1-SNAPSHOT") return true;
        return false;
	}
	
	private Field nameField = makeField(EntityHuman.class, "name");
	
	@Override
	public void setName(HumanEntity human, String name) {
		setField(nameField, getHandle(human), name);
	}
	
	private static Field makeField(Class<?> clazz, String fieldName) {
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException ex) {
			throw new RuntimeException(ex);
		}	
	}
	
	private static void setField(Field field, Object objToSet, Object value) {
		field.setAccessible(true);
		try {
			field.set(objToSet, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}