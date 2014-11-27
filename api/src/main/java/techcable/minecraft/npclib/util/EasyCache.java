package techcable.minecraft.npclib.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import lombok.*;

@Getter
public class EasyCache<K, V> {
    private final Cache<K, V> backing;
    public EasyCache(Loader<K, V> loader) {
        backing = CacheBuilder.newBuilder().weakKeys().weakValues().build(new LoaderCacheLoader<K, V>(loader));
    }
    
    public V get(K key) {
        return backing.getUnchecked(key);
    }
    
    public static interface Loader<K, V> {
        public V load(K key);
    }
    
    @Getter
    private static class LoaderCacheLoader<K, V> extends CacheLoader<K, V> {
        
        private LoaderCacheLoader(Loader<K, V> backing) {
            this.backing = backing;
        }
        private final Loader<K, V> backing;
        
        @Override
        public V load(K key) {
            return backing.load(key);
        }
    }
}