package net.techcable.npclib.nms.versions.v1_8_R3;

import lombok.*;

import net.minecraft.server.v1_8_R3.Entity;
import net.techcable.npclib.NPC;
import net.techcable.npclib.nms.INPCHook;

@Getter
@RequiredArgsConstructor
public class NPCHook implements INPCHook {

    private final NPC npc;
    protected Entity nmsEntity;

    @Override
    public void onDespawn() {
        nmsEntity = null;
    }

    @Override
    public org.bukkit.entity.Entity getEntity() {
        return nmsEntity == null ? null : nmsEntity.getBukkitEntity();
    }
}