package net.techcable.npclib.nms.versions.v1_8_R1.entity;

import lombok.*;

import java.util.UUID;

import net.minecraft.server.v1_8_R1.DamageSource;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.EnumGamemode;
import net.minecraft.server.v1_8_R1.PlayerInteractManager;
import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.nms.versions.v1_8_R1.HumanNPCHook;
import net.techcable.npclib.nms.versions.v1_8_R1.NMS;
import net.techcable.npclib.nms.versions.v1_8_R1.network.NPCConnection;

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
