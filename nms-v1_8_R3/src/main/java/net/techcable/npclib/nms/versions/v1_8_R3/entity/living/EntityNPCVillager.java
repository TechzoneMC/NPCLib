package net.techcable.npclib.nms.versions.v1_8_R3.entity.living;

import lombok.*;

import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EntityAgeable;
import net.minecraft.server.v1_8_R3.EntityVillager;
import net.minecraft.server.v1_8_R3.World;
import net.techcable.npclib.LivingNPC;
import net.techcable.npclib.nms.versions.v1_8_R3.LivingNPCHook;
import net.techcable.npclib.nms.versions.v1_8_R3.LivingNPCHook.LivingHookable;

public class EntityNPCVillager extends EntityVillager implements LivingHookable {

    private final LivingNPC npc;

    @Getter
    @Setter
    private LivingNPCHook hook;

    public EntityNPCVillager(World world, LivingNPC npc, LivingNPCHook hook) {
        super(world);
        this.npc = npc;
        setHook(hook);
    }

    @Override
    public boolean damageEntity(DamageSource source, float damage) {
        if (npc.isProtected()) {
            return false;
        }
        return super.damageEntity(source, damage);
    }

    @Override
    public EntityAgeable createChild(EntityAgeable entityAgeable) {
        return this.b(entityAgeable);
    }
}
