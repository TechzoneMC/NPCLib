package net.techcable.npclib.nms.versions.v1_8_R3.ai;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.ChunkCache;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.PathEntity;
import net.minecraft.server.v1_8_R3.Vec3D;
import net.techcable.npclib.LivingNPC;
import net.techcable.npclib.ai.AIEnvironment;
import net.techcable.npclib.ai.AITask;
import net.techcable.npclib.nms.versions.v1_8_R3.LivingNPCHook;
import net.techcable.npclib.nms.versions.v1_8_R3.NMS;

import org.bukkit.Location;

/**
 * A npc pathing class
 * <p/>
 * Taken from "https://github.com/lenis0012/NPCFactory/blob/master/src/main/java/com/lenis0012/bukkit/npc/NPCPath.java"
 * It is MIT Licensed by lenis0012
 */
public class NPCPath implements AITask {

    private final LivingNPC npc;
    private final PathEntity nmsPath;
    private final double speed;
    private double progress;
    private Vec3D currentPoint;

    protected NPCPath(LivingNPC npc, PathEntity nmsPath, double speed) {
        this.npc = npc;
        this.nmsPath = nmsPath;
        this.speed = speed;
        this.progress = 0.0;
        this.currentPoint = nmsPath.a(getEntity()); // Just base it off signature
    }

    public void tick(AIEnvironment environment) {
        int current = MathHelper.floor(progress);
        double d = progress - current;
        double d1 = 1 - d;
        if (d + speed < 1) {
            double dx = (currentPoint.a - getEntity().locX) * speed; // a is x
            double dz = (currentPoint.c - getEntity().locZ) * speed; // c is z

            getEntity().move(dx, 0, dz);
            if (getEntity() instanceof EntityHuman) ((EntityHuman) getEntity()).checkMovement(dx, 0, dz);
            progress += speed;
        } else {
            //First complete old point.
            double bx = (currentPoint.a - getEntity().locX) * d1;
            double bz = (currentPoint.c - getEntity().locZ) * d1;

            //Check if new point exists
            nmsPath.a(); // Increments the second field (first int field)
            if (!nmsPath.b()) { // Checks if first field is greater than second field
                //Append new movement
                this.currentPoint = nmsPath.a(getEntity()); // Just base it off signature
                double d2 = speed - d1;

                double dx = bx + ((currentPoint.a - getEntity().locX) * d2); // a -> x
                double dy = currentPoint.b - getEntity().locY; //Jump if needed to reach next block. // b -> y
                double dz = bz + ((currentPoint.c - getEntity().locZ) * d2); // c -> z

                getEntity().move(dx, dy, dz);
                if (getEntity() instanceof EntityHuman) ((EntityHuman) getEntity()).checkMovement(dx, dy, dz);
                progress += speed;
            } else {
                //Complete final movement
                getEntity().move(bx, 0, bz);
                if (getEntity() instanceof EntityHuman) ((EntityHuman) getEntity()).checkMovement(bx, 0, bz);
                environment.removeTask(this);
            }
        }
    }

    public static NPCPath find(final LivingNPC npc, Location to, double range, double speed) {
        if (speed > 1) {
            throw new IllegalArgumentException("Speed cannot be higher than 1!");
        }

        final EntityLiving entity = NMS.getHandle(npc.getEntity());
        final LivingNPCHook hook = NMS.getHook(npc);

        try {
            BlockPosition posFrom = new BlockPosition(entity);
            BlockPosition posTo = new BlockPosition(to.getX(), to.getY(), to.getZ());
            int k = (int) (range + 8.0);

            ChunkCache chunkCache = new ChunkCache(entity.world, posFrom.a(-k, -k, -k), posFrom.a(k, k, k), 0);
            PathEntity path = hook.getPathfinder().a(chunkCache, entity, posTo, (float) range);
            if (path != null) {
                return new NPCPath(npc, path, speed);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public EntityLiving getEntity() {
        return NMS.getHandle(npc.getEntity());
    }
}
