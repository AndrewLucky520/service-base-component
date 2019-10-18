package com.talkweb.basecomp.point.dao;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

public interface PointDao {

	Integer getPoint(JSONObject param);

	List<JSONObject> getPointDetail(JSONObject param);

	int getPointDetailCount(JSONObject param);

	int createPoint(JSONObject param);

	Integer getAccountPoint(JSONObject param);

	int createAccountPoint(JSONObject param);

	int updateAccountPoint(JSONObject param);

}
