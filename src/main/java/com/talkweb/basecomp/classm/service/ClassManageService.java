package com.talkweb.basecomp.classm.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.Constants;
import com.talkweb.basecomp.classm.dao.ClassManageDao;
import com.talkweb.basecomp.common.util.DateUtil;
import com.talkweb.basecomp.common.util.HttpClientUtil;
import com.talkweb.basecomp.common.util.JSONUtil;

/**
 * @Description 班级管理服务层接口实现类
 * @author xixi1979
 * @date 2018/7/15
 */
@Service
public class ClassManageService {
    private static final Logger log = LoggerFactory.getLogger(ClassManageService.class);
	
	@Autowired
	private ClassManageDao classManageDao;
	
	/** -----更新座位初始化信息----- **/

	public JSONObject updateInitSeat(JSONObject param) {
		log.debug("updateInitSeat，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_CLASSID))
				|| StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "班级代码和学校代码信息不全，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(FIELD_X))
				|| StringUtils.isEmpty(param.get(FIELD_Y))) {
			return JSONUtil.getResponse(param, "初始化坐标信息不全，处理失败！", log);
		}
		int newX = param.getIntValue(FIELD_X);
		int newY = param.getIntValue(FIELD_Y);
		if (newX <= 0 || newY <= 0)
			return JSONUtil.getResponse(param, "初始化坐标超出范围，处理失败！", log);

		int code = 0;
		JSONObject data = classManageDao.getInitSeat(param);
		if (data == null) {//初始化处理
			String uuid = UUID.randomUUID().toString();
			param.put(FIELD_ID, uuid);
			code = classManageDao.insertInitSeat(param);
		} else {
			code = classManageDao.updateInitSeat(param);
		}
		return JSONUtil.getResponse(code);
	}

	/** -----获取初始化座位信息----- **/

