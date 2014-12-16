package techcable.minecraft.npclib.nms;

import java.util.UUID;

import techcable.minecraft.npclib.NPC;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import lombok.*;

@Getter
@Setter
public class NMSNPC implements NPC {
	
    public NMSNPC(UUID uuid, EntityType type, NMSRegistry registry) {
        if (!type.equals(EntityType.PLAYER)) throw new UnsupportedOperationException("Can only spawn players");
        this.type = type;
        this.UUID = uuid;
        this.registry = registry;    
    }
    
    @Setter(AccessLevel.NONE) //We have our own getters and setters
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
}