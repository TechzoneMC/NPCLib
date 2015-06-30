package net.techcable.npclib.ai;

import lombok.*;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import net.techcable.npclib.utils.SuperLock;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

@RequiredArgsConstructor
public class AIEnvironment {

    private final static ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(2, new ThreadFactoryBuilder().setNameFormat("NPCLib AI %d").setDaemon(true).build()));
    private final SuperLock lock = new SuperLock();
    private final Set<AITask> tasks = Sets.newSetFromMap(new ConcurrentHashMap<AITask, Boolean>());
    private final Map<Runnable, Boolean> callbacks = new ConcurrentHashMap<>();

    protected void tick() {
        lock.lock();
        try {
            for (final AITask task : tasks) {
                task.tick(this);
            }
            Iterator<Entry<Runnable, Boolean>> i = callbacks.entrySet().iterator();
            while (i.hasNext()) {
                Entry<Runnable, Boolean> entry = i.next();
                final Runnable callback = entry.getKey();
                boolean sync = entry.getValue();
                if (sync) {
                    callback.run();
                } else {
                    executor.submit(callback);
                }
                i.remove();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Add a callback to be ticked as soon as possible
     * <p>
     * Tasks are guaranteed to be ticked before callbacks
     * Long running tasks should be put in callbacks to allo
     * <b>Unlike tasks, callbacks run async unless specified otherwise</b>
     * </p>
     *
     * @param callback the callback to run
     */
    public void addCallback(Runnable callback) {
        addCallback(callback, false);
    }

    /**
     * Add a callback to be ticked as soon as possible
     * <p>
     * Tasks are guaranteed to be ticked before callbacks
     * Long running tasks should be put in callbacks to allo
     * <b>Unlike tasks, callbacks run async unless specified otherwise</b>
     * </p>
     *
     * @param callback the callback to run
     * @param sync whether to run sync
     */
    public void addCallback(Runnable callback, boolean sync) {
        lock.lockFreedom();
        try {
            callbacks.put(callback, sync);
        } finally {
            lock.unlockFreedom();
        }
    }

    /**
     * Remove this task from the ai environment
     *
     * @param task the task to remove
     */
    public void addTask(AITask task) {
        lock.lockFreedom();
        try {
            tasks.add(task);
        } finally {
            lock.unlockFreedom();
        }
    }

    /**
     * Remove this task from the ai environment
     *
     * @param task the task to remove
     */
    public void removeTask(AITask task) {
        lock.lockFreedom();
        try {
            tasks.remove(task);
        } finally {
            lock.unlockFreedom();
        }
    }

    /**
     * Run the specified task as soon as possible in an async executor
     *
     * @param runnable the task to run
     */
    public AISubTask runSoon(Runnable runnable) {
        return new AISubTask(executor.submit(runnable));
    }

    @RequiredArgsConstructor
    public static class AISubTask {

        private final ListenableFuture<?> backing;

        public boolean isDone() {
            return backing.isDone();
        }

        public void addListener(Runnable onComplete) {
            backing.addListener(onComplete, MoreExecutors.sameThreadExecutor());
        }

    }

}
