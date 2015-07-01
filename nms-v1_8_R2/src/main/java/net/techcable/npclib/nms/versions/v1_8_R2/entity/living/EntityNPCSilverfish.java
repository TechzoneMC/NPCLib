package net.techcable.npclib.nms.versions.v1_8_R2.entity.living;

import lombok.*;

import net.minecraft.server.v1_8_R2.DamageSource;
import net.minecraft.server.v1_8_R2.EntitySilverfish;
import net.minecraft.server.v1_8_R2.World;
import net.techcable.npclib.LivingNPC;
import net.techcable.npclib.nms.versions.v1_8_R2.LivingNPCHook;
import net.techcable.npclib.nms.versions.v1_8_R2.LivingNPCHook.LivingHookable;

public class EntityNPCSilverfish extends EntitySilverfish implements LivingHookable {
    private final LivingNPC npc;

    @Getter
    @Setter
    private LivingNPCHook hook;

    public EntityNPCSilverfish(World world, LivingNPC npc, LivingNPCHook hook) {
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
