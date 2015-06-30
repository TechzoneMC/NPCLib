package net.techcable.npclib.nms.ai;

import net.techcable.npclib.ai.AIEnvironment;
import net.techcable.npclib.nms.NMSNPC;

import org.bukkit.scheduler.BukkitRunnable;

public class NMSAIEnvironment extends AIEnvironment {

    private final NMSNPC npc;

    public NMSAIEnvironment(NMSNPC npc) {
        this.npc = npc;
        new BukkitRunnable() {

            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(npc.getRegistry().getPlugin(), 0, 1);
    }
}
