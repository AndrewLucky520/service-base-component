package com.talkweb.basecomp.visitor.dao;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description DAO层接口
 * @author xixi1979
 * @date 2019/8/9
 */
public interface VisitorDao {
	/**-----创建访客-----**/
	int createVisitor(JSONObject param);

	/**-----修改访客 -----**/
	int updateVisitor(JSONObject param);

	/**-----删除访客 -----**/
	int deleteVisitor(JSONObject param);

	/**-----访客数 -----**/
	int getVisitorCount(JSONObject param);

	/**-----获取访客列表 -----**/
	List<JSONObject> getVisitorList(JSONObject param);
}