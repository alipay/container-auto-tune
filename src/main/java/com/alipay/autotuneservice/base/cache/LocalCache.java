package com.alipay.autotuneservice.base.cache;

import java.util.LinkedHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 线程安全的本地缓存。
 *
 * @author chenqu
 * @version : LocalCache.java, v 0.1 2021年12月30日 17:52 chenqu Exp $
 */
public abstract class LocalCache<K, V> {

    protected     LinkedHashMap<K, CacheEntry> cache;
    private final ReadWriteLock                lock = new ReentrantReadWriteLock();
    protected     int                          capacity;

    protected class CacheEntry {
        private final K    key;
        private final V    value;
        private       long lastAccessTime;
        private       int  accessCount;
        private final long expirationMilliSeconds;

        public CacheEntry(K key, V value, long expirationMilliSeconds) {
            this.key = key;
            this.value = value;
            this.lastAccessTime = System.currentTimeMillis();
            this.accessCount = 0;
            this.expirationMilliSeconds = expirationMilliSeconds;
        }

        public V accessValue() {
            this.lastAccessTime = System.currentTimeMillis();
            this.accessCount++;
            return this.value;
        }

        public K getKey() {
            return this.key;
        }

        public long getLastAccessTime() {
            return this.lastAccessTime;
        }

        public int getAccessCount() {
            return this.accessCount;
        }

        public boolean isAlive() {
            if (this.expirationMilliSeconds == 0) {
                return true;
            }
            return this.expirationMilliSeconds > getLiveTimeLeft();
        }

        private long getLiveTimeLeft() {
            if (this.expirationMilliSeconds > 0) {
                return System.currentTimeMillis() - this.lastAccessTime;
            }
            return 0;
        }

    }

    public V put(K key, V value) {
        return doPut(key, value, 0);
    }

    public V put(K key, V value, long expiration) {
        return doPut(key, value, expiration);
    }

    private V doPut(K key, V value, long expiration) {
        lock.writeLock().lock();
        try {
            if (isFull() && !cache.containsKey(key)) {
                cleanExpiredEntries();
                if (isFull()) {
                    eliminate();
                }
            }
            CacheEntry entry = new CacheEntry(key, value, expiration);
            CacheEntry oldEntry = cache.put(key, entry);
            return oldEntry == null ? null : oldEntry.accessValue();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public V get(K key) {
        lock.readLock().lock();
        try {
            CacheEntry entry = cache.get(key);
            if (entry != null && !entry.isAlive()) {
                lock.readLock().unlock();
                lock.writeLock().lock();
                try {
                    removeSimply(key);
                    entry = null;
                    lock.readLock().lock();
                } finally {
                    lock.writeLock().unlock();
                }
            }
            return entry != null ? entry.accessValue() : null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public V remove(K key) {
        lock.writeLock().lock();
        try {
            CacheEntry entry = cache.remove(key);
            return entry != null ? entry.accessValue() : null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void removeSimply(K key) {
        cache.remove(key);
    }

    public int size() {
        lock.readLock().lock();
        try {
            return cache.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getCapacity() {
        return capacity;
    }

    protected boolean isFull() {
        lock.readLock().lock();
        try {
            return cache.size() == capacity;
        } finally {
            lock.readLock().unlock();
        }
    }

    protected void cleanExpiredEntries() {
        cache.entrySet().removeIf(entry -> !entry.getValue().isAlive());
    }

    protected abstract void eliminate();
}
