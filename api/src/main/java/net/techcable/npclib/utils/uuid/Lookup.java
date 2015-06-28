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
import java.util.UUID;

public interface Lookup {

    /**
     * Lookup a profile with the given name
     * <p/>
     * The returned player profile may or may not include properties
     * If properties are needed, proceed to use a property lookup
     *
     * @param name look for a profile with this name
     *
     * @return a profile with the given name
     */
    public PlayerProfile lookup(String name);

    /**
     * Lookup the profiles of the given name
     * <p/>
     * The returned player profiles may or may not include properties
     * If properties are needed, proceed to lookup properties
     *
     * @param names
     *
     * @return
     */
    public Collection<PlayerProfile> lookup(Collection<String> names);

    /**
     * Lookup a profile with the given uuid
     * <p/>
     * The returned player profile may or may not include properties
     *
     * @param id look for a profile with this uuid
     *
     * @return a profile with the given id
     */
    public PlayerProfile lookup(UUID id);

    /**
     * Lookup the properties of the given profile
     * <p/>
     * This method does nothing if the profile already has properties
     *
     * @param profile the profile to lookup properties for
     */
    public void lookupProperties(PlayerProfile profile);
}
