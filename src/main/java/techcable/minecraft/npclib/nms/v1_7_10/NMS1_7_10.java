package techcable.minecraft.npclib.nms.v1_7_10;

import java.util.UUID;

import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.World;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.World;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.entity.HumanEntity;

public class NMS1_7_10 implements NMS {
    public void look(org.bukkit.entity.Entity entity, float pitch, float yaw) {
        Entity handle = getHandle(entity);
        if (handle == null) return;
        handle.yaw = yaw;
        setHeadYaw(yaw);
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
        EntityHuman human = new EntityHuman(toSpawn, name);
        World world = getHandle(toSpawn.getWorld());
        human.setLocation(toSpawn.getX(), toSpawn.getY(), toSpawn.getZ(), toSpawn.getYaw(), toSpawn.getPitch());
        world.addEntity(human);
        human.setLocation(toSpawn.getX(), toSpawn.getY(), toSpawn.getZ(), toSpawn.getYaw(), toSpawn.getPitch());
        return (HumanEntity) entity.getBukkitEntity();
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
    
    @Override
    public boolean isCompatable(Version version) {
        if (version.getId() == "1.7.10-R0.1-SNAPSHOT") return true;
        return false;
    }
}