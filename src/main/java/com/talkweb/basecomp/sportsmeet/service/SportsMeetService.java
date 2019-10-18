package com.talkweb.basecomp.sportsmeet.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.Constants;
import com.talkweb.basecomp.common.data.DataCache;
import com.talkweb.basecomp.common.data.DataUtil;
import com.talkweb.basecomp.common.data.SchoolData;
import com.talkweb.basecomp.sportsmeet.dao.SportsMeetDao;

/**
 * @Description 服务层接口
 * @author xixi1979
 * @date 2019/8/1
 */
@Service
public class SportsMeetService {
	static final Logger log = LoggerFactory.getLogger(SportsMeetService.class);

	@Autowired
	private SportsMeetDao sportsMeetDao;
	@Autowired
	private DataCache dataCache;
	
	public List<JSONObject> getSportsList(JSONObject param) {
		String name = param.getString(Constants.FIELD_NAME);
		if (!StringUtils.isEmpty(name)) {
			param.put(Constants.FIELD_NAME, "%" + name + "%");
		}
		List<JSONObject> ret = sportsMeetDao.getSportsList(param);
		long time = System.currentTimeMillis();
		for (JSONObject obj: ret) {
			long start = obj.getDate("startTime").getTime();
			int status = 1;
			if (start <= time) {
				long end = obj.getDate("endTime").getTime();
				status = (end <= time ? 3 : 2);
			}
			obj.put("status", status);
			obj.put("gradeIds", DataUtil.stringToList(obj.getString("gradeIds")));
		}
		return ret;
	}
	
	public int createSports(JSONObject param) {
		param.put(Constants.FIELD_CREATETIME, new Date());
		param.put("sportsId", DataUtil.getUUID());
		this.sportsMeetDao.createSports(param);
		this.sportsMeetDao.createSportsGrade(param);
		return 1;
	}
	
	public int deleteSports(JSONObject param) {
		this.sportsMeetDao.deleteRecord(param);
		this.sportsMeetDao.deleteSchedule(param);
		this.sportsMeetDao.deleteSportsGrade(param);
		this.sportsMeetDao.deleteMember(param);
		return this.sportsMeetDao.deleteSports(param);
	}
	
	public int updateSports(JSONObject param) {
		this.sportsMeetDao.deleteSportsGrade(param);
		this.sportsMeetDao.createSportsGrade(param);
		return this.sportsMeetDao.updateSports(param);
	}
	
	public List<JSONObject> getEventsList(JSONObject param) {
		return this.sportsMeetDao.getEventsList(param);
	}

	public int createEvents(JSONObject param) {
		JSONObject tmp = new JSONObject();
		tmp.put("schoolId", param.get("schoolId"));
		tmp.put("eventsName", param.get("eventsName"));
		List<JSONObject> list = this.sportsMeetDao.getEventsList(tmp);
		if (!list.isEmpty()) {
			return -1;
		}
		if (!StringUtils.isNumber(param.getString("type"))) {
			param.put("type", 1);
		}
		param.put(Constants.FIELD_CREATETIME, new Date());
		param.put("eventsId", DataUtil.getUUID());
		return this.sportsMeetDao.createEvents(param);
	}

	public int deleteEvents(JSONObject param) {
		this.sportsMeetDao.deleteRecord(param);
		this.sportsMeetDao.deleteSchedule(param);
		this.sportsMeetDao.deleteMember(param);
		return this.sportsMeetDao.deleteEvents(param);
	}

	public int updateEvents(JSONObject param) {
		return this.sportsMeetDao.updateEvents(param);
	}
	
	public List<JSONObject> getSportsGradeList(JSONObject param) {
		List<JSONObject> ret = this.sportsMeetDao.getSportsGradeList(param);
		SchoolData sdata = dataCache.getSchoolData(param);
		for (JSONObject obj: ret) {
			String gradeId = obj.getString("gradeId");
			obj.put("id", gradeId);
			obj.put("name", sdata.getGradeName(gradeId));
		}
		ret = DataUtil.getTextValueList(param, ret, true);
		Collections.reverse(ret);
		return ret;
	}
	
	public List<JSONObject> getScheduleList(JSONObject param) {
		return this.sportsMeetDao.getScheduleList(param);
	}

	public int createSchedule(JSONObject param) {
		return this.sportsMeetDao.createSchedule(param);
	}
	
	public int deleteSchedule(JSONObject param) {
		return this.sportsMeetDao.deleteSchedule(param);
	}
	
	public List<JSONObject> getMemberList(JSONObject param) {
		SchoolData sdata = dataCache.getSchoolData(param);
		List<String> studentList = getStudentIdList(sdata, param);
		if (!studentList.isEmpty()) {
			param.put("studentIds", studentList);
		}
		List<JSONObject> ret = this.sportsMeetDao.getMemberList(param);
		for (JSONObject obj: ret) {
			String studentId = obj.getString("studentId");
			String gId = sdata.getStudentUsedGrade(studentId);
			obj.put("gradeName", sdata.getGradeName(gId));
			obj.put("className", sdata.getStudentClassName(studentId));
			obj.put("studentName", sdata.getStudentName(studentId));
		}
		return ret;
	}

	public int getMemberCount(JSONObject param) {
		return this.sportsMeetDao.getMemberCount(param);
	}

