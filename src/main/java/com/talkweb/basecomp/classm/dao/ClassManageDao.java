package com.talkweb.basecomp.classm.dao;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description 班级管理DAO层接口实现类
 * @author xixi1979
 * @date 2018/7/15
 */
public interface ClassManageDao {

	/** -----更新座位初始化信息----- **/
	int updateInitSeat(JSONObject param);

	/** -----获取初始化座位信息----- **/
	JSONObject getInitSeat(JSONObject param);

	/** -----创建初始化座位信息----- **/
	int insertInitSeat(JSONObject param);
	
	/** -----添加学生座位信息----- **/
	int insertSeat(JSONObject param);

	/** -----更新学生座位信息----- **/
	int updateSeat(JSONObject param);

	/** -----删除学生座位信息----- **/
	int deleteSeat(JSONObject param);
	
	/** -----获取所有学生座位信息----- **/
	List<JSONObject> getSeatList(JSONObject param);

	/** -----根据学生账号信息获取学生座位信息----- **/
	List<JSONObject> getSeat(JSONObject param);

	/** -----根据座位信息获取对应学生信息----- **/
	List<JSONObject> getSeat2(JSONObject param);

	/** -----获取指定班级学生信息----- **/
	List<JSONObject> getStudentList(JSONObject param);
	
	/** -----获取指定班级信息----- **/
	JSONObject getClassInfo(JSONObject param);

	/** ---------- **/
	int insertPerformance(JSONObject param);
	int deletePerformance(JSONObject param);
	
	/** ---------- **/
	List<Integer> getPerformance(JSONObject param);

}