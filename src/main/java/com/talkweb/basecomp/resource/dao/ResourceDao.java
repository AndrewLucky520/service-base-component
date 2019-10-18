package com.talkweb.basecomp.resource.dao;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description DAO层接口
 * @author xixi1979
 * @date 2019/8/1
 */
public interface ResourceDao {
	/**-----创建资源目录-----**/
	int createDirectory(JSONObject param);

	/**-----修改资源目录 -----**/
	int updateDirectory(JSONObject param);

	/**-----删除资源目录 -----**/
	int deleteDirectory(JSONObject param);

	/**-----发布资源 -----**/
	int createResource(JSONObject param);

	/**-----修改资源信息 -----**/
	int updateResource(JSONObject param);

	/**-----删除资源 -----**/
	int deleteResource(JSONObject param);

	/**-----获取目录列表 -----**/
	List<JSONObject> getDirectoryList(JSONObject param);

	/**-----获取资源列表 -----**/
	List<JSONObject> getResourceList(JSONObject param);
}