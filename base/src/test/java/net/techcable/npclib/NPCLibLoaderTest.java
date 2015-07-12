package net.techcable.npclib;

import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class NPCLibLoaderTest {

    @Test
    public void testLoader() throws IOException {
        NPCLibLoader.loadNPCLib("2.0.0-beta1-SNAPSHOT");
        assertFalse("NPCLib is supported without bukkit installed", NPCLibLoader.isSupported());
    }
}
