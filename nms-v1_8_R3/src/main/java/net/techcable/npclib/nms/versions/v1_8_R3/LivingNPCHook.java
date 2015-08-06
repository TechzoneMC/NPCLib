package net.techcable.npclib.nms.versions.v1_8_R3;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_8_R3.Pathfinder;
import net.minecraft.server.v1_8_R3.PathfinderNormal;
import net.minecraft.server.v1_8_R3.World;
import net.techcable.npclib.Animation;
import net.techcable.npclib.LivingNPC;
import net.techcable.npclib.nms.ILivingNPCHook;
import net.techcable.npclib.nms.versions.v1_8_R3.ai.NPCPath;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCBat;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCBlaze;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCCaveSpider;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCChicken;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCCow;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCCreeper;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCEnderDragon;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCEnderman;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCEndermite;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCGhast;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCGiant;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCGuardian;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCHorse;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCIronGolem;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCMagmaCube;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCMushroomCow;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCOcelot;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCPig;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCPigZombie;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCRabbit;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCSheep;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCSilverfish;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCSlime;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCSnowman;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCSpider;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCSquid;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCVillager;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCWitch;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCWither;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCWolf;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.living.EntityNPCZombie;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class LivingNPCHook extends NPCHook implements ILivingNPCHook {

    public LivingNPCHook(LivingNPC npc, Location toSpawn, EntityType type) {
        super(npc);
        nmsEntity = spawn(toSpawn, type);
        getNmsEntity().spawnIn(NMS.getHandle(toSpawn.getWorld()));
        getNmsEntity().setPositionRotation(toSpawn.getX(), toSpawn.getY(), toSpawn.getZ(), toSpawn.getYaw(), toSpawn.getPitch());
        NMS.getHandle(toSpawn.getWorld()).addEntity(getNmsEntity());
        if (nmsEntity instanceof LivingHookable) ((LivingHookable) nmsEntity).setHook(this);
    }

    @Override
    public void look(float pitch, float yaw) {
        yaw = clampYaw(yaw);
        getNmsEntity().yaw = yaw;
        getNmsEntity().pitch = pitch;
        getNmsEntity().aK = yaw; // MCP -- rotationYawHead Srg -- field_70759_as
        if (getNmsEntity() instanceof EntityHuman) getNmsEntity().aI = yaw; // MCP -- renderYawOffset Srg -- field_70761_aq
        getNmsEntity().aL = yaw; // MCP -- prevRotationYawHead Srg -- field_70758_at
    }

    private final ItemStack[] lastEquipment = new ItemStack[5];

    public static float clampYaw(float yaw) {
        while (yaw < -180.0F) {
            yaw += 360.0F;
        }
        while (yaw >= 180.0F) {
            yaw -= 360.0F;
        }
        return yaw;
    }

    public static final double DEFAULT_SPEED = 0.2;
    public static final int DEFAULT_RANGE = 45;

    @Override
    public void navigateTo(Location l) {
        NPCPath path = NPCPath.find(getNpc(), l, DEFAULT_RANGE, DEFAULT_SPEED);
        getNpc().addTask(path);
    }

    @Override
    public void animate(Animation animation) {
        Packet packet;
        switch (animation) {
            case HURT :
                packet = new PacketPlayOutEntityStatus(getNmsEntity(), (byte)2); // 1.8 hurt status is 2
                break;
            case DEAD :
                packet = new PacketPlayOutEntityStatus(getNmsEntity(), (byte)3); // 1.8 dead status is 2
                break;
            default :
                throw new UnsupportedOperationException("Unsupported animation " + animation);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            EntityPlayer handle = NMS.getHandle(player);
            handle.playerConnection.sendPacket(packet);
        }
    }

    @Getter(lazy = true)
    private final Pathfinder pathfinder = new Pathfinder(new PathfinderNormal());

    @Override
    public void onTick() {
        if (MinecraftServer.currentTick % 5 != 0) return; // Every 5 ticks
        List<Integer> toUpdate = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            ItemStack currentEquipment = getNmsEntity().getEquipment(i);
            ItemStack lastEquipment = this.lastEquipment[i];
            if (!equals(currentEquipment, lastEquipment)) {
                Packet packet = new PacketPlayOutEntityEquipment(getNmsEntity().getId(), i, currentEquipment);
                NMS.sendToAll(packet);
            }
            this.lastEquipment[i] = currentEquipment;
        }
        if (toUpdate.isEmpty()) return;
    }

    @Override
    public void setName(String s) {
        if (s == null || s.trim().isEmpty()) {
            getNmsEntity().setCustomName("");
            getNmsEntity().setCustomNameVisible(false);
            return;
        }
        getNmsEntity().setCustomName(s);
        getNmsEntity().setCustomNameVisible(true);
    }

    private static boolean equals(ItemStack first, ItemStack second) {
        if (first == null) return second == null;
        return first.equals(second);
    }

    public EntityLiving getNmsEntity() {
        return (EntityLiving) super.getNmsEntity();
    }

    @Override
    public LivingNPC getNpc() {
        return (LivingNPC) super.getNpc();
    }

    @Override
    public LivingEntity getEntity() {
        return (LivingEntity) getNmsEntity().getBukkitEntity();
    }

    protected EntityLiving spawn(Location toSpawn, EntityType type) { // TODO Update this each version with new entities
        World world = NMS.getHandle(toSpawn.getWorld());
        switch (type) {
            case BAT:
                return new EntityNPCBat(world, getNpc(), this);
            case BLAZE:
                return new EntityNPCBlaze(world, getNpc(), this);
            case CAVE_SPIDER:
                return new EntityNPCCaveSpider(world, getNpc(), this);
            case CHICKEN:
                return new EntityNPCChicken(world, getNpc(), this);
            case COW:
                return new EntityNPCCow(world, getNpc(), this);
            case CREEPER:
                return new EntityNPCCreeper(world, getNpc(), this);
            case ENDER_DRAGON:
                return new EntityNPCEnderDragon(world, getNpc(), this);
            case ENDERMAN:
                return new EntityNPCEnderman(world, getNpc(), this);
            case ENDERMITE:
                return new EntityNPCEndermite(world, getNpc(), this);
            case GHAST:
                return new EntityNPCGhast(world, getNpc(), this);
            case GIANT:
                return new EntityNPCGiant(world, getNpc(), this);
            case GUARDIAN:
                return new EntityNPCGuardian(world, getNpc(), this);
            case HORSE:
                return new EntityNPCHorse(world, getNpc(), this);
            case IRON_GOLEM:
                return new EntityNPCIronGolem(world, getNpc(), this);
            case MAGMA_CUBE:
                return new EntityNPCMagmaCube(world, getNpc(), this);
            case MUSHROOM_COW:
                return new EntityNPCMushroomCow(world, getNpc(), this);
            case OCELOT:
                return new EntityNPCOcelot(world, getNpc(), this);
            case PIG:
                return new EntityNPCPig(world, getNpc(), this);
            case PIG_ZOMBIE:
                return new EntityNPCPigZombie(world, getNpc(), this);
            case RABBIT:
                return new EntityNPCRabbit(world, getNpc(), this);
            case SHEEP:
                return new EntityNPCSheep(world, getNpc(), this);
            case SILVERFISH:
                return new EntityNPCSilverfish(world, getNpc(), this);
            case SLIME:
                return new EntityNPCSlime(world, getNpc(), this);
            case SNOWMAN:
                return new EntityNPCSnowman(world, getNpc(), this);
            case SPIDER:
                return new EntityNPCSpider(world, getNpc(), this);
            case SQUID:
                return new EntityNPCSquid(world, getNpc(), this);
            case VILLAGER:
                return new EntityNPCVillager(world, getNpc(), this);
            case WITCH:
                return new EntityNPCWitch(world, getNpc(), this);
            case WITHER:
                return new EntityNPCWither(world, getNpc(), this);
            case WOLF:
                return new EntityNPCWolf(world, getNpc(), this);
            case ZOMBIE:
                return new EntityNPCZombie(world, getNpc(), this);
            default:
                if (type.isAlive()) throw new UnsupportedOperationException("Unsupported living entity: " + type.getName());
                else throw new IllegalArgumentException("Not a living entity");
        }
    }

    public static interface LivingHookable {

        public LivingNPCHook getHook();

        public void setHook(LivingNPCHook hook);
    }
}
