package net.techcable.npclib.ai;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import net.techcable.npclib.utils.NPCLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class AIEnvironment {
    private final Set<AITask> tasks = new HashSet<>();
    private final List<Runnable> callbacks = new ArrayList<>();

    protected void tick() {
        for (final AITask task : tasks) {
            try {
                task.tick(this);
            } catch (Throwable t) {
                NPCLog.warn("AI task " + task.getClass().getSimpleName() + " threw an exception", t);
            }
        }
        for (Runnable callback : callbacks) {
            try {
                callback.run();
            } catch (Throwable t) {
                NPCLog.warn("Callback " + callback.getClass().getSimpleName() + " threw an exception", t);
            }
        }
        callbacks.clear();
    }

    /**
     * Add a callback to be ticked as soon as possible
     * <p>
     * Tasks are guaranteed to be ticked before callbacks
     *
     * @param callback the callback to run
     */
    public void addCallback(Runnable callback) {
        Preconditions.checkNotNull(callback, "Null callback");
        callbacks.add(callback);
    }

    /**
     * Remove this task from the ai environment
     *
     * @param task the task to remove
     */
    public void addTask(AITask task) {
        Preconditions.checkNotNull(task, "Null task");
        tasks.add(task);
    }

    /**
     * Remove this task from the ai environment
     * <p>
     * Does nothing if the task isn't there
     *
     * @param task the task to remove
     */
    public void removeTask(AITask task) {
        Preconditions.checkNotNull(task, "Null task to remove");
        tasks.remove(task);
    }
}
