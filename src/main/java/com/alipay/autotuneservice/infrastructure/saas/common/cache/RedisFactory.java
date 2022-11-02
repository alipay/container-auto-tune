package com.alipay.autotuneservice.infrastructure.saas.common.cache;

import com.alipay.autotuneservice.infrastructure.saas.common.util.SpringContextUtil;
import com.alipay.autotuneservice.infrastructure.saas.common.util.SpringPropertiesCache;
import io.netty.channel.nio.NioEventLoopGroup;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 近端包中使用redis的方法
 * @author yiqi
 * @date 2022/07/06
 */
@Deprecated
public class RedisFactory {

    private static final String HOST;

    private static final Integer PORT;

    private static final String PASSWORD;

    private static final boolean SSL;

    private static final String CONFIG;

    private static final String REDIS_PROTOCOL_PREFIX  = "redis://";
    private static final String REDISS_PROTOCOL_PREFIX = "rediss://";

    private static final int    CONNECTION_POOL_SIZE = 64;

    private static final Map<String, Object> redisClientMap = new ConcurrentHashMap<>();

    static {
        String include = SpringContextUtil.getSdkEnv();
        // 加载include对应的文件内容，读取相应的配置
        Map<String, Object> data = SpringPropertiesCache.get(include);
        Map<String, Object> spring = (Map<String, Object>) data.get("spring");
        Map<String, Object> redis = (Map<String, Object>) spring.get("redis");
        // 根据环境获取配置
        HOST = redis.get("host").toString();
        PORT = Integer.parseInt(redis.get("port").toString());
        PASSWORD = redis.get("password").toString();
        SSL = Objects.nonNull(redis.get("ssl")) ? Boolean.getBoolean(redis.get("ssl").toString()): Boolean.FALSE;
        Object redisson = redis.get("redisson");
        if (redisson != null) {
            CONFIG = ((Map<String, String>) redisson).get("config");
        } else {
            CONFIG = null;
        }
    }

    /**
     * 获取封装的redisClient
     * @return
     */
    public static RedisClient redisClient() {
        return null;
        //return getCache(() -> new RedisClient(redissonClient()), "redisClient");
    }

    /**
     * 原生的redissonClient
     * @return
     */
    public static RedissonClient redissonClient() {
        return getCache(() -> {
            try {
                return localRedisClient(HOST, PORT, PASSWORD, CONFIG, SSL);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, "redissonClient");
    }

    /**
     * redisson配置类加载
     * @param host host
     * @param port port
     * @param password password
     * @param configStr 配置字符串
     * @return RedissonClient
     * @throws Exception
     */
    public static RedissonClient localRedisClient(String host, int port, String password, String configStr, boolean isSsl) throws Exception {
        Config config;
        int timeout = 100000;

        if (configStr != null) {
            try {
                config = Config.fromYAML(configStr);
            } catch (Exception e) {
                throw new IllegalArgumentException("Can't parse config", e);
            }
        } else {
            config = new Config();
            String prefix = REDIS_PROTOCOL_PREFIX;
            if (isSsl) {
                prefix = REDISS_PROTOCOL_PREFIX;
            }
            config.useSingleServer()
                    .setConnectionPoolSize(CONNECTION_POOL_SIZE)
                    .setAddress(prefix + host + ":" + port)
                    .setConnectTimeout(timeout)
                    .setDatabase(0)
                    .setPassword(password);
        }
        config.setEventLoopGroup(new NioEventLoopGroup());
        return Redisson.create(config);
    }

    /**
     * 缓存redis客户端
     * @param function 目标代码
     * @param cacheKey key
     * @return T,范型
     * @param <T> 范型
     */
    private static <T> T getCache(Supplier<T> function, String cacheKey) {
        String key = String.format("%s_%s_%s", HOST, PORT, cacheKey);
        if (redisClientMap.containsKey(key)) {
            return (T) redisClientMap.get(key);
        }
        T t = function.get();
        redisClientMap.put(key, t);
        return t;
    }
}
