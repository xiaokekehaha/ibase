package com.wzk.mvc.support.jedis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import com.wzk.mvc.util.PropertiesUtil;

/**
 * 
 * @author zk
 *
 */
public class JedisTemplate {
    private static Logger logger = LoggerFactory.getLogger(JedisTemplate.class);

    private static ShardedJedisPool shardedJedisPool = null;

    private static Integer EXPIRE = Integer.parseInt(PropertiesUtil.get("redis.expiration"));

    // 获取线程
    private static ShardedJedis getJedis() {
        if (shardedJedisPool == null) {
            synchronized (EXPIRE) {
                if (shardedJedisPool == null) {
                    WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
                    shardedJedisPool = wac.getBean(ShardedJedisPool.class);
                }
            }
        }
        return shardedJedisPool.getResource();
    }

    public static <K> K run(String key, Executor<K> executor, Integer... expire) {
        ShardedJedis jedis = getJedis();
        if (jedis == null) {
            return null;
        }
        try {
            K result = executor.execute(jedis);
            if (jedis.exists(key)) {
                if (expire == null || expire.length == 0) {
                    jedis.expire(key, EXPIRE);
                } else if (expire.length == 1) {
                    jedis.expire(key, expire[0]);
                }
            }
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public static <K> K run(byte[] key, Executor<K> executor, Integer... expire) {
        ShardedJedis jedis = getJedis();
        if (jedis == null) {
            return null;
        }
        try {
            K result = executor.execute(jedis);
            if (jedis.exists(key)) {
                if (expire == null || expire.length == 0) {
                    jedis.expire(key, EXPIRE);
                } else if (expire.length == 1) {
                    jedis.expire(key, expire[0]);
                }
            }
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }
}
