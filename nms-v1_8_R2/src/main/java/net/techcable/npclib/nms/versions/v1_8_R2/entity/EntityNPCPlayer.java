package net.techcable.npclib.nms.versions.v1_8_R2.entity;

import lombok.*;

import java.util.UUID;

import net.minecraft.server.v1_8_R2.DamageSource;
import net.minecraft.server.v1_8_R2.EntityPlayer;
import net.minecraft.server.v1_8_R2.PlayerInteractManager;
import net.minecraft.server.v1_8_R2.WorldSettings.EnumGamemode;
import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.nms.versions.v1_8_R2.HumanNPCHook;
import net.techcable.npclib.nms.versions.v1_8_R2.NMS;
import net.techcable.npclib.nms.versions.v1_8_R2.network.NPCConnection;

import org.bukkit.Location;

import com.mojang.authlib.GameProfile;

@Getter
public class EntityNPCPlayer extends EntityPlayer {

    private final HumanNPC npc;
    @Setter
    private HumanNPCHook hook;

    public EntityNPCPlayer(HumanNPC npc, Location location) {
        super(NMS.getServer(), NMS.getHandle(location.getWorld()), makeProfile(npc), new PlayerInteractManager(NMS.getHandle(location.getWorld())));
        playerInteractManager.b(EnumGamemode.SURVIVAL); //MCP = initializeGameType ---- SRG=func_73077_b
        this.npc = npc;
        playerConnection = new NPCConnection(this);

        setPosition(location.getX(), location.getY(), location.getZ());
    }

    @Override
    public boolean damageEntity(DamageSource source, float damage) {
        if (npc.isProtected()) {
            return false;
        }
        return super.damageEntity(source, damage);
    }

    private static GameProfile makeProfile(HumanNPC npc) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), npc.getName());
        NMS.setSkin(profile, npc.getSkin());
        return profile;
    }
}
