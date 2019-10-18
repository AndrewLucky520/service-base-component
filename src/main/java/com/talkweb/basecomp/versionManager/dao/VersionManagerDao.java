package com.talkweb.basecomp.versionManager.dao;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

public interface VersionManagerDao {

	public List<JSONObject> getVersionList(JSONObject param);
	
	public int addVersion(JSONObject param);
	
	public int delVersion(JSONObject param);
	
	public int updateVersion(JSONObject param);
 
	
}
