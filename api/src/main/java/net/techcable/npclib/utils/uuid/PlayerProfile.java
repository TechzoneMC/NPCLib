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

import org.json.simple.JSONArray;

/**
 * Represnents a player
 * Contains their uuid and username
 * <p/>
 * This may or may not have properties
 *
 * @author Techcable
 */
@EqualsAndHashCode(of = {"id", "name"})
public class PlayerProfile {

    public PlayerProfile(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    @Setter(AccessLevel.PACKAGE)
    private JSONArray properties;
    private final UUID id;
    private final String name;

    /**
     * Get this player's uuid
     *
     * @return this players uuid
     */
    public UUID getId() {
        return id;
    }

    /**
     * Get this player's name
     *
     * @return this player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Get a json array with this players properties
     * Returns null if this players propeties haven't been retreived
     *
     * @return a json array with this player's properties or null if not retreived
     */
    public JSONArray getProperties() {
        return properties;
    }
}
