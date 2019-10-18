package com.talkweb.basecomp.point.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.common.util.JSONUtil;
import com.talkweb.basecomp.point.dao.PointDao;


@Service
public class PointService {
    private static final Logger log = LoggerFactory.getLogger(PointService.class);

	@Autowired
	private PointDao pointDao;
	
	public JSONObject getPointData(JSONObject param) {
		log.debug("getPointData，参数：{}", param);
		if (StringUtils.isEmpty(param.get(FIELD_ACCOUNTID))) {
			return JSONUtil.getResponse(param, "用户账号代码为空，处理失败！", log);
		}

		int total = getAccountPointInternal(param);
		param.put(FIELD_TYPE, TYPE_DAILY_CHECKIN);
		int check = getPointInternal(param);
		
		JSONObject ret = JSONUtil.getResponse();
		ret.put(FIELD_TOTAL, total);
		ret.put(FIELD_CHECK, check > 0 ? 1 : 0);
		ret.put(FIELD_GRADE, getGrade(total));
		return ret;
	}

	public JSONObject getPointDetail(JSONObject param) {
		log.debug("getPointDetail，参数：{}", param);
		if (StringUtils.isEmpty(param.get(FIELD_ACCOUNTID))) {
			return JSONUtil.getResponse(param, "用户账号代码为空，处理失败！", log);
		}

		List<JSONObject> data = getPointDetailListInternal(param);
		int total = getAccountPointInternal(param);
		int grade = getGrade(total);
		int max = grades.get(grade);
		int val = (int) (((float)total)/max * 100);
		if (val > 100)
			val = 100;
		JSONObject ret = JSONUtil.getResponse(data);
		ret.put(FIELD_TOTAL, total);
		ret.put(FIELD_GRADE, getGrade(total));
		ret.put(FIELD_PROGRESS, val);
		ret.put(FIELD_NEEDPOINT, max - total);
		return ret;
	}

	public JSONObject addPoint(JSONObject param) {
		int type = param.getIntValue(FIELD_TYPE);
		switch (type) {
		case TYPE_ACCESSS_APP:
			return this.createPoint(param, TYPE_ACCESSS_APP, false);
		case TYPE_CREATE_CIRCLE:
			return this.createPoint(param, TYPE_CREATE_CIRCLE, false);
		case TYPE_ACCESS_WEEKLY:
			return this.createPoint(param, TYPE_ACCESS_WEEKLY, true);
		case TYPE_PRAISE:
			return this.praise(param);
		case TYPE_COMMENT:
			if (param.getLongValue(FIELD_ACCOUNTID) == param.getLongValue(FIELD_CREATORID)) {
				return JSONUtil.getResponse(param, "本人评论不积分.", log);
			}
			return this.createPoint(param, TYPE_COMMENT, true);
		}
		return JSONUtil.getResponse(param, "不支持积分.", log);
	}
	
	public JSONObject createDailyCheckIn(JSONObject param) {
		param.put(FIELD_BIZID, "dd0001");
		return this.createPoint(param, TYPE_DAILY_CHECKIN, false);
	}
	
	public JSONObject createAccessApp(JSONObject param){
		return this.createPoint(param, TYPE_ACCESSS_APP, false);
	}

	public int getPointInternal(JSONObject param) {
		param.put(FIELD_YEAR, "_" + getYear());
		Integer val = this.pointDao.getPoint(param);
		return val == null ? 0 : val;
	}

	public int getAccountPointInternal(JSONObject param) {
		Integer val = this.pointDao.getAccountPoint(param);
		return val == null ? 0 : val;
	}

	public int createPointInternal(JSONObject param) {
		param.put(FIELD_YEAR, "_" + getYear());
		return this.pointDao.createPoint(param);
	}

