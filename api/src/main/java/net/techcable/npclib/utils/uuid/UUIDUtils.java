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

import java.util.UUID;

import net.techcable.npclib.utils.Reflection;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.google.common.base.Charsets;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UUIDUtils {

    @Getter
    private static final CachingLookup lookup = new CachingLookup(new BackupLookup(new MCPlayerIndexLookup(), new MojangLookup()));

    /**
     * Retreive a player's UUID based on it's name
     * <p/>
     * Returns null if lookup failed
     *
     * @param name the player's name
     *
     * @return the player's uuid, or null if failed
     */
    public static UUID getId(String name) {
        if (lookup.getIfCached(name) != null) return lookup.getIfCached(name).getId(); //Previously cached by UUIDUtils.getPlayerExact()
        if (getPlayerExact(name) != null) {
            return getPlayerExact(name).getUniqueId();
        }
        if (Bukkit.getOnlineMode()) {
            PlayerProfile profile = lookup.lookup(name);
            if (profile == null) return null;
            return profile.getId();
        } else {
            return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
        }
    }

    /**
     * Retreive a player's name based on it's uuid
     * <p/>
     * Returns null if lookup failed
     *
     * @param id the player's uuid
     *
     * @return the player's name, or null if failed
     */
    public static String getName(UUID id) {
        if (lookup.getIfCached(id) != null) return lookup.getIfCached(id).getName();
        if (hasBukkit() && Bukkit.getPlayer(id) != null) {
            String name = Bukkit.getPlayer(id).getName();
            lookup.addToCache(new PlayerProfile(id, name)); //Saves us a potential lookup by staying in the cache after player leaves
            return name;
        }
        if (!hasBukkit() || Bukkit.getOnlineMode()) {
            PlayerProfile profile = lookup.lookup(id);
            if (profile == null) return null;
            return profile.getName();
        } else {
            OfflinePlayer player = Bukkit.getOfflinePlayer(id);
            return player.getName();
        }
    }

    /**
     * A faster version of Bukkit.getPlayerExact()
     * <p/>
     * Bukkit.getPlayerExact() iterates through all online players <br>
     * This caches results from Bukkit.getPlayerExact() to speed up lookups
     *
     * @param name get player with this name
     *
     * @return player with specified name
     *
     * @see org.bukkit.Bukkit#getPlayerExact(String)
     */
    public static Player getPlayerExact(String name) {
        if (lookup.getIfCached(name) != null) return Bukkit.getPlayer(lookup.getIfCached(name).getId());
        if (Bukkit.getPlayerExact(name) != null) {
            UUID id = Bukkit.getPlayerExact(name).getUniqueId();
            /*
             * Calling Bukkit.getPlayerExact() iterates through all online players, making it far slower than hashmap retreival
             * This has the added benefit of remaining in the cache even after the player leaves; potentially saving a mojang lookup for uuid fetching
             */
            lookup.addToCache(new PlayerProfile(id, name));
            return Bukkit.getPlayer(id);
        }
        return null;
    }

    public static boolean hasBukkit() {
        return Reflection.getClass("org.bukkit.Bukkit") != null && Bukkit.getServer() != null;
    }
}