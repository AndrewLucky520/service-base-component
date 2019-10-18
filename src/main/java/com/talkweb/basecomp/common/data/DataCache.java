package com.talkweb.basecomp.common.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.common.config.RedisUtils;

@Component
public class DataCache {
    private static final Logger log = LoggerFactory.getLogger(DataCache.class);
    
	@Autowired
	private RedisUtils redisUtils;

	@Autowired
	private BaseDataInterface baseDataInterface;

	private Map<String, String> errorKeys = new ConcurrentHashMap<>();

	public String getCurrentTermInfo() {
		String key = "currentTermInfo";
		String str = (String) CacheManager.get(key);
		if (str == null) {
			try {
				JSONObject obj = baseDataInterface.getCurrentTermInfoId();
				if (obj != null && obj.get("termInfo") != null)
					str = obj.getString("termInfo");
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		if (!StringUtils.isEmpty(str)) {
			CacheManager.set(key, str, ExpireTime.oneHourExpireTime.getTimeValue());
			return str;
		}
		return null;
	}

	public String getFirstTermInfo() {
		String key = "firstTermInfo";
		String str = (String) CacheManager.get(key);
		if (str == null) {
			try {
				JSONObject obj = baseDataInterface.getFirstTermInfoId();
				if (obj != null && obj.get("firstTermInfo") != null)
					str = obj.getString("firstTermInfo");
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		if (!StringUtils.isEmpty(str)) {
			CacheManager.set(key, str, ExpireTime.oneHourExpireTime.getTimeValue());
			return str;
		}
		return this.getCurrentTermInfo();
	}

	public void updateClassType(JSONObject param, String classId, int classType) {
		String xnxq = getTermInfo(param);
		Long schoolId = DataUtil.getSchoolId(param);
		List<JSONObject> list = this.baseDataInterface.getClassroomBatch(Arrays.asList(classId), schoolId, xnxq);
		if (list.isEmpty()) {
			throw new RuntimeException("查询班级" + classId+ "基础数据出错！");
		}
		String typeStr = "";
		for (String str: classTypeNameMap.keySet()) {
			if (classTypeNameMap.get(str) == classType) {
				typeStr = str;
				break;
			}
		}

		//System.out.println("teacherLessons =========== "+list.get(0).get("teacherLessons"));
		SchoolData sdata = this.getSchoolData(param);
		sdata.classMap = null;
		String key = getSchoolDataKey(param);
		this.setRedisObject(key, sdata.toJSONObject());
		
		JSONObject update = new JSONObject();
		update.put("id", classId);
		update.put("classType", typeStr);
		update.put("teacherLessons", list.get(0).get("teacherLessons"));
		this.baseDataInterface.updateClassroom(schoolId, update, xnxq);
	}

	public JSONObject getSchoolByExtId(String extId, String xnxq) {
		return this.baseDataInterface.getSchoolByExtId(extId, xnxq);
	}

	public List<JSONObject> getSimpleSchoolBatch(JSONObject param, List<Long> schoolIds) {
		String xnxq = getTermInfo(param);
		List<JSONObject> list = baseDataInterface.getSimpleSchoolBatch(schoolIds, xnxq);
		return list == null ? new ArrayList<>() : list;
	}

	public <E> List<JSONObject> listAccountByExtUserId(Collection<E> userIds, String xnxq) {
		List<JSONObject> list = baseDataInterface.listAccountByExtUserId(userIds, xnxq);
		return list == null ? new ArrayList<>() : list;
	}

	public <E> List<JSONObject> getParentList(JSONObject param, Collection<E> accountIds) {
		String xnxq = getTermInfo(param);
		Long schoolId = DataUtil.getSchoolId(param);
		return getParentList(schoolId, xnxq, accountIds);
	}

	public <E> List<JSONObject> getParentList(Long schoolId, String xnxq, Collection<E> accountIds) {
		List<JSONObject> list = this.baseDataInterface.getSimpleParentByStuMsg(accountIds, schoolId, xnxq);
		if (list != null) {
			for (JSONObject obj: list) {
				obj.put("id", obj.get("parentId"));
				obj.put("extId", obj.get("extUserId"));
				obj.put("studentId", obj.get("studentAccountId"));
				obj.remove("studentAccountId");
				obj.remove("extUserId");
			}
		}
		return list == null ? new ArrayList<>() : list;
	}

	public List<JSONObject> getAccountList(JSONObject param, List<Long> ids) {
		String xnxq = getTermInfo(param);
		List<JSONObject> list = null;
		if (ids != null && !ids.isEmpty())
			list = this.baseDataInterface.listAccountById(ids, xnxq);
		return list == null ? new ArrayList<>() : list;
	}

	public <E> List<JSONObject> getUserList(JSONObject param, Collection<E> userIds) {
		String xnxq = getTermInfo(param);
		Long schoolId = DataUtil.getSchoolId(param);
		return getUserList(schoolId, xnxq, userIds);
	}

	public <E> List<JSONObject> getUserList(Long schoolId, String xnxq, Collection<E> userIds) {
		List<JSONObject> list = this.baseDataInterface.getUserBatch(userIds, schoolId, xnxq);
		return list == null ? new ArrayList<>() : list;
	}

	public Object getRedisObject(String key) {
		try {
			return redisUtils.get(key);
		} catch(Exception ex) {
			log.error("获取缓存异常。", ex);
			return null;
		}
	}
	
	public void setRedisObject(String key, Object obj) {
		try {
			redisUtils.set(key, obj, ExpireTime.minExpireTime.getTimeValue());
		} catch(Exception ex) {
			errorKeys.put(key, "");
			log.error("设置缓存异常。", ex);
		}
	}

	public void clearRedisObject(String key) {
		try {
			redisUtils.del(key);
		} catch(Exception ex) {
			errorKeys.put(key, "");
			log.error("清除缓存异常。", ex);
		}
	}

	public void clearSchoolData(JSONObject param) {
		String key = getSchoolDataKey(param);
		clearRedisObject(key);
	}

	public SchoolData getSchoolData(JSONObject param) {
		String key = getSchoolDataKey(param);
		JSONObject data = (JSONObject) this.getRedisObject(key);
		SchoolData sdata = new SchoolData();
		if (data == null) {
			sdata.schoolId = DataUtil.getSchoolId(param);
			sdata.termInfo = this.getTermInfo(param);
		} else {
			sdata.fromJSONObject(data);
		}
		sdata.setDataCache(this);
		return sdata;
	}

    public String getSchoolDataKey(JSONObject param) {
		Long schoolId = DataUtil.getSchoolId(param);
		String xnxq = getTermInfo(param);
		return getSchoolDataKey(schoolId, xnxq);
	}

    public String getSchoolDataKey(Long schoolId, String xnxq) {
		String key = schoolId + "." + xnxq + ".schooldata";
		return key;
	}

    public void setSchoolData(SchoolData data) {
		String key = getSchoolDataKey(data.schoolId, data.termInfo);
		JSONObject bkobj = (JSONObject) this.getRedisObject(key);
		if (bkobj != null) {//更新缓存
			SchoolData bk = new SchoolData();
			bk.fromJSONObject(bkobj);
			if (bk.classMap != null && data.classMap == null) {
				data.classMap = bk.classMap;
			}
			if (bk.gradeMap != null && data.gradeMap == null) {
				data.gradeMap = bk.gradeMap;
			}
			if (bk.lessonMap != null && data.lessonMap == null) {
				data.lessonMap = bk.lessonMap;
			}
			if (bk.orgList != null && data.orgList == null) {
				data.orgList = bk.orgList;
			}
			if (bk.teacherMap != null && data.teacherMap == null) {
				data.teacherMap = bk.teacherMap;
			}
			if (bk.studentMap != null && data.studentMap == null) {
				data.studentMap = bk.studentMap;
			}
			if (bk.parentMap != null && data.parentMap == null) {
				data.parentMap = bk.parentMap;
			}
		}
		setRedisObject(key, data.toJSONObject());
	}

	public void initOrgList(SchoolData data) {
		if (data.orgList == null) {
			List<JSONObject> list = this.getOrgList(data.schoolId, data.termInfo);
			if (list != null && !list.isEmpty()) {
				data.orgList = list;
				this.setSchoolData(data);
			}
		}
	}

	public void initLessonMap(SchoolData data) {
		if (data.lessonMap == null) {
			Map<String, JSONObject> resultMap = new HashMap<>();
			List<JSONObject> list = this.getLessonList(data.schoolId, data.termInfo);
			resultMap = DataUtil.getIdMap(list);
			if (!resultMap.isEmpty()) {
				data.lessonMap = resultMap;
				this.setSchoolData(data);
			}
		}
	}
	
	public void initGradeMap(SchoolData data) {
		if (data.gradeMap == null) {
			Map<String, JSONObject> resultMap = new HashMap<>();
			List<JSONObject> list = this.getGradeList(data.schoolId, data.termInfo);
			resultMap = DataUtil.getIdMap(list);
			if (!resultMap.isEmpty()) {
				data.gradeMap = resultMap;
				this.setSchoolData(data);
			}
		}
	}

	public void initClassMap(SchoolData data) {
		if (data.classMap == null) {
			Map<String, JSONObject> resultMap = new HashMap<>();
			List<JSONObject> list = this.getClassList(data.schoolId, data.termInfo);
			resultMap = DataUtil.getIdMap(list);
			if (!resultMap.isEmpty()) {
				data.classMap = resultMap;
				this.setSchoolData(data);
			}
		}
	}
	
	public void initTeacherMap(SchoolData data) {
		if (data.teacherMap == null) {
			Map<String, JSONObject> resultMap = new HashMap<>();
			List<JSONObject> list = this.getTeacherList(data.schoolId, data.termInfo);
			resultMap = DataUtil.getIdMap(list);
			if (!resultMap.isEmpty()) {
				data.teacherMap = resultMap;
				this.setSchoolData(data);
			}
		}
	}

	public void initStudentMap(SchoolData data) {
		if (data.studentMap == null) {
			Map<String, JSONObject> resultMap = new HashMap<>();
			List<JSONObject> list = this.getStudentList(data.schoolId, data.termInfo);
			resultMap = DataUtil.getIdMap(list);
			if (!resultMap.isEmpty()) {
				data.studentMap = resultMap;
				this.setSchoolData(data);
			}
		}
	}

	public void initParentMap(SchoolData data) {
		if (data.parentMap == null) {
			Map<String, JSONObject> resultMap = new HashMap<>();
			List<JSONObject> list = this.getParentList(data.schoolId, data.termInfo, 
					data.getStudentMap().keySet());
			resultMap = DataUtil.getIdMap(list);
			if (!resultMap.isEmpty()) {
				data.parentMap = resultMap;
				this.setSchoolData(data);
			}
		}
	}

	public String getTermInfo(JSONObject param) {
		String xnxq = DataUtil.getTermInfo(param);
		if (!StringUtils.isEmpty(xnxq))
			return xnxq;
		return this.getCurrentTermInfo();
	}

	private List<JSONObject> getStudentList(Long schoolId, String xnxq) {
		List<JSONObject> list = new ArrayList<>();
		JSONObject s = new JSONObject();
		s.put("termInfoId", xnxq);
		s.put("schoolId", schoolId);

		list = this.baseDataInterface.getStudentList(s);
		if (list != null) {
			for (JSONObject info : list) {
				Object clsId = null;
				try {
					JSONObject ss = info.getJSONArray("users").getJSONObject(0).getJSONObject("studentPart");
					if (ss != null) {
						clsId = ss.get("classId");
					}
				} catch(Exception ex) {}
				info.put("classId", clsId);
			}
		}
		return list;
	}
	
	private List<JSONObject> getOrgList(Long schoolId, String xnxq) {
		List<JSONObject> list = this.baseDataInterface.getSchoolOrgList2(schoolId, xnxq);
		if (list != null) {
			for (JSONObject info : list) {
				info.put("orgId", info.get("id"));
				
				JSONArray arr = info.getJSONArray("scopeTypes");
				if (arr != null && !arr.isEmpty()) {
					JSONArray newarr = new JSONArray();
					for (Object str: arr) {
						Integer val = gradeLevelNameMap.get(str.toString());
						if (val != null) {
							newarr.add(val);
						}
					}
					info.put("scopeTypes", newarr);
				}
			}
		}
		return list == null ? new ArrayList<>() : list;
	}

	private List<JSONObject> getLessonList(Long schoolId, String xnxq) {
		List<JSONObject> list = baseDataInterface.getLessonInfoList(schoolId, xnxq);
		return list == null ? new ArrayList<>() : list;
	}

	private List<JSONObject> getGradeList(Long schoolId, String xnxq) {
		if (StringUtils.isEmpty(xnxq)) {
			xnxq = this.getCurrentTermInfo();
		}
		String xn = xnxq.substring(0, xnxq.length() - 1);
		JSONObject s = new JSONObject();
		s.put("id", schoolId);
		
		List<JSONObject> list = baseDataInterface.getGradeList(s, xnxq);
		if (list != null) {
			for (JSONObject obj: list) {
				String lvStr = obj.getString("currentLevel");
				Integer level = gradeLevelNameMap.get(lvStr);
				int year = DataUtil.convertLevel(level, xn);
				obj.put("currentLevel", level);
				//obj.put("gradeId", obj.get("id"));
				obj.put("id", obj.get("id"));
				obj.put("name", gradeNameMap.get(level));
				obj.put("usedGrade", year);
				obj.put("startYear", DataUtil.getGradeStartYear(level, xn));
				lvStr = obj.getString("createLevel");
				level = gradeLevelNameMap.get(lvStr);
				obj.put("createLevel", level);
			}
		}
		return list == null ? new ArrayList<>() : list;
	}

	private List<JSONObject> getClassList(Long schoolId, String termInfo) {
		JSONObject map = new JSONObject();
		map.put("schoolId", schoolId);
		map.put("termInfoId",termInfo);
		List<JSONObject> list = baseDataInterface.getClassList(map);
		if (list != null) {
			for (JSONObject obj: list) {
				obj.put("name", obj.get("className"));
				obj.put("classType", classTypeNameMap.get(obj.getString("classType")));
			}
		}
		return list == null ? new ArrayList<>() : list;
	}

	private List<JSONObject> getTeacherList(Long schoolId, String xnxq) {
		JSONObject s = new JSONObject();
		s.put("id", schoolId);
		
		List<JSONObject> list = this.baseDataInterface.getAllSchoolEmployees("", s, xnxq);
		//System.out.println("getTeacherList #######" + list);
		if (list != null) {
			for (JSONObject info : list) {
				JSONArray arr = info.getJSONArray("users");
				if (arr != null) {
					for (int i = 0; i < arr.size(); i++) {
						JSONObject user = arr.getJSONObject(i);
						JSONObject t = user.getJSONObject("teacherPart");
						JSONObject u = user.getJSONObject("userPart");
						if (t != null) {
							info.put("userId", t.get("id"));
							info.put("courseIds", t.get("courseIds"));
							info.put("deanOfClassIds", t.get("deanOfClassIds"));
							if (u != null) {
								info.put("orgIds", u.get("orgIds"));
								info.put("deanOfOrgIds", u.get("deanOfOrgIds"));
							}
							break;
						}
					}
					info.remove("users");
				}
			}
		}
		return list == null ? new ArrayList<>() : list;
	}

	private static Map<String, Integer> gradeLevelNameMap;
	private static Map<Integer, String> gradeNameMap;
	private static Map<String, Integer> classTypeNameMap;
	
	static {
		gradeLevelNameMap = new HashMap<>();
		gradeLevelNameMap.put("T_Kindergarten_SSml", 6);
		gradeLevelNameMap.put("T_Kindergarten_Sml", 7);
		gradeLevelNameMap.put("T_Kindergarten_Mid", 8);
		gradeLevelNameMap.put("T_Kindergarten_Big", 9);
		gradeLevelNameMap.put("T_PrimaryOne", 10);
		gradeLevelNameMap.put("T_PrimaryTwo", 11);
		gradeLevelNameMap.put("T_PrimaryThree", 12);
		gradeLevelNameMap.put("T_PrimaryFour", 13);
		gradeLevelNameMap.put("T_PrimaryFive", 14);
		gradeLevelNameMap.put("T_PrimarySix", 15);
		gradeLevelNameMap.put("T_JuniorOne", 16);
		gradeLevelNameMap.put("T_JuniorTwo", 17);
		gradeLevelNameMap.put("T_JuniorThree", 18);
		gradeLevelNameMap.put("T_HighOne", 19);
		gradeLevelNameMap.put("T_HighTwo", 20);
		gradeLevelNameMap.put("T_HighThree", 21);
		gradeLevelNameMap.put("T_JuniorFour", 22);
		gradeNameMap = new HashMap<>();
		gradeNameMap.put(6, "小小班");
		gradeNameMap.put(7, "小班");
		gradeNameMap.put(8, "中班");
		gradeNameMap.put(9, "大班");
		gradeNameMap.put(10, "一年级");
		gradeNameMap.put(11, "二年级");
		gradeNameMap.put(12, "三年级");
		gradeNameMap.put(13, "四年级");
		gradeNameMap.put(14, "五年级");
		gradeNameMap.put(15, "六年级");
		gradeNameMap.put(16, "初一");
		gradeNameMap.put(17, "初二");
		gradeNameMap.put(18, "初三");
		gradeNameMap.put(19, "高一");
		gradeNameMap.put(20, "高二");
		gradeNameMap.put(21, "高三");
		gradeNameMap.put(22, "初四");
		classTypeNameMap = new HashMap<>();
		classTypeNameMap.put("multiple", 0);
		classTypeNameMap.put("Arts", 1);
		classTypeNameMap.put("Science", 2);
	}
}
