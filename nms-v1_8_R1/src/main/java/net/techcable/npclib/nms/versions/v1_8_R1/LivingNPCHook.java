package net.techcable.npclib.nms.versions.v1_8_R1;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_8_R1.EntityHuman;
import net.minecraft.server.v1_8_R1.EntityLiving;
import net.minecraft.server.v1_8_R1.ItemStack;
import net.minecraft.server.v1_8_R1.MinecraftServer;
import net.minecraft.server.v1_8_R1.Packet;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityEquipment;
import net.techcable.npclib.LivingNPC;
import net.techcable.npclib.nms.ILivingNPCHook;

import org.bukkit.entity.LivingEntity;

public class LivingNPCHook extends NPCHook implements ILivingNPCHook {

    public LivingNPCHook(LivingNPC npc) {
        super(npc);
    }

    @Override
    public void look(float pitch, float yaw) {
        yaw = clampYaw(yaw);
        getNmsEntity().yaw = yaw;
        getNmsEntity().pitch = pitch;
        getNmsEntity().aI = yaw; // MCP -- rotationYawHead Srg -- field_70759_as
        if (getNmsEntity() instanceof EntityHuman) getNmsEntity().aG = yaw; // MCP -- renderYawOffset Srg -- field_70761_aq
        getNmsEntity().aJ = yaw; // MCP -- prevRotationYawHead Srg -- field_70758_at
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
            getNmsEntity().setCustomName("");
            getNmsEntity().setCustomNameVisible(false);
            return;
        }
        getNmsEntity().setCustomName(s);
        getNmsEntity().setCustomNameVisible(true);
    }

    private static boolean equals(ItemStack first, ItemStack second) {
        if (first == null) return second == null;
        return first.equals(second);
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
        return (LivingEntity) super.getNmsEntity();
    }
}
