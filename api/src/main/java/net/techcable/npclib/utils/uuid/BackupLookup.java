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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Sets;

/**
 * A backup lookup first asks the primary lookup, and then asks the secondary lookup
 * <p/>
 * Useful to not completely rely on third-party services
 */
@RequiredArgsConstructor
@AllArgsConstructor
public class BackupLookup implements Lookup {

    private final Lookup primary, backup;
    private boolean treatNullsAsError = true;

    @Override
    public PlayerProfile lookup(String name) {
        PlayerProfile profile = null;
        try {
            profile = primary.lookup(name);
            if (!treatNullsAsError && profile == null) return profile;
        } catch (Throwable t) {
        }
        if (profile == null) {
            profile = backup.lookup(name);
        }
        return profile;
    }

    @Override
    public Collection<PlayerProfile> lookup(Collection<String> names) {
        Set<String> failures = Sets.newHashSet(names);
        Collection<PlayerProfile> results;
        boolean errored = false;
        try {
            results = primary.lookup(names);
        } catch (Throwable t) {
            errored = true;
            results = new HashSet<>();
        }
        for (PlayerProfile result : results) {
            failures.remove(result.getName());
        }
        if (errored || !failures.isEmpty() && treatNullsAsError) {
            results.addAll(backup.lookup(failures));
        }
        return results;
    }


    @Override
    public PlayerProfile lookup(UUID id) {
        PlayerProfile profile = null;
        try {
            profile = primary.lookup(id);
            if (!treatNullsAsError && profile == null) return profile;
        } catch (Throwable t) {
        }
        if (profile == null) {
            profile = backup.lookup(id);
        }
        return profile;
    }

    @Override
    public void lookupProperties(PlayerProfile profile) {
        if (profile.getProperties() != null) return;
        boolean errored = false;
        try {
            primary.lookupProperties(profile);
        } catch (Throwable t) {
            errored = true;
        }
        if (errored || profile.getProperties() == null && treatNullsAsError) {
            backup.lookupProperties(profile);
        }
    }
}