	public int createMember(JSONObject param) {
		SchoolData sdata = dataCache.getSchoolData(param);
		List<String> studentList = getStudentIdList(sdata, param);
		if (!studentList.isEmpty()) {
			JSONObject tmp = new JSONObject();
			tmp.put("sportsId", param.get("sportsId"));
			tmp.put("eventsId", param.get("eventsId"));
			tmp.put("studentIds", studentList);
			this.sportsMeetDao.deleteMember(tmp);
		}
		List<JSONObject> list = new ArrayList<>();
		JSONArray arr = param.getJSONArray("studentIds");
		for (int i = 0; i < arr.size(); i++) {
			JSONObject tmp = new JSONObject();
			tmp.put("eventsId", param.get("eventsId"));
			tmp.put("studentId", arr.get(i));
			list.add(tmp);
		}
		param.put("studentList", list);
		param.put(Constants.FIELD_CREATETIME, new Date());
		return this.sportsMeetDao.createMember(param);
	}

	public int deleteMember(JSONObject param) {
		return this.sportsMeetDao.deleteMember(param);
	}

	public List<JSONObject> getRecordList(JSONObject param) {
		List<JSONObject> ret = this.getMemberList(param);
		List<String> studentList = DataUtil.getFieldList(ret, "studentId");
		if (studentList.isEmpty())
			return ret;
		JSONObject tmp = new JSONObject();
		tmp.put("sportsId", param.get("sportsId"));
		tmp.put("eventsId", param.get("eventsId"));
		tmp.put("studentIds", studentList);
		List<JSONObject> list = this.sportsMeetDao.getRecordList(tmp);
		Map<String, String> recordMap = DataUtil.getKeyValueMap(list, "id", "record");
		for (JSONObject obj: ret) {
			String key = obj.get("eventsId") + obj.getString("studentId");
			if (recordMap.containsKey(key))
				obj.put("record", recordMap.get(key));
		}
		return ret;
	}

	public int createRecord(JSONObject param) {
		List<JSONObject> update = new ArrayList<>();
		JSONArray arr = param.getJSONArray("recordList");
		for (int i = 0; i < arr.size(); i++) {
			JSONObject obj = arr.getJSONObject(i);
			if (!StringUtils.isEmpty("studentId")
				&& !StringUtils.isEmpty("eventsId")) {
				update.add(obj);
			}
		}
		if (update.isEmpty()) 
			return 1;
		param.put("recordList", update);
		param.put(Constants.FIELD_CREATETIME, new Date());
		return this.sportsMeetDao.createRecord(param);
	}

	public List<JSONObject> getStudentList(JSONObject param) {
		List<JSONObject> ret = new ArrayList<>();
		SchoolData sdata = dataCache.getSchoolData(param);
		List<String> studentList = getStudentIdList(sdata, param);
		if (studentList.isEmpty()) {
			return ret;
		}
		param.put("studentIds", studentList);
		List<JSONObject> list = this.sportsMeetDao.getMemberList(param);
		List<String> hasList = DataUtil.getFieldList(list, "studentId");
		for (String studentId: studentList) {
			if (hasList.contains(studentId))//已报名
				continue;
			
			JSONObject obj = new JSONObject();
			obj.put("studentId", studentId);
			obj.put("studentName", sdata.getStudentName(studentId));
			ret.add(obj);
		}
		return ret;
	}

	public List<JSONObject> getGradeList(JSONObject param) {
		List<JSONObject> ret = new ArrayList<>();
		SchoolData sdata = dataCache.getSchoolData(param);
		List<String> gradeIds = sdata.getSortUsedGradeList();
		for (String gradeId: gradeIds) {
			JSONObject obj = new JSONObject();
			obj.put("id", gradeId);
			obj.put("name", sdata.getGradeName(gradeId));
			ret.add(obj);
		}
		return DataUtil.getTextValueList(param, ret, false);
	}

	public List<JSONObject> getClassList(JSONObject param) {
		String gradeId = param.getString("gradeId");
		if (gradeId != null && gradeId.contains(","))
			gradeId = null;
		SchoolData sdata = dataCache.getSchoolData(param);
		Collection<JSONObject> classList = null;
		if (!StringUtils.isEmpty(gradeId)) {
			classList = sdata.getGradeClassList(Arrays.asList(gradeId));
		} else {
			classList = sdata.getClassMap().values();
		}
		return DataUtil.getTextValueList(param, classList, true);
	}

	public int createEventsBatch(JSONObject param, List<JSONObject> list) {
		for (JSONObject jsonObject : list) {
			jsonObject.put("eventsId", DataUtil.getUUID());
		}
		param.put(Constants.FIELD_CREATETIME, new Date());
		param.put("eventsList", list);
		return sportsMeetDao.createEventsBatch(param);
	}

	public int createMemberBatch(JSONObject param, List<JSONObject> list) {
		param.put("studentList", list);
		param.put(Constants.FIELD_CREATETIME, new Date());
		return this.sportsMeetDao.createMember(param);
	}

	private List<String> getStudentIdList(SchoolData sdata, JSONObject param) {
		String classId = param.getString("classId");
		String gradeId = param.getString("gradeId");
		String name = param.getString("name");
		if (classId != null && classId.contains(","))
			classId = null;
		if (gradeId != null && gradeId.contains(","))
			gradeId = null;
		List<String> studentList = new ArrayList<>();
		if (!StringUtils.isEmpty(classId)) {
			studentList = sdata.queryStudentIdList(Arrays.asList(classId), name);
		} else if (!StringUtils.isEmpty(gradeId)) {
			List<String> classIdList = sdata.getGradeClassIdList(gradeId);
			studentList = sdata.queryStudentIdList(classIdList, name);
		} else if (!StringUtils.isEmpty(name)) {
			studentList = sdata.queryStudentIdList(name);
		}
		return studentList;
	}
}
