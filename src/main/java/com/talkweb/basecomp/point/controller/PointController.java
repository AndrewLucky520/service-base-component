package com.talkweb.basecomp.point.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.common.util.JSONUtil;
import com.talkweb.basecomp.point.service.PointService;

@Controller
@RequestMapping("/point")
public class PointController {
    private static final Logger log = LoggerFactory.getLogger(PointController.class);
	@Autowired
	private PointService pointService;
	
	@RequestMapping("/dailyCheckIn")
	@ResponseBody
	public JSONObject dailyCheckIn(@RequestBody JSONObject param) {
		try {
			return pointService.createDailyCheckIn(param);
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "签到异常。", ex, log);
		}
	}

	@RequestMapping("/addPoint")
	@ResponseBody
	public JSONObject addPoint(HttpServletRequest req, @RequestBody JSONObject param) {
		try {
			return pointService.addPoint(param);
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "添加积分异常。", ex, log);
		}			
	}

	@RequestMapping("/accessApp")
	@ResponseBody
	public JSONObject accessApp(HttpServletRequest req, @RequestBody JSONObject param) {
		try {
			return pointService.createAccessApp(param);
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "访问应用异常。", ex, log);
		}
	}

	@RequestMapping("/getPointData")
	@ResponseBody
	public JSONObject getPointData(HttpServletRequest req, @RequestBody JSONObject param) {
		try {
		return pointService.getPointData(param);
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "获取积分数据异常。", ex, log);
		}			
	}

	@RequestMapping("/getPointDetail")
	@ResponseBody
	public JSONObject getPointDetail(HttpServletRequest req, @RequestBody JSONObject param) {
		try {
			return pointService.getPointDetail(param);		
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "获取积分详情异常。", ex, log);
		}	
	}
}
