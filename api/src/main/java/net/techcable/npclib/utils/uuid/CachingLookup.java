/**
 * The MIT License
 * Copyright (c) 2014-2015 Techcable
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.techcable.npclib.utils.uuid;

import lombok.*;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.json.simple.JSONArray;

import com.google.common.collect.Lists;

@RequiredArgsConstructor
public class CachingLookup implements Lookup {

    private final Lookup backing;
    private final Cache<UUID, PlayerProfile> idCache = new Cache<>();
    private final Cache<String, PlayerProfile> nameCache = new Cache<>();
    private final Cache<PlayerProfile, JSONArray> propertyCache = new Cache<>();

    public PlayerProfile lookup(String name) {
        Iterator<PlayerProfile> iterator = lookup(Lists.newArrayList(name)).iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    @Override
    public Collection<PlayerProfile> lookup(Collection<String> names) {
        Set<PlayerProfile> profiles = new HashSet<>();
        Set<String> toLookup = new HashSet<>();
        for (String name : names) {
            if (nameCache.contains(name)) {
                profiles.add(nameCache.get(name));
            } else {
                toLookup.add(name);
            }
        }
        for (PlayerProfile profile : backing.lookup(toLookup)) {
            addToCache(profile);
            profiles.add(profile);
        }
        return profiles;
    }

    public PlayerProfile lookup(UUID id) {
        if (idCache.contains(id)) return idCache.get(id);
        PlayerProfile profile = backing.lookup(id);
        if (profile == null) return null;
        addToCache(profile);
        return profile;
    }

    @Override
    public void lookupProperties(PlayerProfile profile) {
        if (profile == null) return;
        if (profile.getProperties() != null) return;
        if (propertyCache.contains(profile)) {
            JSONArray properties = propertyCache.get(profile);
            profile.setProperties(properties);
        }
        backing.lookupProperties(profile);
        addToCache(profile);
    }

    private static class Cache<K, V> {

        private long expireTime = 1000 * 60 * 5; //default 5 min
        private Map<K, CachedEntry<V>> map = new HashMap<>();

        public boolean contains(K key) {
            return map.containsKey(key) && get(key) != null;
        }

        public V get(K key) {
            CachedEntry<V> entry = map.get(key);
            if (entry == null) return null;
            if (entry.isExpired()) {
                map.remove(key);
                return null;
            } else {
                return entry.getValue();
            }
        }

        public void put(K key, V value) {
            map.put(key, new CachedEntry(value, expireTime));
        }

        private static class CachedEntry<V> {

            public CachedEntry(V value, long expireTime) {
                this.value = new SoftReference(value);
                this.expires = expireTime + System.currentTimeMillis();
            }

            private final SoftReference<V> value; //Caching is low memory priortiy
            private final long expires;

            public V getValue() {
                if (isExpired()) {
                    return null;
                }
                return value.get();
            }

            public boolean isExpired() {
                if (value.get() == null) return true;
                return expires != -1 && expires > System.currentTimeMillis();
            }
        }
    }

    public void addToCache(PlayerProfile profile) {
        nameCache.put(profile.getName(), profile);
        idCache.put(profile.getId(), profile);
        if (profile.getProperties() != null) {
            propertyCache.put(profile, profile.getProperties());
        }
    }

    public PlayerProfile getIfCached(String name) {
        if (!nameCache.contains(name)) return null;
        return nameCache.get(name);
    }

    public PlayerProfile getIfCached(UUID id) {
        if (!idCache.contains(id)) return null;
        return idCache.get(id);
    }
}
