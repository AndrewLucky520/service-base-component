/**
 * 
 */
package com.talkweb.basecomp.common.data;

/**
 * @ClassName: CacheExpireTime
 * @version:1.0
 * @Description: 
 * @author 廖刚 ---智慧校
 * @date 2015年6月17日
 */
public enum ExpireTime {
	minExpireTime(900, "最小缓存时间:15分钟"),
	oneHourExpireTime(3600, "1小时");
	
	/**
	 * @param timeValue
	 * @param desc
	 */
	private ExpireTime(long timeValue, String desc) {
		this.timeValue = timeValue;
		this.desc = desc;
	}
	
	private long timeValue;	//缓存时间
	private String desc;	//缓存枚举描述
	
	
	
	/**
	 * @return the timeValue
	 */
	public long getTimeValue() {
		return timeValue;
	}
	/**
	 * @param timeValue the timeValue to set
	 */
	public void setTimeValue(Long timeValue) {
		this.timeValue = timeValue;
	}
	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}
	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}
}
