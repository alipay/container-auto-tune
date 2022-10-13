/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.autotuneservice.fake;

import org.redisson.api.BatchOptions;
import org.redisson.api.ClusterNodesGroup;
import org.redisson.api.ExecutorOptions;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.LockOptions.BackOff;
import org.redisson.api.MapOptions;
import org.redisson.api.Node;
import org.redisson.api.NodesGroup;
import org.redisson.api.RAtomicDouble;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBatch;
import org.redisson.api.RBinaryStream;
import org.redisson.api.RBitSet;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RBoundedBlockingQueue;
import org.redisson.api.RBucket;
import org.redisson.api.RBuckets;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RDeque;
import org.redisson.api.RDoubleAdder;
import org.redisson.api.RFunction;
import org.redisson.api.RGeo;
import org.redisson.api.RHyperLogLog;
import org.redisson.api.RIdGenerator;
import org.redisson.api.RKeys;
import org.redisson.api.RLexSortedSet;
import org.redisson.api.RList;
import org.redisson.api.RListMultimap;
import org.redisson.api.RListMultimapCache;
import org.redisson.api.RLiveObjectService;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RLock;
import org.redisson.api.RLongAdder;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RPatternTopic;
import org.redisson.api.RPermitExpirableSemaphore;
import org.redisson.api.RPriorityBlockingDeque;
import org.redisson.api.RPriorityBlockingQueue;
import org.redisson.api.RPriorityDeque;
import org.redisson.api.RPriorityQueue;
import org.redisson.api.RQueue;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RReliableTopic;
import org.redisson.api.RRemoteService;
import org.redisson.api.RRingBuffer;
import org.redisson.api.RScheduledExecutorService;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RScript;
import org.redisson.api.RSemaphore;
import org.redisson.api.RSet;
import org.redisson.api.RSetCache;
import org.redisson.api.RSetMultimap;
import org.redisson.api.RSetMultimapCache;
import org.redisson.api.RSortedSet;
import org.redisson.api.RStream;
import org.redisson.api.RTimeSeries;
import org.redisson.api.RTopic;
import org.redisson.api.RTransaction;
import org.redisson.api.RTransferQueue;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.RedissonRxClient;
import org.redisson.api.TransactionOptions;
import org.redisson.api.redisnode.BaseRedisNodes;
import org.redisson.api.redisnode.RedisNodes;
import org.redisson.client.codec.Codec;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

/**
 * @author huangkaifei
 * @version : FakeRedissonClient.java, v 0.1 2022年08月18日 7:49 PM huangkaifei Exp $
 */
public class FakeRedissonClient implements RedissonClient {
    @Override
    public <V> RTimeSeries<V> getTimeSeries(String s) {
        return null;
    }

    @Override
    public <V> RTimeSeries<V> getTimeSeries(String s, Codec codec) {
        return null;
    }

    @Override
    public <K, V> RStream<K, V> getStream(String s) {
        return null;
    }

    @Override
    public <K, V> RStream<K, V> getStream(String s, Codec codec) {
        return null;
    }

    @Override
    public RRateLimiter getRateLimiter(String s) {
        return null;
    }

    @Override
    public RBinaryStream getBinaryStream(String s) {
        return null;
    }

    @Override
    public <V> RGeo<V> getGeo(String s) {
        return null;
    }

    @Override
    public <V> RGeo<V> getGeo(String s, Codec codec) {
        return null;
    }

    @Override
    public <V> RSetCache<V> getSetCache(String s) {
        return null;
    }

    @Override
    public <V> RSetCache<V> getSetCache(String s, Codec codec) {
        return null;
    }

    @Override
    public <K, V> RMapCache<K, V> getMapCache(String s, Codec codec) {
        return null;
    }

    @Override
    public <K, V> RMapCache<K, V> getMapCache(String s, Codec codec, MapOptions<K, V> mapOptions) {
        return null;
    }

    @Override
    public <K, V> RMapCache<K, V> getMapCache(String s) {
        return null;
    }

    @Override
    public <K, V> RMapCache<K, V> getMapCache(String s, MapOptions<K, V> mapOptions) {
        return null;
    }

    @Override
    public <V> RBucket<V> getBucket(String s) {
        return (RBucket<V>) new Object();
    }

    @Override
    public <V> RBucket<V> getBucket(String s, Codec codec) {
        return null;
    }

    @Override
    public RBuckets getBuckets() {
        return null;
    }

    @Override
    public RBuckets getBuckets(Codec codec) {
        return null;
    }

    @Override
    public <V> RHyperLogLog<V> getHyperLogLog(String s) {
        return null;
    }

    @Override
    public <V> RHyperLogLog<V> getHyperLogLog(String s, Codec codec) {
        return null;
    }

    @Override
    public <V> RList<V> getList(String s) {
        return null;
    }

    @Override
    public <V> RList<V> getList(String s, Codec codec) {
        return null;
    }

