package net.techcable.npclib.nms.versions.v1_7_R4;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.ItemStack;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityStatus;
import net.techcable.npclib.Animation;
import net.techcable.npclib.LivingNPC;
import net.techcable.npclib.PathNotFoundException;
import net.techcable.npclib.nms.ILivingNPCHook;
import net.techcable.npclib.nms.versions.v1_7_R4.ai.NPCPath;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class LivingNPCHook extends NPCHook implements ILivingNPCHook {

    public LivingNPCHook(LivingNPC npc, Location toSpawn, EntityType type) {
        super(npc);
        this.nmsEntity = spawn(toSpawn, type);
        getNmsEntity().spawnIn(NMS.getHandle(toSpawn.getWorld()));
        getNmsEntity().setPositionRotation(toSpawn.getX(), toSpawn.getY(), toSpawn.getZ(), toSpawn.getYaw(), toSpawn.getPitch());
        NMS.getHandle(toSpawn.getWorld()).addEntity(getNmsEntity());
        if (nmsEntity instanceof LivingHookable) ((LivingHookable) nmsEntity).setHook(this);
    }

    @Override
    public void look(float pitch, float yaw) {
        yaw = clampYaw(yaw);
        getNmsEntity().yaw = yaw;
        getNmsEntity().pitch = pitch;
        getNmsEntity().aO = yaw; // MCP -- rotationYawHead Srg -- field_70759_as
        if (getNmsEntity() instanceof EntityHuman)
            getNmsEntity().aM = yaw; // MCP -- renderYawOffset Srg -- field_70761_aq
        getNmsEntity().aP = yaw; // MCP -- prevRotationYawHead Srg -- field_70758_at
    }

    private final ItemStack[] lastEquipment = new ItemStack[5];

    public static float clampYaw(float yaw) {
        while (yaw < -180.0F) {
            yaw += 360.0F;
        }
        while (yaw >= 180.0F) {
            yaw -= 360.0F;
        }
        return yaw;
    }

    @Override
    public void onTick() {
        if (MinecraftServer.currentTick % 5 != 0) return; // Every 5 ticks
        List<Integer> toUpdate = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            ItemStack currentEquipment = getNmsEntity().getEquipment(i);
            ItemStack lastEquipment = this.lastEquipment[i];
            if (!equals(currentEquipment, lastEquipment)) {
                Packet packet = new PacketPlayOutEntityEquipment(getNmsEntity().getId(), i, currentEquipment);
                NMS.sendToAll(packet);
            }
            this.lastEquipment[i] = currentEquipment;
        }
        if (toUpdate.isEmpty()) return;
    }

    @Override
    public void setName(String s) {
        if (s == null || s.trim().isEmpty()) {
            getEntity().setCustomName("");
            getEntity().setCustomNameVisible(false);
            return;
        }
        getEntity().setCustomName(s);
        getEntity().setCustomNameVisible(true);
    }

    private static boolean equals(ItemStack first, ItemStack second) {
        if (first == null) return second == null;
        return first.equals(second);
    }

    public static final double DEFAULT_SPEED = 0.2;
    public static final int DEFAULT_RANGE = 45;

    @Override
    public void navigateTo(Location l) throws PathNotFoundException {
        NPCPath path = NPCPath.find(getNpc(), l, DEFAULT_RANGE, DEFAULT_SPEED);
        if (path == null) throw new PathNotFoundException(l, getEntity().getLocation());
        getNpc().addTask(path);
    }

    @Override
    public void animate(Animation animation) {
        Packet packet;
        switch (animation) {
            case HURT:
                packet = new PacketPlayOutEntityStatus(getNmsEntity(), (byte) 2); // 1.8 and 1.7 hurt status is 2
                break;
            case DEAD:
                packet = new PacketPlayOutEntityStatus(getNmsEntity(), (byte) 3); // 1.8 and 1.7 dead status is 2
                break;
            default:
                throw new UnsupportedOperationException("Unsupported animation " + animation);
        }
        for (EntityPlayer handle : ((List<EntityPlayer>) NMS.getServer().getPlayerList().players)) {
            handle.playerConnection.sendPacket(packet);
        }
    }

    public EntityLiving getNmsEntity() {
        return (EntityLiving) super.getNmsEntity();
    }

    @Override
    public LivingNPC getNpc() {
        return (LivingNPC) super.getNpc();
    }

    @Override
    public LivingEntity getEntity() {
        return getNmsEntity() == null ? null : (LivingEntity) getNmsEntity().getBukkitEntity();
    }

    protected EntityLiving spawn(Location toSpawn, EntityType type) { // TODO Update this each version with new entities
        if (type.isAlive()) throw new UnsupportedOperationException("Unsupported living entity: " + type.getName());
        else throw new IllegalArgumentException("Not a living entity");
    }

    public static interface LivingHookable {

        public LivingNPCHook getHook();

        public void setHook(LivingNPCHook hook);
    }
}
