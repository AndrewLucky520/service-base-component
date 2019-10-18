package com.talkweb.basecomp.common.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单内存缓存管理器
 * 可设置过期时间，单位毫秒
 * 使用Timer定时清理过期数据，每分钟清理一次，可修改清理周期
 */
@SuppressWarnings({"rawtypes","unchecked"}) 
public class CacheManager {
 
	private static Map<String, CacheData> cache = new ConcurrentHashMap<>();
	
	/**
	 * 启动定时任务清理过期缓存，避免内存溢出
	 */
	static {
		Timer timer = new Timer();
		timer.schedule(new TimeoutTimerTask(cache), 300 * 1000);//5分钟检测一次
	}
	
	/**
	 * 设置缓存，不过期
	 * @param key
	 * @param t
	 */
	public static void set(String key, String t) {
		cache.put(key, new CacheData<>(t, 0));
	}
	
	/**
	 * 设置缓存，指定过期时间expire(单位毫秒)
	 * @param key
	 * @param t
	 * @param expire 过期时间
	 */
	public static void set(String key, Object t, long expire) {
		CacheData<Object> data = cache.get(key);
		if (data != null) {
			cache.remove(key);
		}
		cache.put(key, new CacheData<>(t, expire));
	}
	
	/**
	 * 根据key获取指定缓存
	 * @param key
	 * @return
	 */
	public static Object get(String key) {
		CacheData<Object> data = cache.get(key);
		if (null == data) {
			return null;
		}
		if (data.isExpire()) {
			remove(key);
			return null;
		}
		return data.getData();
	}
	
	/**
	 * 移除指定key缓存
	 * @param key
	 */
	public static void remove(String key) {
		cache.remove(key);
	}
	
	/**
	 * 移除所有缓存
	 */
	public static void removeAll() {
		cache.clear();
	}
	
	private static class CacheData<T> {
		// 缓存数据
		private T data;
		// 过期时间(单位，秒)
		private long expireTime;
		
		public CacheData(T t, long expire) {
			this.data = t;
			if(expire <= 0) {
				this.expireTime = 0L;
			} else {
				this.expireTime = Calendar.getInstance().getTimeInMillis() + expire * 1000;
			}
		}
		
		/**
		 * 判断缓存数据是否过期
		 * @return true表示过期，false表示未过期
		 */
		public boolean isExpire() {
			if (expireTime <= 0) {
				return false;
			}
			if (expireTime > Calendar.getInstance().getTimeInMillis()) {
				return false;
			}
			return true;
		}
		
		public T getData() {
			return data;
		}
	}
	
	static class TimeoutTimerTask extends TimerTask { 
		Map<String, CacheData> cache;  
          
        public TimeoutTimerTask(Map<String, CacheData> cache){  
            this.cache = cache;  
        }  
  
        @Override  
        public void run() {
        	List<String> keys = new ArrayList<>(cache.keySet());
        	for (String key: keys) {
        		CacheData obj = this.cache.get(key);
        		if (obj != null && obj.isExpire()) {
        			cache.remove(key);
        			System.out.println("清除缓存=============" + key);
        		}
        	}
        }  
    }  
}