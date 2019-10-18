package com.talkweb.basecomp.guide.dao;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description  
 * @author  zhanghuihui
 * @date  20190727
 */
public interface GuideDao {

	void addBanMyMenuGuide(JSONObject param);

	List<JSONObject> getBanMyMenuGuides(JSONObject param);

}