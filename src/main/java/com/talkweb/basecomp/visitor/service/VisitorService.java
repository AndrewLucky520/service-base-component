package com.talkweb.basecomp.visitor.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.Constants;
import com.talkweb.basecomp.visitor.dao.VisitorDao;

/**
 * @Description 服务层接口
 * @author xixi1979
 * @date 2019/8/1
 */
@Service
public class VisitorService {
	private static final Logger log = LoggerFactory.getLogger(VisitorService.class);
	
	@Autowired
	private VisitorDao visitorDao;

	public List<JSONObject> getVisitorList(JSONObject param) {
		log.debug("getVisitorList，参数：{}", param);
		String name = param.getString(Constants.FIELD_NAME);
		if (!StringUtils.isEmpty(name)) {
			param.put(Constants.FIELD_NAME, "%" + name + "%");
		}
		List<JSONObject> list = this.visitorDao.getVisitorList(param);
		return list;
	}

	public int createVisitor(JSONObject param) {
		log.debug("createVisitor，参数：{}", param);
		param.put(Constants.FIELD_ID, getUUID());
		return visitorDao.createVisitor(param);
	}

	public int updateVisitor(JSONObject param) {
		log.debug("updateVisitor，参数：{}", param);
		return visitorDao.updateVisitor(param);
	}

	public int updateVisitorStatus(JSONObject param) {
		log.debug("updateVisitorStatus，参数：{}", param);
		param.put(Constants.FIELD_CREATETIME, new Date());
		return visitorDao.updateVisitor(param);
	}

	public int deleteVisitor(JSONObject param){
		log.debug("deleteVisitor，参数：{}", param);
		visitorDao.deleteVisitor(param);
		return 1;
	}

	public int getVisitorCount(JSONObject param){
		log.debug("getVisitorCount，参数：{}", param);
		return visitorDao.getVisitorCount(param);
	}
	
    private static String getUUID() {
    	return UUID.randomUUID().toString().replaceAll("-", "");
    }
}