package net.techcable.npclib.nms.versions.v1_8_R1;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_8_R1.EntityLiving;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.MinecraftServer;
import net.minecraft.server.v1_8_R1.Packet;
import net.minecraft.server.v1_8_R1.WorldServer;
import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.LivingNPC;
import net.techcable.npclib.NPC;
import net.techcable.npclib.nms.IHumanNPCHook;
import net.techcable.npclib.nms.ILivingNPCHook;
import net.techcable.npclib.nms.versions.v1_8_R1.LivingNPCHook.LivingHookable;
import net.techcable.npclib.nms.versions.v1_8_R1.entity.EntityNPCPlayer;
import net.techcable.npclib.utils.NPCLog;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R1.CraftServer;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
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
    public ILivingNPCHook spawnLivingNPC(Location toSpawn, LivingNPC npc, EntityType type) {
        return new LivingNPCHook(npc, toSpawn, type);
    }

    @Override
    public void onJoin(Player joined, Collection<? extends NPC> npcs) {
        for (NPC npc : npcs) {
            if (!(npc instanceof HumanNPC)) continue;
            HumanNPCHook hook = getHook((HumanNPC) npc);
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

    public static HumanNPCHook getHook(HumanNPC npc) {
        EntityPlayer player = getHandle(npc.getEntity());
        if (player instanceof EntityNPCPlayer) return null;
        return ((EntityNPCPlayer) player).getHook();
    }

    public static LivingNPCHook getHook(LivingNPC npc) {
        if (npc instanceof HumanNPC) return getHook((HumanNPC) npc);
        EntityLiving entity = getHandle(npc.getEntity());
        if (entity instanceof LivingHookable) return ((LivingHookable) entity).getHook();
        return null;
    }

    public static void sendToAll(Packet packet) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            getHandle(p).playerConnection.sendPacket(packet);
        }
    }
    private static final LoadingCache<UUID, GameProfile> properties = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(new CacheLoader<UUID, GameProfile>() {

                @Override
                public GameProfile load(UUID uuid) throws Exception {
                    return MinecraftServer.getServer().aB().fillProfileProperties(new GameProfile(uuid, null), true);
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