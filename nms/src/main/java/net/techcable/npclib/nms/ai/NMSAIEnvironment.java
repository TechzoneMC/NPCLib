package net.techcable.npclib.nms.ai;

import net.techcable.npclib.ai.AIEnvironment;
import net.techcable.npclib.nms.NMSNPC;

import org.bukkit.scheduler.BukkitRunnable;

public class NMSAIEnvironment extends AIEnvironment {

    private final NMSNPC npc;

    public NMSAIEnvironment(final NMSNPC npc) {
        this.npc = npc;
        new BukkitRunnable() {

            @Override
            public void run() {
                if (npc.isDestroyed()) cancel();
                if (!npc.isSpawned()) return;
                tick();
            }
        }.runTaskTimer(npc.getRegistry().getPlugin(), 0, 1);
    }
}
