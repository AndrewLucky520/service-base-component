package com.talkweb.basecomp.visitor.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.Constants;
import com.talkweb.basecomp.common.util.JSONUtil;
import com.talkweb.basecomp.visitor.service.VisitorService;


/**
 * @Description 班级圈Action层
 * @author xixi1979
 * @date 2018/9/11
 */
@Controller
@RequestMapping(value = "/visitor/")
public class VisitorController {
	static final Logger log = LoggerFactory.getLogger(VisitorController.class);
	
	@Autowired
	private VisitorService visitorService;

	@RequestMapping(value = "/getVisitorList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getVisitorList(HttpServletRequest req, @RequestBody JSONObject param) {
		try {
			List<JSONObject> list = this.visitorService.getVisitorList(param);
			JSONObject ret = JSONUtil.getResponse(1, list);
			ret.put("total", this.visitorService.getVisitorCount(param));
			return ret;
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "获取访客列表异常。");
		}	
	}

	@RequestMapping(value = "/createVisitor", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject createVisitor(HttpServletRequest req, @RequestBody JSONObject param) {
		if (StringUtils.isEmpty(param.getString("visitorName"))) {
			return JSONUtil.getResponse(-1, "访客姓名为空，处理失败！");
		}
		if (StringUtils.isEmpty(param.getString("byVisitor"))) {
			return JSONUtil.getResponse(-1, "被访人为空，处理失败！");
		}
		if (StringUtils.isEmpty(param.getString("mobilePhone"))) {
			return JSONUtil.getResponse(-1, "手机号码为空，处理失败！");
		}

		try {
			this.visitorService.createVisitor(param);
			return JSONUtil.getResponse(1);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "创建访客异常。");
		}		
	}
	
	@RequestMapping(value = "/updateVisitor", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject updateVisitor(HttpServletRequest req, @RequestBody JSONObject param) {
		if (StringUtils.isEmpty(param.getString("mobilePhone"))) {
			return JSONUtil.getResponse(-1, "手机号码为空，处理失败！");
		}

		try {
			this.visitorService.updateVisitor(param);
			return JSONUtil.getResponse(1);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "修改访客异常。");
		}		
	}

	@RequestMapping(value = "/visitorLeave", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject visitorLeave(HttpServletRequest req, @RequestBody JSONObject param) {
		try {
			this.visitorService.updateVisitorStatus(param);
			return JSONUtil.getResponse(1);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "修改访客异常。");
		}		
	}
	
	@RequestMapping(value = "/deleteVisitor", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject deleteVisitor(HttpServletRequest req, @RequestBody JSONObject param) {
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ID))) {
			return JSONUtil.getResponse(-1, "目录ID为空，处理失败！");
		}
		try {
			this.visitorService.deleteVisitor(param);
			return JSONUtil.getResponse(1);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "删除访客异常。");
		}		
	}
	
}
