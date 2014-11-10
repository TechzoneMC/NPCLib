package techcable.minecraft.npclib.util.versioning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import techcable.minecraft.npclib.util.EasyCache;

import lombok.*;

public class CraftbukkitVersionManager {
    private CraftbukkitVersionManager() {}
    
    private static List<CraftbukkitVersion> versions = new ArrayList<>();
    private static Map<Class, Set<VersionedClass>> versionedClassMap = new HashMap<>(); 
    @Setter
    private static CraftbukkitVersion currentVersion;
    
    public static CraftbukkitVersion getCurrentVersion() {
        if (currentVersion == null) computeVersion();
        return currentVersion;
    }
    
    public static void computeVersion() {
        for (CraftbukkitVersion version : versions) {
            if (version.isVersion()) {
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
    private static void initVersions() {
	    versions.add(new CraftbukkitVersion("spigot-1.8-protocolhack", "1.7.10", "1_7_R4") {
	       
	       @Override
	       public boolean isVersion() {
	           if (/* If not 1.7.10 it can't be the protocol hack */ !super.isVersion()) return false;
	           try {
	               Class.forName("org.spigotmc.ProtocolData");
	           } catch (Exception ex) {
	               return false;
	           }
	           return true;
	       }
	    });
	    versions.add(new CraftbukkitVersion("1.7.10-R0.1-SNAPSHOT", "1.7.10", "1_7_R4"));
    }
    
    public <T extends VersionedClass> void registerVersionedClass(Class<T> spec, T instance) {
    	if (versionedClassMap.get(spec) == null) {
    		versionedClassMap.put(spec, new HashSet<VersionedClass>());
    	}
        versionedClassMap.get(spec).add(instance);
    }
    
    private static EasyCache<Class<?>, VersionedClass> cache = new EasyCache<>(new EasyCache.Loader<Class<?>, VersionedClass>() {
        @Override
        public VersionedClass load(Class<?> spec) {
            for (VersionedClass clazz : versionedClassMap.get(spec)) {
                if (clazz.isCompatable(getCurrentVersion())) return clazz;
            }
            return null;
        }
    });
    @SuppressWarnings("unchecked")
	public static <T extends VersionedClass> T getVersioned(Class<T> spec) {
        return (T) cache.get(spec);
    }
}
