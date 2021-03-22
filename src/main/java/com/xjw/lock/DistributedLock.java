package com.xjw.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 获取分布式锁（用于单实例redis）
 */
@Component
public class DistributedLock {
	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;

	// 成功
	private final Long SUCCESS = 1L;

	/**
	 * 锁定key的lua语句
	 */
	private final String lockScript = "if redis.call('setNx',KEYS[1],ARGV[1]) then if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end end";

	/**
	 * 解锁key的lua语句
	 */
	private final String unlockScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

	/**
	 * 获取分布式锁
	 *
	 * @param lockKey    锁的key
	 * @param value      锁的key对应的值（***该值在集群环境设置的值务必错开）
	 * @param expireTime 过期时间（单位：秒）
	 * @return
	 */
	public boolean getLock(String lockKey, String value, int expireTime) {
		boolean ret = false;
		RedisScript<String> redisScript = new DefaultRedisScript<>(lockScript, String.class);
		Object result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), value, expireTime);
		if (SUCCESS.equals(result)) {
			return true;
		}
		return ret;
	}

	/**
	 * 释放分布式锁
	 * 
	 * @param lockKey 需要释放的锁
	 * @param value
	 * @return
	 */
	public boolean unLock(String lockKey, String value) {
		RedisScript<String> redisScript = new DefaultRedisScript<>(unlockScript, String.class);
		Object result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), value);
		if (SUCCESS.equals(result)) {
			return true;
		}
		return false;
	}
}
