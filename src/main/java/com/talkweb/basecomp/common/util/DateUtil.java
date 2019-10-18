package com.talkweb.basecomp.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * 日期处理工具类
 * 
 * @author yc
 *
 */
public class DateUtil {
	public static final String YMD = "yyyyMMdd";
	public static final String YMD_YEAR = "yyyy";
	public static final String YMD_BREAK = "yyyy-MM-dd";
	public static final String YMDHMS = "yyyyMMddHHmmss";
	public static final String YMDHMS_BREAK = "yyyy-MM-dd HH:mm:ss";
	public static final String YMDHMS_BREAK_HALF = "yyyy-MM-dd HH:mm";

	public static final int CAL_SECOND = 1000;
	public static final int CAL_MINUTES = 1000 *60;
	public static final int CAL_HOURS = 1000 * 60 * 60;
	public static final long CAL_DAYS = 1000 * 60 * 60 * 24L;
	public static final long CAL_MONTH = 1000 * 60 * 60 * 24L * 30;
	public static final long CAL_YEAR = 1000 * 60 * 60 * 24L * 30 * 12;

	public static String getDateText(Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	public static int calDiffs(Date startDate, Date endDate, long calType) {
		Long start = startDate.getTime();
		Long end = endDate.getTime();
		int diff = (int) ((end - start) / calType);
		return diff;
	}

	public static String timeDiffText(Date startDate, Date endDate) {
		int calDiffs = DateUtil.calDiffs(startDate, endDate, DateUtil.CAL_SECOND);
		
		if(calDiffs < 0){
			return "刚刚";
		}
		if (calDiffs >= 0 && calDiffs < 60) {
			return "刚刚";
		}
		calDiffs = DateUtil.calDiffs(startDate, endDate, DateUtil.CAL_MINUTES);
		if (calDiffs >=0 && calDiffs < 60) {
			return calDiffs + "分钟前";
		}
		calDiffs = DateUtil.calDiffs(startDate, endDate, DateUtil.CAL_HOURS);
		if (calDiffs >= 1 && calDiffs < 24) {
			return calDiffs + "小时前";
		}
		calDiffs = DateUtil.calDiffs(startDate, endDate, DateUtil.CAL_DAYS);
		if (calDiffs >=1 && calDiffs < 30) {
			return calDiffs + "天前";
		}
		calDiffs = DateUtil.calDiffs(startDate, endDate, DateUtil.CAL_MONTH);
		if (calDiffs >=1 && calDiffs < 12) {
			return calDiffs + "月前";
		}
		calDiffs = DateUtil.calDiffs(startDate, endDate, DateUtil.CAL_YEAR);
		if (calDiffs >=1) {
			return calDiffs + "年前";
		}
		return DateUtil.getDateText(startDate, DateUtil.YMDHMS_BREAK_HALF);
	}

	public static String showTimeText(Date date) {
		return DateUtil.timeDiffText(date, new Date());
	}

	public static String nowToWeek() {
        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        cal.setTime(new Date());
        int w = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

	public static void initTermInfo(JSONObject param) {
		if (StringUtils.isEmpty(param.get(FIELD_TERMINFO))) {
	        Calendar cal = Calendar.getInstance(); // 获得一个日历
	        int y = cal.get(Calendar.YEAR);
	        int m = cal.get(Calendar.MONTH);
	        if (m < 9)
	            y = y - 1;
			param.put(FIELD_TERMINFO, 2 < m && m < 9 ? y + "2" : y + "1");
			//param.put(FIELD_TERMINFOYEAR, y);
		}
    }
	
	/**
	 * 判断日期是否小于当前日期
	* @Title: beforeNowTime 
	* @Description: TODO
	* @param @param dateVal
	* @param @return
	* @return int
	* @throws 
	 */
	public static boolean beforeNowTime(String dateVal) {
		SimpleDateFormat f = new SimpleDateFormat(YMD_BREAK);
		
		try {
			if(f.parse(f.format(dateVal)).before(f.parse(f.format(new Date()))) ){
				return true;
			}
		} catch (ParseException e) {
			return false;
		}
		return false;
	}
	
	/**
	 * 判断日期是否大于当前日期
	* @Title: compareDate 
	* @Description: TODO
	* @param @param dateVal
	* @param @return
	* @return int
	* @throws 
	 */
	public static boolean afterNowTime(String dateVal) {
		SimpleDateFormat f = new SimpleDateFormat(YMD_BREAK);
		
		try {
			if(f.parse(f.format(dateVal)).before(f.parse(f.format(new Date()))) ){
				return true;
			}
		} catch (ParseException e) {
			return false;
		}
		return false;
	}
	
	private final static String FIELD_TERMINFO = "termInfo";
}