	public JSONObject insertOrGetInitSeat(JSONObject param) {
		log.debug("insertOrGetInitSeat，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_CLASSID))
				|| StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "班级代码和学校代码信息不全，处理失败！", log);
		}
		JSONObject data = classManageDao.getInitSeat(param);
		if (data == null) {//初始化处理
			String uuid = UUID.randomUUID().toString();
			param.put(FIELD_X, INIT_X);
			param.put(FIELD_Y, INIT_Y);
			param.put(FIELD_ID, uuid);
			int code = classManageDao.insertInitSeat(param);
			if (code == 0)
				return JSONUtil.getResponse(param, "创建座位初始化数据失败！", log);
			
			data = classManageDao.getInitSeat(param);
			data.put(FIELD_ISCREATE, "1");
		} else {
			data.put(FIELD_ISCREATE, "0");
		}
		return JSONUtil.getResponse(data);
	}

	/** -----更新学生座位信息----- **/

	public synchronized JSONObject updateSeat(JSONObject param) {
		log.debug("updateSeat，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_CLASSID))
				|| StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "班级代码和学校代码信息不全，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ACCOUNTID))) {
			return JSONUtil.getResponse(param, "学生账号信息不全，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(FIELD_X))
				|| StringUtils.isEmpty(param.get(FIELD_Y))) {
			return JSONUtil.getResponse(param, "学生座位坐标信息不全，处理失败！", log);
		}
		int newX = param.getIntValue(FIELD_X);
		int newY = param.getIntValue(FIELD_Y);
		if (newX <= 0 || newY <= 0)
			return JSONUtil.getResponse(param, "学生座位坐标超出初始化设置，处理失败！", log);

		int x = INIT_X, y = INIT_Y;
		JSONObject c = new JSONObject();
		c.put(Constants.FIELD_CLASSID, param.get(Constants.FIELD_CLASSID));
		JSONObject init = getInitSeat(c);
		if (init != null) {
			x = init.getIntValue(FIELD_X);
			y = init.getIntValue(FIELD_Y);
		} 

		if (newX > x || newY > y)
			return JSONUtil.getResponse(param, "学生座位坐标超出初始化设置，处理失败！", log);
		
		List<JSONObject> l = classManageDao.getSeat(param);//当前学生座位信息
		JSONObject a = null, b = null;
		if (l != null && l.size() > 0)
			a = l.get(0);
		
		l = classManageDao.getSeat2(param);//所选的座位所在学生信息
		if (l != null && l.size() > 0)
			b = l.get(0);
		
		int code = 0;
		if (a == null) {//当前学生未指定座位
			String uuid = UUID.randomUUID().toString();
			param.put(FIELD_ID, uuid);
			code = classManageDao.insertSeat(param);
			if (code == 0) 
				return JSONUtil.getResponse(param, "创建学生座位信息失败！", log);
			if (b != null) {//删除所选座位原学生座位信息
				code = classManageDao.deleteSeat(b);
				if (code == 0) {
					throw new RuntimeException("删除学生"
				+b.getString(Constants.FIELD_ACCOUNTID)+"座位信息失败。");
				}
			}
			return JSONUtil.getResponse(code);
		} else {//当前学生有指定座位
			code = classManageDao.updateSeat(param);
			if (code == 0) 
				return JSONUtil.getResponse(param, "修改学生座位信息失败！", log);
			if (b != null) {
				if (a.getIntValue(FIELD_X) > x || a.getIntValue(FIELD_Y) > y) {//指定的座位超出初始化范围
					code = classManageDao.deleteSeat(b);
				} else {//修改所选座位所在学生的座位信息为当前学生原座位信息
					b.put(FIELD_X, a.get(FIELD_X));
					b.put(FIELD_Y, a.get(FIELD_Y));
					code = classManageDao.updateSeat(b);
				}
				if (code == 0) {
					throw new RuntimeException("修改学生"
				+b.getString(Constants.FIELD_ACCOUNTID)+"座位信息失败。");
				}
			}
			return JSONUtil.getResponse(code);
		}
	}

	/** -----获取所有学生座位信息----- **/

	public JSONObject getSeatList(JSONObject param) {
		log.debug("getSeatList，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_TERMINFO))) {
			return JSONUtil.getResponse(param, "学年学期信息为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_CLASSID))) {
			return JSONUtil.getResponse(param, "班级代码不全，处理失败！", log);
		}
		try {
			List<JSONObject> seats = classManageDao.getSeatList(param);
			List<JSONObject> students = classManageDao.getStudentList(param);
			List<JSONObject> remain = new ArrayList<>();
			JSONObject inits = this.getInitSeat(param);
			checkSeatData(inits, seats, students, remain);

			JSONObject atnd = handleAtndData(seats, param);
			JSONObject ret = JSONUtil.getResponse(seats);
			ret.put(FIELD_REMAINLIST, remain);
			ret.put(FIELD_INITSEAT, inits);
			if (atnd != null)
				ret.put(FIELD_ATNDCOUNTNUM, atnd);
			return ret;
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "获取学生座位信息异常。", ex, log);
		}
	}
	

	public JSONObject getSeatListForApp(JSONObject param) {
		log.debug("getSeatListForApp，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_CLASSID))) {
			return JSONUtil.getResponse(param, "班级代码不全，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "学校代码信息不全，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_TERMINFO))) {
			return JSONUtil.getResponse(param, "学年学期信息为空，处理失败！", log);
		}
		try {
			param.put(FIELD_NEEDPERFORMANCE, 1);
			List<JSONObject> seats = classManageDao.getSeatList(param);
			List<JSONObject> students = classManageDao.getStudentList(param);
			List<JSONObject> remain = new ArrayList<>();
			JSONObject inits = this.getInitSeat(param);
			checkSeatData(inits, seats, students, remain);

			if (remain.isEmpty()) {
				students = seats;
			}
			JSONObject atnd = handleAtndData(students, param);
			JSONObject ret = JSONUtil.getResponse(students);
			if (remain.isEmpty()) {
				ret.put(FIELD_INITSEAT, inits);
			}
			if (atnd != null)
				ret.put(FIELD_ATNDCOUNTNUM, atnd);
			return ret;
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "获取学生座位信息异常。", ex, log);
		}
	}

	public JSONObject getStudentList(JSONObject param) {
		log.debug("getStudentList，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_CLASSID))) {
			return JSONUtil.getResponse(param, "班级代码不全，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_TERMINFO))) {
			return JSONUtil.getResponse(param, "学年学期信息为空，处理失败！", log);
		}
		try {
			List<JSONObject> data = classManageDao.getStudentList(param);
			return JSONUtil.getResponse(data);
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "获取学生列表异常。", ex, log);
		}
	}


	public JSONObject getPerformance(JSONObject param) {
		log.debug("getPerformance，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ACCOUNTID))) {
			return JSONUtil.getResponse(param, "班级代码不全，处理失败！", log);
		}
		try {
			List<Integer> data = classManageDao.getPerformance(param);
			JSONArray arr = new JSONArray();
			for (Integer obj : data) {
				if (obj != null)
					arr.add(obj);
			}
	        SimpleDateFormat f = new SimpleDateFormat("MM月dd日");
			JSONObject ret = JSONUtil.getResponse();
			ret.put(FIELD_PERFORMANCE, arr);
			ret.put(FIELD_DATE, f.format(new Date()));
			ret.put(FIELD_WEEK, DateUtil.nowToWeek());
			return ret;
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "获取学生在校表现异常。", ex, log);
		}
	}


	public JSONObject updatePerformance(JSONObject param) {
		log.debug("updatePerformance，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ACCOUNTID))) {
			return JSONUtil.getResponse(param, "学生账号代码不全，处理失败！", log);
		}
		classManageDao.deletePerformance(param);
		if (!StringUtils.isEmpty(param.get(FIELD_PERFORMANCE))) {
			if (StringUtils.isEmpty(param.get(Constants.FIELD_CREATORID)))
				param.put(Constants.FIELD_CREATORID, 0);
			JSONArray arr = param.getJSONArray(FIELD_PERFORMANCE);
			if (arr != null && arr.size() > 0)
				classManageDao.insertPerformance(param);
		}
		return JSONUtil.getResponse();
	}
	
	private JSONObject handleAtndData(List<JSONObject> data, JSONObject param) {
		if (data.isEmpty()) {
			return null;
		}
		
		if (StringUtils.isEmpty(param.get("extClassId"))) {
			log.info("获取考勤数据失败, 未传入extClassId.");
			return null;
		}
		
		try {
			String url = atndUrl + "?classId=" + param.get("extClassId") + "&queryType=day";
			JSONObject ret = HttpClientUtil.postRequest(url, null);
			if (ret == null || ret.getIntValue("code") != 1) {
				return null;
			}
			
			if (ret.containsKey("lateList")) {//迟到
				JSONArray arr = ret.getJSONArray("lateList");
				handleAtndDataInternal(data, arr, 1);
			}
	
			if (ret.containsKey("leaveEarlyList")) {//早退
				JSONArray arr = ret.getJSONArray("leaveEarlyList");
				handleAtndDataInternal(data, arr, 2);
			}
	
			if (ret.containsKey("absenteeismList")) {//缺勤
				JSONArray arr = ret.getJSONArray("absenteeismList");
				handleAtndDataInternal(data, arr, 3);
			}
			
			return ret.getJSONObject(FIELD_ATNDCOUNTNUM);
		} catch(Exception ex) {
			log.error("获取考勤数据失败.", ex);
			return null;
		}
	}

    @Value("${classmanage.atndUrl}")
    private String atndUrl;

	private void handleAtndDataInternal(List<JSONObject> data, JSONArray arr, int type) {
		if (arr == null || arr.isEmpty()) return;
		
		for (JSONObject obj: data) {
			String extId = obj.getString("extId");
			if (StringUtils.isEmpty(extId)) {
				continue;
			}
			for (int i = 0; i < arr.size(); i++) {
				JSONObject tmp = arr.getJSONObject(i);
				if (tmp != null && extId.equals(tmp.getString("zhxyuserid"))) {
					obj.put("atndType", type);
				}
			}
		}
	}

	private JSONObject getInitSeat(JSONObject param) {
		JSONObject inits = classManageDao.getInitSeat(param);
		int x = INIT_X, y = INIT_Y;
		if (inits == null) {//初始化处理
			inits = new JSONObject();
			inits.put(FIELD_X, x);
			inits.put(FIELD_Y, y);
		} else {
			if (inits.getIntValue(FIELD_X) <= 0)
				inits.put(FIELD_X, x);
			if (inits.getIntValue(FIELD_Y) <= 0)
				inits.put(FIELD_Y, y);
		}
		return inits;
	}
	
	private void checkSeatData(JSONObject inits, List<JSONObject> seats, List<JSONObject> students, List<JSONObject> remain) {
		if (seats.isEmpty()) {
			remain.addAll(students);
			return;
		}

		int x = inits.getIntValue(FIELD_X);
		int y = inits.getIntValue(FIELD_Y);
		
		List<Long> ids = new ArrayList<>();
		for (JSONObject obj : students) {
			Long id = obj.getLong(Constants.FIELD_ACCOUNTID);
			if (!ids.contains(id)) {
				ids.add(id);
			}
		}
		List<Long> handled = new ArrayList<>();
		List<JSONObject> removed = new ArrayList<>();
		for (JSONObject obj: seats) {
			Long id = obj.getLong(Constants.FIELD_ACCOUNTID);
			if (obj.getIntValue(FIELD_X) > x || obj.getIntValue(FIELD_Y) > y) {
				removed.add(obj);
			} else {
				if (!ids.contains(id)) {
					removed.add(obj);
				} else {
					handled.add(id);
				}
			}
		}
		seats.removeAll(removed);
		if (handled.size() < students.size()) {
			for (JSONObject obj : students) {
				Long id = obj.getLong(Constants.FIELD_ACCOUNTID);
				if (!handled.contains(id)) {
					remain.add(obj);
				}
			}
		}
	}
	
	private static final int INIT_X = 8;//教研组
	private static final int INIT_Y = 8;//年级组
	
	private final static String FIELD_PERFORMANCE = "performance";
	private final static String FIELD_DATE = "date";
	private final static String FIELD_WEEK = "week";
	private final static String FIELD_X = "x";
	private final static String FIELD_Y = "y";
	private final static String FIELD_ID = "id";
	private final static String FIELD_ISCREATE = "iscreate";
	private final static String FIELD_INITSEAT = "initSeat";
	private final static String FIELD_ATNDCOUNTNUM = "atndCountNum";
	private final static String FIELD_NEEDPERFORMANCE = "needPerformance";
	//private final static String FIELD_SEATLIST = "seatList";
	private final static String FIELD_REMAINLIST = "remainList";
}