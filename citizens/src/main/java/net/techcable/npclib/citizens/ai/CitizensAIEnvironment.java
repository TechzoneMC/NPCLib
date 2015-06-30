package net.techcable.npclib.citizens.ai;

import net.citizensnpcs.api.CitizensAPI;
import net.techcable.npclib.ai.AIEnvironment;
import net.techcable.npclib.citizens.CitizensNPC;

import org.bukkit.scheduler.BukkitRunnable;

public class CitizensAIEnvironment extends AIEnvironment {

    private final CitizensNPC npc;

    public CitizensAIEnvironment(CitizensNPC npc) {
        this.npc = npc;
        new BukkitRunnable() {

            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(CitizensAPI.getPlugin(), 0, 1);
    }
}
