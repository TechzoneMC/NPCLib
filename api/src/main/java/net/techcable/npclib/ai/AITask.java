package net.techcable.npclib.ai;

/**
 * A task for this npc's AI to perform
 */
public interface AITask {

    /**
     * Run this task in the given ai environment
     *
     * @param environment the ai environment
     */
    public void tick(AIEnvironment environment);
}