    @Override
    public <K, V> RListMultimap<K, V> getListMultimap(String s) {
        return null;
    }

    @Override
    public <K, V> RListMultimap<K, V> getListMultimap(String s, Codec codec) {
        return null;
    }

    @Override
    public <K, V> RListMultimapCache<K, V> getListMultimapCache(String s) {
        return null;
    }

    @Override
    public <K, V> RListMultimapCache<K, V> getListMultimapCache(String s, Codec codec) {
        return null;
    }

    @Override
    public <K, V> RLocalCachedMap<K, V> getLocalCachedMap(String s,
                                                          LocalCachedMapOptions<K, V> localCachedMapOptions) {
        return null;
    }

    @Override
    public <K, V> RLocalCachedMap<K, V> getLocalCachedMap(String s,
                                                          Codec codec,
                                                          LocalCachedMapOptions<K, V> localCachedMapOptions) {
        return null;
    }

    @Override
    public <K, V> RMap<K, V> getMap(String s) {
        return null;
    }

    @Override
    public <K, V> RMap<K, V> getMap(String s, MapOptions<K, V> mapOptions) {
        return null;
    }

    @Override
    public <K, V> RMap<K, V> getMap(String s, Codec codec) {
        return null;
    }

    @Override
    public <K, V> RMap<K, V> getMap(String s, Codec codec, MapOptions<K, V> mapOptions) {
        return null;
    }

    @Override
    public <K, V> RSetMultimap<K, V> getSetMultimap(String s) {
        return null;
    }

    @Override
    public <K, V> RSetMultimap<K, V> getSetMultimap(String s, Codec codec) {
        return null;
    }

    @Override
    public <K, V> RSetMultimapCache<K, V> getSetMultimapCache(String s) {
        return null;
    }

    @Override
    public <K, V> RSetMultimapCache<K, V> getSetMultimapCache(String s, Codec codec) {
        return null;
    }

    @Override
    public RSemaphore getSemaphore(String s) {
        return null;
    }

    @Override
    public RPermitExpirableSemaphore getPermitExpirableSemaphore(String s) {
        return null;
    }

    @Override
    public RLock getLock(String s) {
        return null;
    }

    @Override
    public RLock getSpinLock(String s) {
        return null;
    }

    @Override
    public RLock getSpinLock(String s, BackOff backOff) {
        return null;
    }

    @Override
    public RLock getMultiLock(RLock... rLocks) {
        return null;
    }

    @Override
    public RLock getRedLock(RLock... rLocks) {
        return null;
    }

    @Override
    public RLock getFairLock(String s) {
        return null;
    }

    @Override
    public RReadWriteLock getReadWriteLock(String s) {
        return null;
    }

    @Override
    public <V> RSet<V> getSet(String s) {
        return null;
    }

    @Override
    public <V> RSet<V> getSet(String s, Codec codec) {
        return null;
    }

    @Override
    public <V> RSortedSet<V> getSortedSet(String s) {
        return null;
    }

    @Override
    public <V> RSortedSet<V> getSortedSet(String s, Codec codec) {
        return null;
    }

    @Override
    public <V> RScoredSortedSet<V> getScoredSortedSet(String s) {
        return null;
    }

    @Override
    public <V> RScoredSortedSet<V> getScoredSortedSet(String s, Codec codec) {
        return null;
    }

    @Override
    public RLexSortedSet getLexSortedSet(String s) {
        return null;
    }

    @Override
    public RTopic getTopic(String s) {
        return null;
    }

    @Override
    public RTopic getTopic(String s, Codec codec) {
        return null;
    }

    @Override
    public RReliableTopic getReliableTopic(String s) {
        return null;
    }

    @Override
    public RReliableTopic getReliableTopic(String s, Codec codec) {
        return null;
    }

    @Override
    public RPatternTopic getPatternTopic(String s) {
        return null;
    }

    @Override
    public RPatternTopic getPatternTopic(String s, Codec codec) {
        return null;
    }

    @Override
    public <V> RQueue<V> getQueue(String s) {
        return null;
    }

    @Override
    public <V> RTransferQueue<V> getTransferQueue(String s) {
        return null;
    }

    @Override
    public <V> RTransferQueue<V> getTransferQueue(String s, Codec codec) {
        return null;
    }

    @Override
    public <V> RDelayedQueue<V> getDelayedQueue(RQueue<V> rQueue) {
        return null;
    }

    @Override
    public <V> RQueue<V> getQueue(String s, Codec codec) {
        return null;
    }

    @Override
    public <V> RRingBuffer<V> getRingBuffer(String s) {
        return null;
    }

    @Override
    public <V> RRingBuffer<V> getRingBuffer(String s, Codec codec) {
        return null;
    }

    @Override
    public <V> RPriorityQueue<V> getPriorityQueue(String s) {
        return null;
    }

