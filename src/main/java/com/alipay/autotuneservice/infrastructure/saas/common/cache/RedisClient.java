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
package com.alipay.autotuneservice.infrastructure.saas.common.cache;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author huoyuqi
 * @version RedisClient.java, v 0.1 2022年03月24日 11:17 上午 huoyuqi
 */
@Slf4j
@Component
public class RedisClient {
    private final RedissonClient redissonClient;

    public RedisClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public RedissonClient getRedissonClient() {
        return redissonClient;
    }

    /**
     * 执行redis setnx 命令
     *
     * @param key     key
     * @param value   value
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return
     */
    public boolean setNx(String key, Object value, long timeout, TimeUnit unit) {
        return redissonClient.getBucket(key).trySet(value, timeout, unit);
    }

    /**
     * 执行redis setex命令
     *
     * @param key     key
     * @param value   value
     * @param timeout 超时时间
     * @param unit    时间单位
     */
    public void setEx(String key, Object value, long timeout, TimeUnit unit) {
        redissonClient.getBucket(key).set(value, timeout, unit);
    }

    /**
     * 获取缓存
     *
     * @param key   缓存key
     * @param clazz 序列化的对象class
     * @param <T>   范型
     * @return T
     */
    public <T> T get(String key, Class<T> clazz) {
        Object o = redissonClient.getBucket(key).get();
        return clazz.cast(o);
    }

    /**
     * 获取缓存，不序列化
     *
     * @param key 缓存key
     * @return Object
     */
    public Object get(String key) {
        return redissonClient.getBucket(key).get();
    }

    public String get(String key, Codec codec) {
        return redissonClient.getBucket(key, codec).get().toString();
    }

    /**
     * 缓存
     *
     * @param key   key
     * @param value 缓存对象
     */
    public void set(String key, Object value) {
        redissonClient.getBucket(key).set(value);
    }

    /**
     * 缓存，带超时时间
     *
     * @param key    key
     * @param value  缓存对象
     * @param expire 超时时间
     */
    public void set(String key, Object value, long expire) {
        redissonClient.getBucket(key).set(value, expire, TimeUnit.SECONDS);
    }

    /**
     * 删除缓存
     *
     * @param key 缓存key
     */
    public void remove(String key) {
        redissonClient.getBucket(key).delete();
    }

    /**
     * 累加
     *
     * @param key     key
     * @param counter 累加数量
     */
    public void setIncrValue(String key, int counter) {
        redissonClient.getAtomicLong(key).set(counter);
    }

    /**
     * 获取累加结果
     *
     * @param key key
     * @return 累加结果
     */
    public long getIncrValue(String key) {
        return redissonClient.getAtomicLong(key).get();
    }

    /**
     * 累加（+1）
     *
     * @param key key
     * @return 返回加1的结果
     */
    public long incr(String key) {
        return redissonClient.getAtomicLong(key).incrementAndGet();
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    public void del(String key) {
        redissonClient.getBucket(key).delete();
    }

    /**
     * 范围查询
     *
     * @param key key
     * @return List<Object>
     */
    public List<Object> lrange(String key) {
        return redissonClient.getList(key).readAll();
    }

    /**
     * redis rpush
     *
     * @param key key
     * @param obj 缓存对象
     * @return boolean
     */
    public boolean rpush(String key, Object obj) {
        return redissonClient.getList(key).add(obj);
    }

    /**
     * redis sadd
     *
     * @param key key
     * @param obj 缓存对象
     * @return boolean
     */
    public boolean sadd(String key, String obj) {
        return redissonClient.getSet(key).add(obj);
    }

    /**
     * redis sremove
     *
     * @param key key
     * @param obj 缓存对象
     * @return boolean
     */
    public void smove(String key, Object obj) {
        redissonClient.getSet(key).remove(obj);
    }

    /**
     * redis sismember
     *
     * @param key key
     * @param obj 缓存对象
     * @return boolean
     */
    public boolean sismember(String key, Object obj) {
        return redissonClient.getSet(key).contains(obj);
    }

    /**
     * redis lock
     *
     * @param key        key
     * @param lockAction 加锁完成之后的事情
     * @return boolean
     */
    public void lock(String key, AbsLockAction lockAction) {
        RLock lock = redissonClient.getLock(key);
        try {
            lock.lock();
            lockAction.onAcquire(key);
            lockAction.doInLock(key);
        } finally {
            lock.unlock();
            lockAction.onExit(key);
        }
    }

    /**
     * redis 尝试加锁
     *
     * @param key        key
     * @param lockAction 加锁完成之后的事情
     * @return boolean
     */
    public void tryLock(String key, int waitTime, AbsLockAction lockAction)
                                                                           throws InterruptedException {
        RLock lock = redissonClient.getLock(key);
        boolean tryLock = lock.tryLock(waitTime, TimeUnit.SECONDS);
        if (tryLock) {
            try {
                lockAction.onAcquire(key);
                lockAction.doInLock(key);
            } finally {
                lock.unlock();
                lockAction.onExit(key);
            }
        } else {
            lockAction.tryLockFail(key);
        }
    }

    /**
     * @return booleanF
     * @Author YiJin
     * @Description 阻塞锁
     * @Date 2022/7/18
     * @Param [key, waitTime（单位：秒）]
     **/
    public boolean tryLock(String key, int waitTime) {
        RLock lock = redissonClient.getLock(key);
        try {
            return lock.tryLock(waitTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return void
     * @Author YiJin
     * @Description 解锁
     * @Date 2022/7/19
     * @Param [key]
     **/
    public void unLock(String key) {
        redissonClient.getLock(key).unlock();
    }

    /**
     * redis lock
     *
     * @param lockKey  key
     * @param runnable 加锁完成之后的事情
     * @return boolean
     */
    public void doExec(String lockKey, Runnable runnable) {
        try {
            RLock rLock = redissonClient.getLock(lockKey);
            if (!rLock.tryLock(5L, TimeUnit.SECONDS)) {
                log.info(String.format("%s-->get lock Failed", lockKey));
                return;
            }
            try {
                log.info(String.format("%s-->get lock Success", lockKey));
                runnable.run();
            } catch (Exception e) {
                log.error(String.format("%s-->exec error", lockKey), e);
            } finally {
                rLock.unlock();
            }
        } catch (Exception ex) {
            log.error("lockExec doExec is error", ex);
        }
    }

    /**
     * 获取锁
     *
     * @param lockKey 锁key
     * @return RLock
     */
    public RLock getLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }

    /**
     * 关闭redis
     */
    public void shutdown() {
        redissonClient.shutdown();
    }

    /**
     * redis发布
     *
     * @param tuneEvent 事件
     * @param <T>       RedisPubSubEvent
     * @return RedisPubSubEvent
     */
    public <T extends RedisPubSubEvent> long publish(T tuneEvent) {
        RTopic topic = redissonClient.getTopic(tuneEvent.getClass().getName());
        return topic.publish(tuneEvent);
    }

    /**
     * 订阅
     *
     * @param consumer Consumer
     * @param clazz    对象
     * @param <T>      RedisPubSubEvent
     */
    public <T extends RedisPubSubEvent> void subscribe(Consumer<T> consumer, Class<T> clazz) {
        RTopic topic = redissonClient.getTopic(clazz.getName());
        if (topic == null) {
            return;
        }
        topic.addListener(clazz, (charSequence, oomContext) -> {
            consumer.accept(oomContext);
        });
    }
}