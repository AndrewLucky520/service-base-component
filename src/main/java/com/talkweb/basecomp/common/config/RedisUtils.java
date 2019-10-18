package com.talkweb.basecomp.common.config;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtils {

	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;
	
	public Object get(Object key) {
		return redisTemplate.opsForValue().get(key);
	}

	public Object getAndSet(Object key, Object value) {
		return redisTemplate.opsForValue().getAndSet(key, value);
	}

	public void set(Object key, Object Value,long expireTime) {
		redisTemplate.opsForValue().set(key, Value, expireTime, TimeUnit.SECONDS);
	}
	
	public void set(Object key, Object Value) {
		redisTemplate.opsForValue().set(key, Value, 86400, TimeUnit.SECONDS);
	}

	public Boolean setNX(Object key, Object value) {
		return redisTemplate.opsForValue().setIfAbsent(key, value);
	}
		
	public void del(Object key) {
		redisTemplate.delete(key);
	}

	private static String LCOK_KEY = "lock:"; 
	public boolean lock(String appKey, long timeout, int expiredTime) {
		String key = LCOK_KEY + appKey;
        long nano = System.nanoTime();
        timeout *= 1000 * 1000;
        try {
            while ((System.nanoTime() - nano) < timeout) {
                Long expiresStr = System.currentTimeMillis() + expiredTime;
                if (this.setNX(key, expiresStr)) {
                    return true;
                }
                Object current = this.get(key);
                if (current != null && ((Long) current) < System.currentTimeMillis()) {
                	Object old = this.getAndSet(key, expiresStr);
                    if (old != null && old.equals(current)) {
                        // 考虑多线程并发的情况，只有一个线程的设置值和当前值相同，它才有权利加锁
                        return true;
                    }
                }
                Thread.sleep(10, RandomUtils.nextInt(500));
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return false;
    }

	public boolean unLock(String appKey) {
		String key = LCOK_KEY + appKey;
        try {
        	this.del(key);
        } catch (Exception e) {
        	//log.error("释放锁异常！", e);
        }
        return false;
    }
	
}
