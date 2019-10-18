package com.talkweb.basecomp.classm.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.classm.service.ClassManageService;
import com.talkweb.basecomp.common.util.JSONUtil;

/**
 * @ClassName: SmallProgramAction.java
 * @version:1.0
 * @Description: OMS管理控制器
 * @date 2016年5月26日
 */
@Controller
@RequestMapping(value = "/classManage/")
public class ClassManageController {
    private static final Logger log = LoggerFactory.getLogger(ClassManageController.class);
	
	@Autowired
	private ClassManageService classManageService;

	@RequestMapping(value = "/updateInitSeat", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject updateInitSeat(HttpServletRequest req, @RequestBody JSONObject param) {
		try {
			return classManageService.updateInitSeat(param);
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "获取学生座位信息异常。", ex, log);
		}
	}

	@RequestMapping(value = "/getInitSeat", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getInitSeat(HttpServletRequest req, @RequestBody JSONObject param) {
		try {
			return classManageService.insertOrGetInitSeat(param);		
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "获取初始化座位信息异常。", ex, log);
		}	
	}
	
	@RequestMapping(value = "/updateSeat", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject updateSeat(HttpServletRequest req, @RequestBody JSONObject param) {
		try {
			return classManageService.updateSeat(param);			
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "修改学生座位信息异常。", ex, log);
		}		
	}

	@RequestMapping(value = "/getSeatList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getSeatList(HttpServletRequest req, @RequestBody JSONObject param) {
		return classManageService.getSeatList(param);			
	}

	@RequestMapping(value = "/getSeatListForApp", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getSeatListForApp(HttpServletRequest req, @RequestBody JSONObject param) {
		return classManageService.getSeatListForApp(param);			
	}

	@RequestMapping(value = "/getStudentList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getStudentList(HttpServletRequest req, @RequestBody JSONObject param) {
		return classManageService.getStudentList(param);			
	}

	@RequestMapping(value = "/getPerformance", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getPerformance(HttpServletRequest req, @RequestBody JSONObject param) {
		return classManageService.getPerformance(param);			
	}

	@RequestMapping(value = "/updatePerformance", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject updatePerformance(HttpServletRequest req, @RequestBody JSONObject param) {
		try {
			return classManageService.updatePerformance(param);	
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "修改学生在校表现异常。", ex, log);
		}		
	}
	
}
