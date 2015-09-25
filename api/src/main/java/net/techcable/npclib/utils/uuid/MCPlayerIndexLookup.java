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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import static net.techcable.npclib.utils.HttpUtils.getJson;

public class MCPlayerIndexLookup implements Lookup {

    @Override
    public PlayerProfile lookup(String name) {
        JSONObject json = (JSONObject) getJson("http://api.mcplayerindex.com/uuid/" + name);
        return deserializeProfile(json);
    }

    @Override
    public Collection<PlayerProfile> lookup(Collection<String> names) {
        Set<PlayerProfile> profiles = new HashSet<>();
        for (String name : names) {
            PlayerProfile profile = lookup(name);
            if (profile == null) continue;
            profiles.add(profile);
        }
        return profiles;
    }

    @Override
    public PlayerProfile lookup(UUID id) {
        return lookupProperties(id);
    }

    @Override
    public void lookupProperties(PlayerProfile profile) {
        if (profile.getProperties() != null) return;
        JSONArray properties = lookupProperties(profile.getId()).getProperties();
        profile.setProperties(properties);
    }

    private PlayerProfile lookupProperties(UUID id) {
        JSONObject json = (JSONObject) getJson("http://api.mcplayerindex.com/raw/" + id.toString() + "/signed");
        return deserializeProfile(json);
    }


    private PlayerProfile deserializeProfile(JSONObject json) {
        if (!json.containsKey("name") || !json.containsKey("id")) return null;
        if (!(json.get("name") instanceof String) || !(json.get("id") instanceof String)) return null;
        String name = (String) json.get("name");
        UUID id = toUUID((String) json.get("id"));
        if (id == null) return null;
        PlayerProfile profile = new PlayerProfile(id, name);
        if (json.containsKey("properties") && json.get("properties") instanceof JSONArray) {
            profile.setProperties((JSONArray) json.get("properties"));
        }
        return profile;
    }

    //Utilities

    private static String toString(UUID id) {
        return id.toString().replace("-", "");
    }

    private static UUID toUUID(String raw) {
        String dashed;
        if (raw.length() == 32) {
            dashed = raw.substring(0, 8) + "-" + raw.substring(8, 12) + "-" + raw.substring(12, 16) + "-" + raw.substring(16, 20) + "-" + raw.substring(20, 32);
        } else {
            dashed = raw;
        }
        return UUID.fromString(dashed);
    }
}
