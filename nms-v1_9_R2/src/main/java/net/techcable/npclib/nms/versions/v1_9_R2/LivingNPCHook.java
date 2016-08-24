package net.techcable.npclib.nms.versions.v1_9_R2;

import java.util.Objects;

import net.minecraft.server.v1_9_R2.EntityHuman;
import net.minecraft.server.v1_9_R2.EntityLiving;
import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.EnumItemSlot;
import net.minecraft.server.v1_9_R2.ItemStack;
import net.minecraft.server.v1_9_R2.MinecraftServer;
import net.minecraft.server.v1_9_R2.Packet;
import net.minecraft.server.v1_9_R2.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_9_R2.PacketPlayOutEntityStatus;
import net.techcable.npclib.Animation;
import net.techcable.npclib.LivingNPC;
import net.techcable.npclib.PathNotFoundException;
import net.techcable.npclib.nms.ILivingNPCHook;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class LivingNPCHook extends NPCHook implements ILivingNPCHook {

    public LivingNPCHook(LivingNPC npc, Location toSpawn, EntityType type) {
        super(npc);
        nmsEntity = spawn(toSpawn, type);
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
        getNmsEntity().aL = yaw; // MCP -- rotationYawHead Srg -- field_70759_as
        if (getNmsEntity() instanceof EntityHuman)
            getNmsEntity().aJ = yaw; // MCP -- renderYawOffset Srg -- field_70761_aq
        getNmsEntity().aM = yaw; // MCP -- prevRotationYawHead Srg -- field_70758_at
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
    public void navigateTo(Location l) throws PathNotFoundException {
        throw new UnsupportedOperationException("Pathfining is currently unsupported in 1.9");
    }

    @Override
    public void animate(Animation animation) {
        Packet packet;
        switch (animation) {
            case HURT:
                packet = new PacketPlayOutEntityStatus(getNmsEntity(), (byte) 2); // 1.8 hurt status is 2
                break;
            case DEAD:
                packet = new PacketPlayOutEntityStatus(getNmsEntity(), (byte) 3); // 1.8 dead status is 2
                break;
            default:
                throw new UnsupportedOperationException("Unsupported animation " + animation);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            EntityPlayer handle = NMS.getHandle(player);
            handle.playerConnection.sendPacket(packet);
        }
    }

    @Override
    public void onTick() {
        if (MinecraftServer.currentTick % 5 != 0) return; // Every 5 ticks
        for (EnumItemSlot slot : EnumItemSlot.values()) {
            ItemStack currentEquipment = getNmsEntity().getEquipment(slot);
            ItemStack lastEquipment = this.lastEquipment[slot.ordinal()];
            if (!Objects.equals(currentEquipment, lastEquipment)) {
                Packet packet = new PacketPlayOutEntityEquipment(getNmsEntity().getId(), slot, currentEquipment);
                NMS.sendToAll(packet);
            }
            this.lastEquipment[slot.ordinal()] = currentEquipment;
        }
    }

    @Override
    public void setName(String s) {
        if (s == null || s.trim().isEmpty()) {
            getNmsEntity().setCustomName("");
            getNmsEntity().setCustomNameVisible(false);
            return;
        }
        getNmsEntity().setCustomName(s);
        getNmsEntity().setCustomNameVisible(true);
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
        return (LivingEntity) getNmsEntity().getBukkitEntity();
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
