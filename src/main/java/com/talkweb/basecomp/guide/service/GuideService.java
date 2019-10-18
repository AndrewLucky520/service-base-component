package com.talkweb.basecomp.guide.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.guide.dao.GuideDao;

/**
 * @Description  
 * @author  zhanghuihui
 * @date 2019/07/27
 */
@Service
public class GuideService {
    private static final Logger log = LoggerFactory.getLogger(GuideService.class);
	
	@Autowired
	private GuideDao  guideDao ;
	 
	public void addBanMyMenuGuide(JSONObject param) {
		guideDao.addBanMyMenuGuide(param);
	}


	public List<JSONObject> getBanMyMenuGuides(JSONObject param) {
		return guideDao.getBanMyMenuGuides(param);
	}
}