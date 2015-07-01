package net.techcable.npclib.nms.versions.v1_7_R4.entity.living;

import lombok.*;

import net.minecraft.server.v1_7_R4.DamageSource;
import net.minecraft.server.v1_7_R4.EntityPigZombie;
import net.minecraft.server.v1_7_R4.World;
import net.techcable.npclib.LivingNPC;
import net.techcable.npclib.nms.versions.v1_7_R4.LivingNPCHook;
import net.techcable.npclib.nms.versions.v1_7_R4.LivingNPCHook.LivingHookable;

public class EntityNPCPigZombie extends EntityPigZombie implements LivingHookable {
    private final LivingNPC npc;

    @Getter
    @Setter
    private LivingNPCHook hook;

    public EntityNPCPigZombie(World world, LivingNPC npc, LivingNPCHook hook) {
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

}
