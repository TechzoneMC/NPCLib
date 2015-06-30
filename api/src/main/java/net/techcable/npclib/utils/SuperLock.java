package net.techcable.npclib.utils;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SuperLock {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public void lockFreedom() {
        lock.readLock().lock();
    }

    public void unlockFreedom() {
        lock.readLock().unlock();
    }

    public void lock() {
        lock.writeLock().lock();
    }

    public void unlock() {
        lock.writeLock().unlock();
    }
}