    @Override
    public <V> RPriorityQueue<V> getPriorityQueue(String s, Codec codec) {
        return null;
    }

    @Override
    public <V> RPriorityBlockingQueue<V> getPriorityBlockingQueue(String s) {
        return null;
    }

    @Override
    public <V> RPriorityBlockingQueue<V> getPriorityBlockingQueue(String s, Codec codec) {
        return null;
    }

    @Override
    public <V> RPriorityBlockingDeque<V> getPriorityBlockingDeque(String s) {
        return null;
    }

    @Override
    public <V> RPriorityBlockingDeque<V> getPriorityBlockingDeque(String s, Codec codec) {
        return null;
    }

    @Override
    public <V> RPriorityDeque<V> getPriorityDeque(String s) {
        return null;
    }

    @Override
    public <V> RPriorityDeque<V> getPriorityDeque(String s, Codec codec) {
        return null;
    }

    @Override
    public <V> RBlockingQueue<V> getBlockingQueue(String s) {
        return null;
    }

    @Override
    public <V> RBlockingQueue<V> getBlockingQueue(String s, Codec codec) {
        return null;
    }

    @Override
    public <V> RBoundedBlockingQueue<V> getBoundedBlockingQueue(String s) {
        return null;
    }

    @Override
    public <V> RBoundedBlockingQueue<V> getBoundedBlockingQueue(String s, Codec codec) {
        return null;
    }

    @Override
    public <V> RDeque<V> getDeque(String s) {
        return null;
    }

    @Override
    public <V> RDeque<V> getDeque(String s, Codec codec) {
        return null;
    }

    @Override
    public <V> RBlockingDeque<V> getBlockingDeque(String s) {
        return null;
    }

    @Override
    public <V> RBlockingDeque<V> getBlockingDeque(String s, Codec codec) {
        return null;
    }

    @Override
    public RAtomicLong getAtomicLong(String s) {
        return null;
    }

    @Override
    public RAtomicDouble getAtomicDouble(String s) {
        return null;
    }

    @Override
    public RLongAdder getLongAdder(String s) {
        return null;
    }

    @Override
    public RDoubleAdder getDoubleAdder(String s) {
        return null;
    }

    @Override
    public RCountDownLatch getCountDownLatch(String s) {
        return null;
    }

    @Override
    public RBitSet getBitSet(String s) {
        return null;
    }

    @Override
    public <V> RBloomFilter<V> getBloomFilter(String s) {
        return null;
    }

    @Override
    public <V> RBloomFilter<V> getBloomFilter(String s, Codec codec) {
        return null;
    }

    @Override
    public RIdGenerator getIdGenerator(String s) {
        return null;
    }

    @Override
    public RFunction getFunction() {
        return null;
    }

    @Override
    public RFunction getFunction(Codec codec) {
        return null;
    }

    @Override
    public RScript getScript() {
        return null;
    }

    @Override
    public RScript getScript(Codec codec) {
        return null;
    }

    @Override
    public RScheduledExecutorService getExecutorService(String s) {
        return null;
    }

    @Override
    public RScheduledExecutorService getExecutorService(String s, ExecutorOptions executorOptions) {
        return null;
    }

    @Override
    public RScheduledExecutorService getExecutorService(String s, Codec codec) {
        return null;
    }

    @Override
    public RScheduledExecutorService getExecutorService(String s, Codec codec,
                                                        ExecutorOptions executorOptions) {
        return null;
    }

    @Override
    public RRemoteService getRemoteService() {
        return null;
    }

    @Override
    public RRemoteService getRemoteService(Codec codec) {
        return null;
    }

    @Override
    public RRemoteService getRemoteService(String s) {
        return null;
    }

    @Override
    public RRemoteService getRemoteService(String s, Codec codec) {
        return null;
    }

    @Override
    public RTransaction createTransaction(TransactionOptions transactionOptions) {
        return null;
    }

    @Override
    public RBatch createBatch(BatchOptions batchOptions) {
        return null;
    }

    @Override
    public RBatch createBatch() {
        return null;
    }

    @Override
    public RKeys getKeys() {
        return null;
    }

    @Override
    public RLiveObjectService getLiveObjectService() {
        return null;
    }

    @Override
    public RedissonRxClient rxJava() {
        return null;
    }

    @Override
    public RedissonReactiveClient reactive() {
        return null;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void shutdown(long l, long l1, TimeUnit timeUnit) {

    }

    @Override
    public Config getConfig() {
        return null;
    }

    @Override
    public <T extends BaseRedisNodes> T getRedisNodes(RedisNodes<T> redisNodes) {
        return null;
    }

    @Override
    public NodesGroup<Node> getNodesGroup() {
        return null;
    }

    @Override
    public ClusterNodesGroup getClusterNodesGroup() {
        return null;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isShuttingDown() {
        return false;
    }

    @Override
    public String getId() {
        return null;
    }
}