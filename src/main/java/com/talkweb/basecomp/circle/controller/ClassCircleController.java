package com.talkweb.basecomp.circle.controller;

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
import com.talkweb.basecomp.circle.service.ClassCircleService;
import com.talkweb.basecomp.common.util.JSONUtil;


/**
 * @Description 班级圈Action层
 * @author xixi1979
 * @date 2018/9/11
 */
@Controller
@RequestMapping(value = "/classCircle/")
public class ClassCircleController {
	private static final Logger log = LoggerFactory.getLogger(ClassCircleController.class);
	
	@Autowired
	private ClassCircleService classCircleService;

	@RequestMapping(value = "/getMomentList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getMomentList(HttpServletRequest req, @RequestBody JSONObject param) {
		return classCircleService.getCircleList(param);			
	}

	@RequestMapping(value = "/getTopCircle", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getTopCircle(HttpServletRequest req, @RequestBody JSONObject param) {
		return classCircleService.getTopCircle(param);			
	}

	@RequestMapping(value = "/saveMoments", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject saveMoments(HttpServletRequest req, @RequestBody JSONObject param) {
		try {
			return classCircleService.createCircle(param);		
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "发布班级圈动态异常。", ex, log);
		}
	}

	@RequestMapping(value = "/deleteMoment", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject deleteMoment(HttpServletRequest req, @RequestBody JSONObject param) {
		try {
			return classCircleService.deleteCircle(param);		
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "删除班级圈动态异常。", ex, log);
		}
	}

	@RequestMapping(value = "/saveComment", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject saveComment(HttpServletRequest req, @RequestBody JSONObject param) {
		try {
			return classCircleService.createCircleComment(param);		
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "评论异常。", ex, log);
		}		
	}

	@RequestMapping(value = "/deleteComment", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject deleteComment(HttpServletRequest req, @RequestBody JSONObject param) {
		try {
			return classCircleService.deleteCircleComment(param);				
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "删除评论异常。", ex, log);
		}
	}

	@RequestMapping(value = "/praise", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject praise(HttpServletRequest req, @RequestBody JSONObject param) {
		try {
			return classCircleService.createOrDeleteCircleLike(param);		
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "点赞/取消点赞异常。", ex, log);
		}		
	}
}
