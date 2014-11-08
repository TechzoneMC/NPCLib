package techcable.minecraft.npclib.util.versioning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.*;

public class CraftbukkitVersionManager {
    private CraftbukkitVersionManager() {}
    
    private List<CraftbukkitVersion> versions = new ArrayList<>();
    private Map<Class, Set<VersionedClass>> versionedClassMap = new HashMap<>(); 
    @Setter
    private CraftbukkitVersion currentVersion;
    
    public CraftbukkitVersion getCurrentVersion() {
        if (currentVersion == null) computeVersion();
        return currentVersion;
    }
    
    public void computeVersion() {
        for (CraftbukkitVersion version : versions) {
            if (vesion.isVersion()) {
                setCurrentVersion(version);
                return;
            }
        }
    }
    
    static {
	    initVersions();
    }
    
    /**
     * Add all known versions
     * First compatable version will be selected
     * 
     */
    private void initVersions() {
	    versions.add(new CraftbukkitVersion("spigot-1.8-protocolhack", "1.7.10", "1_7_R4") {
	       
	       @Override
	       public boolean isVersion() {
	           if (!super.isVersion()) return false;
	           try {
	               Class.forName("org.spigotmc.ProtocolData");
	           } catch (Exception ex) {
	               return false;
	           }
	       }
	    });
	    versions.add(new CraftbukkitVersion("1.7.10-R0.1-SNAPSHOT", "1.7.10", "1_7_R4"));
    }
    
    public <T extends VersionedClass> void registerVersionedClass(Class<T> spec, T instance) {
        versionedClassMap.put(spec, instance);
    }
    
    private EasyCache<Class, VersionedClass> cache = new EasyCache(new Loader<Class, VersionedClass>() {
        @Override
        public VersionedClass load(Class spec) {
            for (VersionedClass clazz : versionedClassMap.get(spec)) {
                if (clazz.isCompatable(getCurrentVersion())) return clazz;
            }
            return null;
        }
    });
    public <T extends VersionedClass> T getVersioned(Class<T> spec) {
        cache.get(spec);
    }
}
