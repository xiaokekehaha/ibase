package com.wzk.mvc.support.jedis;

import redis.clients.jedis.ShardedJedis;

/**
 * 
 * @author zk
 *
 * @param <K>
 */
public interface Executor<K> {
	public K execute(ShardedJedis jedis);
}
