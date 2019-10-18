package com.talkweb.basecomp.versionManager.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.versionManager.dao.VersionManagerDao;
import com.talkweb.basecomp.versionManager.service.VersionManagerService;

@Service
public class VersionManagerServiceImpl implements VersionManagerService{
	
	@Autowired
	private VersionManagerDao versionManagerDao;

	@Override
	public List<JSONObject> getVersionList(JSONObject param) {
		List<JSONObject> list = versionManagerDao.getVersionList(param);
		return list;
	}

	@Override
	public int addVersion(JSONObject param) {
		int result = versionManagerDao.addVersion(param);
		return result;
	}

	@Override
	public int deleteVersion(JSONObject param) {
		 String versionIds = param.getString("versionIds");
		 int result = 0;
		 if (StringUtils.isNotBlank(versionIds)) {
			 String []versions = versionIds.split(",");
			 for (int i = 0; i < versions.length; i++) {
				 param.put("versionId", versions[i]);
				 result = result + versionManagerDao.delVersion(param);
			 }
		 }
		 return result;
	}

	@Override
	public int updateVersion(JSONObject param) {
		int result = versionManagerDao.updateVersion(param);
		return result;
	}

	
	
}