	protected static int getYear() {
		Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

	protected List<JSONObject> getPointDetailListInternal(JSONObject param){
		int pageNo = param.getIntValue(FIELD_PAGENO);
		int pageSize = param.getIntValue(FIELD_PAGESIZE);
		if (pageNo <= 0) pageNo = 1;
		if (pageSize <= 0) {
			pageSize = DEFAULT_MESSAGE_PAGESIZE;
		}
		List<JSONObject> ret = new ArrayList<>();
		int pageStart = (pageNo - 1) * pageSize;
		int curYear = Calendar.getInstance().get(Calendar.YEAR);
		while (true) {
			param.put(FIELD_YEAR, "_" + curYear);
			param.put(FIELD_PAGESTART, pageStart);
			param.put(FIELD_PAGESIZE, pageSize);
			int cc = this.pointDao.getPointDetailCount(param);
			if (pageStart < cc) {
				List<JSONObject> tmp = this.pointDao.getPointDetail(param);
				if (tmp.size() > 0) {
					ret.addAll(tmp);
					if (tmp.size() == pageSize) {
						break;
					}
					pageStart = 0;
					pageSize -= tmp.size();
				}
			} else {
				pageStart -= cc;
			}
			if (curYear <= 2018) {
				break;
			}
			curYear -= 1;
		}
		return ret;
	}
	
	protected JSONObject praise(JSONObject param) {
		if (param.getLongValue(FIELD_ACCOUNTID) == param.getLongValue(FIELD_CREATORID)) {
			return JSONUtil.getResponse(param, "本人点赞不积分.", log);
		}
		int code = this.createPointInternal(param, TYPE_PRAISE, true);
		if (code == ERROR_DUPLICATE_POINT) {
			return JSONUtil.getResponse(param, "重复积分.", log);
		}
		Object obj = param.get(FIELD_CREATORID);
		if (StringUtils.isEmpty(obj)) {
			return JSONUtil.getResponse(param, "发布人账号代码为空，处理失败！", log);
		}
		param.put(FIELD_ACCOUNTID, obj);
		synchronized (this) {
			return this.createPoint(param, TYPE_BE_PRAISED, false);
		}
	}
	
	JSONObject createPoint(JSONObject param, int type, boolean check) {
		int code = this.createPointInternal(param, type, check);
		if (code == ERROR_DUPLICATE_POINT) {
			return JSONUtil.getResponse(param, "重复积分。", log);
		}
		if (code == ERROR_UPPER_LIMIT) {
			return JSONUtil.getResponse(param, "超过积分上限。", log);
		}
		return JSONUtil.getResponse(code);
	}
	
	int createPointInternal(JSONObject param, int type, boolean check) {
		log.debug("createPoint，参数:{} type:{}", param, type);
		if (StringUtils.isEmpty(param.get(FIELD_ACCOUNTID))) {
			return 0;
		}

		if (check && !StringUtils.isEmpty(param.get(FIELD_BIZID))) {
			JSONObject tmp = new JSONObject();
			tmp.put(FIELD_ACCOUNTID, param.get(FIELD_ACCOUNTID));
			tmp.put(FIELD_BIZID, param.get(FIELD_BIZID));
			tmp.put(FIELD_TYPE, type);
			int val = getPointInternal(tmp);
			if (val > 0) {
				return ERROR_DUPLICATE_POINT;
			}
		}
		
		JSONObject tmp = new JSONObject();
		tmp.put(FIELD_ACCOUNTID, param.get(FIELD_ACCOUNTID));
		tmp.put(FIELD_TYPE, type);
		
		int point = getPointInternal(tmp);
		Integer lmt = limit.get(type);
		if (lmt != null && point >= lmt) {
			if (type == TYPE_DAILY_CHECKIN) {
				return ERROR_DUPLICATE_POINT;
			}
			return ERROR_UPPER_LIMIT;
		}
		Integer pp = points.get(type);
		param.put(FIELD_TYPE, type);
		param.put(FIELD_POINT, pp);
		int total = getAccountPointInternal(param);
		int code = createPointInternal(param);
		if (total == 0) {
			pointDao.createAccountPoint(param);
		} else {
			param.put(FIELD_POINT, pp + total);
			pointDao.updateAccountPoint(param);
		}
		return code;
	}

	private int getGrade(int total) {
		if (grades == null) {
			initGradeInfo();
		}
		if (total > maxPoint)
			total = maxPoint;
		for (int i = 1; i <= maxGrade; i++) {
			Integer pp = grades.get(i);
			if (total < pp) {
				return i;
			}
		}
		return 1;
	}

    @Value("${point.gradeConfig}")
    protected String gradeConfig;

	private final static int DEFAULT_MESSAGE_PAGESIZE = 10;
	
	//private final static String FIELD_SCHOOLID = "schoolId";
	private final static String FIELD_ACCOUNTID = "accountId";
	private final static String FIELD_CREATORID = "creatorId";
	private final static String FIELD_BIZID = "bizId";
	private final static String FIELD_TYPE = "type";
	private final static String FIELD_POINT = "point";
	private final static String FIELD_TOTAL = "total";
	private final static String FIELD_CHECK = "check";
	private final static String FIELD_GRADE = "grade";
	private final static String FIELD_PROGRESS = "progress";
	private final static String FIELD_NEEDPOINT = "needpoint";
	private final static String FIELD_PAGENO = "pageNo";
	private final static String FIELD_PAGESIZE = "pageSize";
	private final static String FIELD_PAGESTART = "pageStart";
	private final static String FIELD_YEAR = "year";

	private static final int ERROR_DUPLICATE_POINT = -1;
	private static final int ERROR_UPPER_LIMIT  = -2;
	
	private static final int TYPE_DAILY_CHECKIN = 1;//每日签到
	private static final int TYPE_ACCESSS_APP = 2;//访问应用
	private static final int TYPE_CREATE_CIRCLE = 3;//发布班级圈
	private static final int TYPE_BE_PRAISED = 4;//内容被点赞
	private static final int TYPE_PRAISE = 5;//点赞内容
	//private static final int TYPE_BE_COMMENT = 6;//内容被评论
	private static final int TYPE_COMMENT = 7;//评论内容
	private static final int TYPE_ACCESS_WEEKLY = 8;//浏览周刊
	
	private static final int LIMIT_DAILY_CHECKIN = 2;//每日签到
	private static final int LIMIT_ACCESSS_APP = 5;//访问应用
	private static final int LIMIT_CREATE_CIRCLE = 3;//发布班级圈
	private static final int LIMIT_BE_PRAISED = 10;//内容被点赞
	private static final int LIMIT_PRAISE = 10;//班级圈点赞
	//private static final int LIMIT_BE_COMMENT = 5;//内容被评论
	private static final int LIMIT_COMMENT = 5;//评论内容
	private static final int LIMIT_ACCESS_WEEKLY = 10;//浏览周刊
	
	private static final int POINT_DAILY_CHECKIN = 2;//每日签到
	private static final int POINT_ACCESSS_APP = 1;//访问应用
	private static final int POINT_CREATE_CIRCLE = 1;//发布班级圈
	private static final int POINT_BE_PRAISED = 1;//内容被点赞
	private static final int POINT_PRAISE = 1;//班级圈点赞
	//private static final int POINT_BE_COMMENT = 1;//内容被评论
	private static final int POINT_COMMENT = 1;//评论内容
	private static final int POINT_ACCESS_WEEKLY = 2;//浏览周刊

	private static HashMap<Integer, Integer> limit = new HashMap<Integer, Integer>();
	private static HashMap<Integer, Integer> points = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> grades = null;
	private int maxGrade;
	private int maxPoint;
	
	private void initGradeInfo() {
		if (grades == null) {
			synchronized(this) {
				if (grades == null)
					initGradeInfoInternal();
			}
		}
	}

	private void initGradeInfoInternal() {
		if (StringUtils.isEmpty(gradeConfig)) {
			gradeConfig = "9,29,89,209,389,629,969,1409,1949,2689,3629,4769";
		}
		String[] strs = gradeConfig.split(",");
		Integer[] vals = new Integer[strs.length];
		int i = 0;
		for (String str : strs) {
			try {
				vals[i++] = Integer.parseInt(str);
			} catch(Exception ex) {
				log.error("积分等级配置错误。{}", gradeConfig, ex);
				gradeConfig = null;
				initGradeInfoInternal();
				return;
			}
		}
		for (i = 0; i < vals.length - 1; i++) {
			if (vals[i + 1] < vals[i] || vals[i] < 0) {
				log.error("积分等级配置错误。{}", gradeConfig);
				gradeConfig = null;
				initGradeInfoInternal();
				return;
			}
		}
		grades = new HashMap<Integer, Integer>();
		maxGrade = vals.length;
		maxPoint = vals[vals.length - 1];
		for (i = 0; i < vals.length; i++) {
			grades.put(i + 1, vals[i] + 1);
		}
	}
	
	static {
		limit.put(TYPE_DAILY_CHECKIN, LIMIT_DAILY_CHECKIN);
		limit.put(TYPE_ACCESSS_APP, LIMIT_ACCESSS_APP);
		limit.put(TYPE_CREATE_CIRCLE, LIMIT_CREATE_CIRCLE);
		limit.put(TYPE_BE_PRAISED, LIMIT_BE_PRAISED);
		limit.put(TYPE_PRAISE, LIMIT_PRAISE);
		//limit.put(TYPE_BE_COMMENT, LIMIT_BE_COMMENT);
		limit.put(TYPE_COMMENT, LIMIT_COMMENT);
		limit.put(TYPE_ACCESS_WEEKLY, LIMIT_ACCESS_WEEKLY);
		
		points.put(TYPE_DAILY_CHECKIN, POINT_DAILY_CHECKIN);
		points.put(TYPE_ACCESSS_APP, POINT_ACCESSS_APP);
		points.put(TYPE_CREATE_CIRCLE, POINT_CREATE_CIRCLE);
		points.put(TYPE_BE_PRAISED, POINT_BE_PRAISED);
		points.put(TYPE_PRAISE, POINT_PRAISE);
		//points.put(TYPE_BE_COMMENT, POINT_BE_COMMENT);
		points.put(TYPE_COMMENT, POINT_COMMENT);
		points.put(TYPE_ACCESS_WEEKLY, POINT_ACCESS_WEEKLY);
	}
}
