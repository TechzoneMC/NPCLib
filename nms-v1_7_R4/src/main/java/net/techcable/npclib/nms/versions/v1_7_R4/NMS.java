package net.techcable.npclib.nms.versions.v1_7_R4;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.WorldServer;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.LivingNPC;
import net.techcable.npclib.NPC;
import net.techcable.npclib.nms.IHumanNPCHook;
import net.techcable.npclib.nms.ILivingNPCHook;
import net.techcable.npclib.nms.versions.v1_7_R4.entity.EntityNPCPlayer;
import net.techcable.npclib.utils.NPCLog;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class NMS implements net.techcable.npclib.nms.NMS {

    private static NMS instance;

    public NMS() {
        if (instance == null) instance = this;
    }

    public static NMS getInstance() {
        return instance;
    }

    @Override
    public IHumanNPCHook spawnHumanNPC(Location toSpawn, HumanNPC npc) {
        return new HumanNPCHook(npc, toSpawn);
    }

    @Override
    public void onJoin(Player joined, Collection<? extends NPC> npcs) {
        for (NPC npc : npcs) {
            if (!(npc instanceof HumanNPC)) continue;
            HumanNPCHook hook = getHandle((HumanNPC) npc);
            if (hook == null) continue;
            hook.onJoin(joined);
        }
    }

    // UTILS
    public static final String NO_CRAFTBUKKIT_MSG = "Non-CraftBukkit implementations are unsupported";

    public static EntityPlayer getHandle(Player player) {
        if (!(player instanceof CraftPlayer)) throw new UnsupportedOperationException(NO_CRAFTBUKKIT_MSG);
        return ((CraftPlayer) player).getHandle();
    }

    public static EntityLiving getHandle(LivingEntity player) {
        if (!(player instanceof CraftLivingEntity)) throw new UnsupportedOperationException(NO_CRAFTBUKKIT_MSG);
        return ((CraftLivingEntity) player).getHandle();
    }

    public static MinecraftServer getServer() {
        Server server = Bukkit.getServer();
        if (!(server instanceof CraftServer)) throw new UnsupportedOperationException(NO_CRAFTBUKKIT_MSG);
        return ((CraftServer) server).getServer();
    }

    public static WorldServer getHandle(World world) {
        if (!(world instanceof CraftWorld)) throw new UnsupportedOperationException(NO_CRAFTBUKKIT_MSG);
        return ((CraftWorld) world).getHandle();
    }

    public static HumanNPCHook getHandle(HumanNPC npc) {
        EntityPlayer player = getHandle(npc.getEntity());
        if (player instanceof EntityNPCPlayer) return null;
        return ((EntityNPCPlayer) player).getHook();
    }

    public static void sendToAll(Packet packet) {
        for (EntityPlayer p : (List<EntityPlayer>) MinecraftServer.getServer().getPlayerList().players) {
            p.playerConnection.sendPacket(packet);
        }
    }
    private static final Cache<UUID, GameProfile> properties = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(new CacheLoader<UUID, GameProfile>() {

                @Override
                public GameProfile load(UUID uuid) throws Exception {
                    return MinecraftServer.getServer().av().fillProfileProperties(new GameProfile(uuid, null), true);
                }
            });

    public static void setSkin(GameProfile profile, UUID skinId) {
        GameProfile skinProfile;
        if (Bukkit.getPlayer(skinId) != null) {
            skinProfile = getHandle(Bukkit.getPlayer(skinId)).getProfile();
        } else {
            skinProfile = properties.getUnchecked(skinId);
        }
        if (skinProfile.getProperties().containsKey("textures")) {
            profile.getProperties().removeAll("textures");
            profile.getProperties().putAll("textures", skinProfile.getProperties().get("textures"));
        } else {
            NPCLog.debug("Skin with uuid not found: " + skinId);
        }
    }
}