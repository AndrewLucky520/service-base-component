package com.talkweb.basecomp.versionManager.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

public interface VersionManagerService {

	public List<JSONObject> getVersionList(JSONObject param);
	
	public int addVersion(JSONObject param);
	
	public int deleteVersion(JSONObject param);
	
	public int updateVersion(JSONObject param);
 
}
