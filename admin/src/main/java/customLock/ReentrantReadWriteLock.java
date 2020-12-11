package customLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * A custom reentrant read/write lock that allows:
 * 1) Multiple readers (when there is no writer). Any thread can acquire multiple read locks (if nobody is writing).
 * 2) One writer (when nobody else is writing or reading).
 * 3) A writer is allowed to acquire a read lock while holding the write lock.
 * 4) A writer is allowed to acquire another write lock while holding the write lock.
 * 5) A reader can not acquire a write lock while holding a read lock.
 * <p>
 * Use ReentrantReadWriteLockTest to test this class.
 * The code is modified from the code of Prof. Rollins.
 */
public class ReentrantReadWriteLock {
    private final static Logger log = LogManager.getRootLogger();
    private int readers;
    private int writers;
    private Map<Long, Holder> holders;

    /**
     * Constructor for ReentrantReadWriteLock
     */
    public ReentrantReadWriteLock() {
        // FILL IN CODE: initialize instance variables
        readers = 0;
        writers = 0;
        holders = new HashMap<>();
    }

    /**
     * Internal class, store all threads hold the lock
     */
    private static class Holder {
        private int readers;
        private int writers;

        Holder(int reader, int writer) {
            this.readers = reader;
            this.writers = writer;
        }

        int getReaders() {
            return readers;
        }

        int getWriters() {
            return writers;
        }

        void decreaseWriters() {
            if (writers > 0)
                writers--;
        }

        void decreaseReaders() {
            if (readers > 0)
                readers--;
        }

        void increaseReaders() {
            this.readers++;
        }

        void increaseWriters() {
            this.writers++;
        }

    }

    /**
     * Return true if the current thread holds a read lock.
     *
     * @return true or false
     */
    public synchronized boolean isReadLockHeldByCurrentThread() {
        long currentTreadId = Thread.currentThread().getId();
        if (!holders.containsKey(currentTreadId)) {
            return false;
        } else {
            Holder currentHolder = holders.get(currentTreadId);
            return currentHolder.getReaders() > 0;
        }
    }

    /**
     * Return true if the current thread holds a write lock.
     *
     * @return true or false
     */
    public synchronized boolean isWriteLockHeldByCurrentThread() {
        long currentTreadId = Thread.currentThread().getId();
        if (!holders.containsKey(currentTreadId)) {
            return false;
        } else {
            Holder currentHolder = holders.get(currentTreadId);
            return currentHolder.getWriters() > 0;
        }
    }

    /**
     * Non-blocking method that attempts to acquire the read lock. Returns true
     * if successful.
     * Checks conditions (whether it can acquire the read lock), and if they are true,
     * updates readers info.
     * <p>
     * Note that if conditions are false (can not acquire the read lock at the moment), this method
     * does NOT wait, just returns false
     *
     * @return is acquire read lock success
     */
    public synchronized boolean tryAcquiringReadLock() {
        long currentThreadId = Thread.currentThread().getId();
        if (writers > 0) {
            if (!holders.containsKey(currentThreadId)) {
                return false;
            } else {
                Holder currentHolder = holders.get(currentThreadId);
                if (currentHolder.writers > 0) {
                    readers++;
                    currentHolder.increaseReaders();
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            if (!holders.containsKey(currentThreadId)) {
                holders.put(currentThreadId, new Holder(1, 0));
            } else {
                holders.get(currentThreadId).increaseReaders();
            }
            readers++;
            return true;
        }
    }

    /**
     * Non-blocking method that attempts to acquire the write lock. Returns true
     * if successful.
     * Checks conditions (whether it can acquire the write lock), and if they are true,
     * updates writers info.
     * <p>
     * Note that if conditions are false (can not acquire the write lock at the moment), this method
     * does NOT wait, just returns false
     *
     * @return is acquire write lock success
     */
    public synchronized boolean tryAcquiringWriteLock() {
        long currentThreadId = Thread.currentThread().getId();
        if (writers > 0 || readers > 0) {
            if (!holders.containsKey(currentThreadId)) {
                return false;
            } else {
                Holder currentHolder = holders.get(currentThreadId);
                if (currentHolder.writers > 0) {
                    writers++;
                    currentHolder.increaseWriters();
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            if (!holders.containsKey(currentThreadId)) {
                holders.put(currentThreadId, new Holder(0, 1));
            } else {
                holders.get(currentThreadId).increaseWriters();
            }

            writers++;
            return true;
        }
    }

    /**
     * Blocking method that will return only when the read lock has been
     * acquired.
     * Calls tryAcquiringReadLock, and as long as it returns false, waits.
     * Catches InterruptedException.
     */
    public synchronized void lockRead() {
        while (!tryAcquiringReadLock()) {
            try {
                wait();
            } catch (InterruptedException e) {
                log.error(e);
            }
        }
    }

    /**
     * Releases the read lock held by the calling thread. Other threads might
     * still be holding read locks. If no more readers after unlocking, calls notifyAll().
     */
    public synchronized void unlockRead() {
        long currentThreadId = Thread.currentThread().getId();
        if (holders.containsKey(currentThreadId)) {
            Holder currentHolder = holders.get(currentThreadId);
            if (readers > 0) {
                readers--;
            }
            currentHolder.decreaseReaders();

            if (currentHolder.getReaders() == 0 && currentHolder.getWriters() == 0) {
                holders.remove(currentThreadId);
            }
        }

        if (readers == 0) {
            notifyAll();
        }
    }

    /**
     * Blocking method that will return only when the write lock has been
     * acquired.
     * Calls tryAcquiringWriteLock, and as long as it returns false, waits.
     * Catches InterruptedException.
     */
    public synchronized void lockWrite() {
        while (!tryAcquiringWriteLock()) {
            try {
                wait();
            } catch (InterruptedException e) {
                log.error(e);
            }
        }
    }

    /**
     * Releases the write lock held by the calling thread. The calling thread
     * may continue to hold a read lock.
     * If the number of writers becomes 0, calls notifyAll.
     */

    public synchronized void unlockWrite() {
        long currentThreadId = Thread.currentThread().getId();
        if (holders.containsKey(currentThreadId)) {
            Holder currentHolder = holders.get(currentThreadId);
            if (writers > 0) {
                writers--;
            }
            currentHolder.decreaseWriters();

            if (currentHolder.getReaders() == 0 && currentHolder.getWriters() == 0) {
                holders.remove(currentThreadId);
            }
        }

        if (writers == 0) {
            notifyAll();
        }
    }
}
