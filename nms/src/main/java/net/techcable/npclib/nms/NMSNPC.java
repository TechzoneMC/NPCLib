package net.techcable.npclib.nms;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import net.techcable.npclib.NPC;
import net.techcable.npclib.util.ProfileUtils;
import net.techcable.npclib.util.ProfileUtils.PlayerProfile;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import lombok.*;

@Getter
@Setter
public class NMSNPC extends BukkitRunnable implements NPC, Listener {
	
    public NMSNPC(UUID uuid, EntityType type, NMSRegistry registry) {
        if (!type.equals(EntityType.PLAYER)) throw new UnsupportedOperationException("Can only spawn players");
        this.type = type;
        this.UUID = uuid;
        this.registry = registry; 
        runTaskTimer(getRegistry().getPlugin(), 20, 1);
    }
    
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE) 
    private boolean protect;
    private final NMSRegistry registry;
    private Entity entity;
    private final UUID UUID;
    private final EntityType type;
    private String name = "";
    
    @Override
	public boolean despawn() {
	    if (!isSpawned()) return false;
	    getEntity().remove();
	    setEntity(null);
	    run();
	    cancel();
	    getRegistry().deregister(this);
	    return true;
	}
    
    @Override
	public void faceLocation(Location toFace) {
	    Util.look(getEntity(), toFace);
	}
	
    @Override
	public String getName() {
	    String name = this.name;
	    if (getEntity() == null) return name;
	    if (getEntity() != null && getEntity() instanceof LivingEntity) {
	        if (getEntity() instanceof HumanEntity) {
	            name = ((HumanEntity)getEntity()).getName();
	        }
	        name = ((LivingEntity)getEntity()).getCustomName();
	        if (name == null) return "";
	        this.name = name;
	    }
	    return name;
	}
    
    @Override
	public boolean isSpawned() {
	    return entity != null;
	}

	@Override
	public void setName(String name) {
	    if (name == null) return;
	    this.name = name;
	    if (isSpawned()) {
	    	if (getEntity() instanceof Player) {
	    		Location current = getEntity().getLocation();
			    despawn();
			    spawn(current);
	    	} else if (getEntity() instanceof LivingEntity) {
	    		((LivingEntity)getEntity()).setCustomName(name);
	    	}
	    }
	}

	@Override
	public boolean spawn(Location toSpawn) {
	    if (isSpawned()) return false;
	    Entity spawned = Util.spawn(toSpawn, getType(), getName(), this);
	    if (spawned != null) {
	        setEntity(spawned);
	        update(Bukkit.getOnlinePlayers());
	        return true;
	    } else return false;
	}

	@Override
	public void setProtected(boolean protect) {
		this.protect = protect;
	}
	
	@Override
	public boolean isProtected() {
		return protect;
	}
	
	private UUID skin;
	
	@Override
	public void setSkin(UUID skin) {
		if (!Util.getNMS().isSupported(OptionalFeature.SKINS)) throw new UnsupportedOperationException();
		this.skin = skin;
		if (isSpawned()) {
			Location last = getEntity().getLocation();
			getEntity().remove();
			Util.spawn(last, EntityType.PLAYER, getName(), this);
		}
	}
	
	@Override
	public void setSkin(String skin) {
	    if (skin == null) return;
	    PlayerProfile profile = ProfileUtils.lookup(skin);
	    if (profile == null) return;
	    setSkin(profile.getId());
	}
	
	//Update logic
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
	    if (!isSpawned()) return;
		update(event.getPlayer());
	}

	public void update(Player... players) {
		if (isSpawned()) {
     Util.getNMS().notifyOfSpawn(players, (Player)getEntity());
     tryEquipmentChangeNotify(players);
  } else Util.getNMS().notifyOfDespawn(players, (Player)getEntity());
	}
	
	@Override
	public void run() {
   if (!isSpawned()) return;
		tryEquipmentChangeNotify(Util.getNearbyPlayers(getRange(), getEntity().getLocation()));
	}
	
	private ItemStack[] lastArmor = new ItemStack[5];
	public void tryEquipmentChangeNotify(Player[] toNotify) {
		synchronized (lastArmor) {
			ArrayList<Integer> toUpdate = new ArrayList<>(5);
			for (int i = 0; i < 5; i++) {
				ItemStack lastArmor = this.lastArmor[i];
				ItemStack currentArmor = getEquipment(i);
				if (!equals(currentArmor, lastArmor)) toUpdate.add(i);
			}
			if (toUpdate.size() != 0) Util.getNMS().notifyOfEquipmentChange(toNotify, (Player)this.getEntity(), ArrayUtils.toPrimitive(toUpdate.toArray(new Integer[toUpdate.size()])));
		}
	}
	
	public static int getRange() {
		return (Bukkit.getViewDistance() * 16) + 24;
	}
	
	public ItemStack getEquipment(int slot) {
		switch (slot) {
		case 0 :
			return getEquipment().getItemInHand();
		case 1 :
			return getEquipment().getHelmet();
		case 2 :
			return getEquipment().getChestplate();
		case 3 :
			return getEquipment().getLeggings();
		case 4 :
			return getEquipment().getBoots();
		default :
			return null;
		}
	}
	
	public EntityEquipment getEquipment() {
		return ((LivingEntity)getEntity()).getEquipment();
	}
	
	public static boolean equals(ItemStack first, ItemStack second) {
		if (first == null) return second == null;
		return first.equals(second);
	}
}