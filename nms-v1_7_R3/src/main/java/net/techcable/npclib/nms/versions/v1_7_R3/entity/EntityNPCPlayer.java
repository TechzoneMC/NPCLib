package net.techcable.npclib.nms.versions.v1_7_R3.entity;

import lombok.*;

import java.util.UUID;

import net.minecraft.server.v1_7_R3.DamageSource;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.EnumGamemode;
import net.minecraft.server.v1_7_R3.PlayerInteractManager;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.nms.versions.v1_7_R3.HumanNPCHook;
import net.techcable.npclib.nms.versions.v1_7_R3.NMS;
import net.techcable.npclib.nms.versions.v1_7_R3.network.NPCConnection;

import org.bukkit.Location;

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
