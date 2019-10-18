package com.talkweb.basecomp.sportsmeet.dao;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

public interface SportsMeetDao {

	public List<JSONObject> getSportsList(JSONObject param);
	
	public int createSports(JSONObject param);
	
	public int deleteSports(JSONObject param);
	
	public int updateSports(JSONObject param);
	
	public List<JSONObject> getEventsList(JSONObject param);

	public int createEvents(JSONObject param);

	public int deleteEvents(JSONObject param);

	public int updateEvents(JSONObject param);
	
	public List<JSONObject> getSportsGradeList(JSONObject param);
	
	public int createSportsGrade(JSONObject param);
	
	public int deleteSportsGrade(JSONObject param);

	public List<JSONObject> getScheduleList(JSONObject param);

	public int createSchedule(JSONObject param);
	
	public int deleteSchedule(JSONObject param);
	
	public List<JSONObject> getMemberList(JSONObject param);

	public int getMemberCount(JSONObject param);

	public int createMember(JSONObject param);

	public int deleteMember(JSONObject param);

	public List<JSONObject> getRecordList(JSONObject param);

	public int createRecord(JSONObject param);

	public int deleteRecord(JSONObject param);
	
	public int createEventsBatch(JSONObject param);
}
