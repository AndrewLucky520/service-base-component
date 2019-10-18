package com.talkweb.basecomp.weekly.dao;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description 班级周刊DAO层接口
 * @author xixi1979
 * @date 2018/8/1
 */
public interface ClassWeeklyDao {

	/**-----获取周刊分类列表 -----**/
	List<JSONObject> getWeeklyTypeList();

	/**-----获取指定班级周刊管理员列表 -----**/
	List<JSONObject> getAuthorityList(JSONObject param);

	/**-----获取周刊管理员设置数-----**/
	int getAuthorityCount(JSONObject param);

	/**-----获取指定班级周刊列表 -----**/
	List<JSONObject> getWeeklyList(JSONObject param);

	/**-----获取班级周刊数 -----**/
	int getWeeklyCount(JSONObject param);
	
	/**-----获取周刊相关数据 -----**/
	JSONObject getWeekly(JSONObject param);

	/**-----获取周刊统计数据 -----**/
	List<JSONObject> getWeeklyStatistics(JSONObject param);

	/**-----添加周刊管理员 -----**/
	int createAuthority(JSONObject param);

	/**-----修改周刊浏览次数-----**/
	int updateViews(JSONObject param);

	int updateFirstImage(JSONObject param);
	
	/**-----发布周刊 -----**/
	int createWeekly(JSONObject param);

	/**-----周刊关联附件 -----**/
	int createWeeklyAttachment(JSONObject param);

	/**-----附件关联图片 -----**/
	int createDocumentImage(JSONObject param);

	/**-----获取附件关联图片列表 -----**/
	List<JSONObject> getDocumentImage(JSONObject param);

	/**-----删除周刊管理员-----**/
	int deleteAuthority(JSONObject param);

	/**-----删除周刊-----**/
	int deleteWeekly(JSONObject param);

	/**-----删除周刊附件-----**/
	int deleteWeeklyAttachment(JSONObject param);

	/**-----删除周刊附件图片-----**/
	int deleteDocumentImage(JSONObject param);

	/**-----获取指定班级的基本信息，必须指定classId -----**/
	List<JSONObject> getClass(JSONObject param);

	/**-----获取老师任教的班级或者班主任管理的班级列表，必须指定老师的userId -----**/
	List<JSONObject> getUserWeeklyClassList(JSONObject param);

	/**-----获取年级列表 -----**/
	List<JSONObject> getGradeList(JSONObject param);

	/**-----获取指定年级的班级列表，必须指定年级Id -----**/
	List<JSONObject> getClassList(JSONObject param);
	
	/**-----获取管理的班级列表 -----**/
	List<JSONObject> getManagedClassList(JSONObject param);

	/**-----创建班徽 -----**/
	int createClassEmblem(JSONObject param);

	/**-----更新班徽-----**/
	int updateClassEmblem(JSONObject param);

	/**-----获取班级班徽 -----**/
	JSONObject getClassEmblem(JSONObject param);

}