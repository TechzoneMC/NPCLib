package net.techcable.npclib.nms.versions.v1_8_R2;

import java.util.Collection;
import java.util.UUID;

import net.minecraft.server.v1_8_R2.EntityLiving;
import net.minecraft.server.v1_8_R2.EntityPlayer;
import net.minecraft.server.v1_8_R2.MinecraftServer;
import net.minecraft.server.v1_8_R2.Packet;
import net.minecraft.server.v1_8_R2.WorldServer;
import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.LivingNPC;
import net.techcable.npclib.NPC;
import net.techcable.npclib.nms.IHumanNPCHook;
import net.techcable.npclib.nms.ILivingNPCHook;
import net.techcable.npclib.nms.versions.v1_8_R2.LivingNPCHook.LivingHookable;
import net.techcable.npclib.nms.versions.v1_8_R2.entity.EntityNPCPlayer;
import net.techcable.npclib.utils.NPCLog;
import net.techcable.npclib.utils.uuid.PlayerProfile;
import net.techcable.npclib.utils.uuid.UUIDUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R2.CraftServer;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

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

    public static EntityLiving getHandle(LivingEntity entity) {
        if (!(entity instanceof CraftLivingEntity)) throw new UnsupportedOperationException(NO_CRAFTBUKKIT_MSG);
        return ((CraftLivingEntity) entity).getHandle();
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

    public static void setSkin(GameProfile profile, UUID skinId) {
        profile.getProperties().get("textures").clear();
        if (skinId == null) return;
        if (Bukkit.getPlayer(skinId) != null) { // Avoid a lookup if the player is online :)
            EntityPlayer playerWithSkin = getHandle(Bukkit.getPlayer(skinId));
            Collection<Property> textureProperties = playerWithSkin.getProfile().getProperties().get("textures");
            profile.getProperties().get("textures").addAll(textureProperties);
            return;
        }
        PlayerProfile skinProfile = UUIDUtils.getLookup().lookup(skinId);
        if (skinProfile == null) {
            NPCLog.warn("Unable to get skin for uuid %s", skinId);
            return;
        }
        if (skinProfile.getProperties() == null || skinProfile.getProperties().isEmpty()) return;
        for (Object element : skinProfile.getProperties()) {
            if (!(element instanceof JSONObject)) continue;
            JSONObject object = (JSONObject) element;
            if (!(object.get("name") instanceof String)) continue;
            String name = (String) object.get("name");
            if (!"textures".equals(name)) continue;
            if (!(object.get("value") instanceof String)) continue;
            String signature = object.get("signature") instanceof String ? (String) object.get("signature") : null;
            String value = (String) object.get("value");
            Property property = new Property(name, value, signature);
            profile.getProperties().put(name, property);
        }
    }
}