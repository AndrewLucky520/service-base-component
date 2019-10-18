package com.talkweb.basecomp.weekly.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.common.util.JSONUtil;
import com.talkweb.basecomp.weekly.service.ClassWeeklyService;


/**
 * @Description 班级周刊Action层
 * @author xixi1979
 * @date 2018/8/1
 */
@Controller
@RequestMapping(value = "/classWeekly/")
public class ClassWeeklyController {
    private static final Logger log = LoggerFactory.getLogger(ClassWeeklyController.class);
	
	@Autowired
	private ClassWeeklyService classWeeklyService;

	@RequestMapping(value = "/getManagedClassList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getManagedClassList(HttpServletRequest req, @RequestBody JSONObject param) {
		return classWeeklyService.getManagedClassList(param);			
	}

	@RequestMapping(value = "/getClassList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getClassList(HttpServletRequest req, @RequestBody JSONObject param) {
		return classWeeklyService.getClassList(param);			
	}

	@RequestMapping(value = "/getWeeklyClassList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getWeeklyClassList(HttpServletRequest req, @RequestBody JSONObject param) {
		return classWeeklyService.getWeeklyClassList(param);			
	}

	@RequestMapping(value = "/getGradeList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getGradeList(HttpServletRequest req, @RequestBody JSONObject param) {
		return classWeeklyService.getGradeList(param);			
	}

	@RequestMapping(value = "/getWeeklyTypeList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getWeeklyTypeList(HttpServletRequest req, @RequestBody JSONObject param) {
		return classWeeklyService.getWeeklyTypeList(param);			
	}

	@RequestMapping(value = "/getWeeklyStatistics", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getWeeklyStatistics(HttpServletRequest req, @RequestBody JSONObject param) {
		return classWeeklyService.getWeeklyStatistics(param);			
	}

	@RequestMapping(value = "/exportStatistics", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<byte[]> exportStatistics(HttpServletRequest req) {
		return classWeeklyService.exportStatistics(req);			
	}

	@RequestMapping(value = "/downloadAttachment", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<byte[]> downloadAttachment(HttpServletRequest req) {
		return classWeeklyService.downloadAttachment(req);			
	}

	@RequestMapping(value = "/downloadAttachments", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<byte[]> downloadAttachments(HttpServletRequest req) {
		return classWeeklyService.downloadAttachments(req);			
	}
	
	@RequestMapping(value = "/getWeeklyList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getWeeklyList(HttpServletRequest req, @RequestBody JSONObject param) {
		return classWeeklyService.getWeeklyList(param);			
	}

	@RequestMapping(value = "/accessWeekly", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject accessWeekly(HttpServletRequest req, @RequestBody JSONObject param) {
		try {
			return classWeeklyService.updateAndGetWeekly(param);
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "浏览周刊详情异常。", ex, log);
		}	
	}

	@RequestMapping(value = "/createWeekly", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject createWeekly(HttpServletRequest req, @RequestBody JSONObject param) {
		try {
			return classWeeklyService.createWeekly(param);		
		} catch(Exception ex) {
			log.error("发布周刊异常。", ex);
			return JSONUtil.getResponse(param, "发布周刊异常。", ex, log);
		}		
	}

	@RequestMapping(value = "/deleteWeekly", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject deleteWeekly(HttpServletRequest req, @RequestBody JSONObject param) {
		try {
			return classWeeklyService.deleteWeekly(param);			
		} catch(Exception ex) {
			log.error("删除周刊异常。", ex);
			return JSONUtil.getResponse(param, "删除周刊异常。", ex, log);
		}			
	}

	@RequestMapping(value = "/createAuthority", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject createAuthority(HttpServletRequest req, @RequestBody JSONObject param) {
		try {
			return classWeeklyService.createAuthority(param);			
		} catch(Exception ex) {
			log.error("添加周刊管理员异常。", ex);
			return JSONUtil.getResponse(param, "添加周刊管理员异常。", ex, log);
		}					
	}

	@RequestMapping(value = "/deleteAuthority", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject deleteAuthority(HttpServletRequest req, @RequestBody JSONObject param) {
		try {
			return classWeeklyService.deleteAuthority(param);				
		} catch(Exception ex) {
			log.error("删除周刊管理员异常。", ex);
			return JSONUtil.getResponse(param, "删除周刊管理员异常。", ex, log);
		}							
	}

	@RequestMapping(value = "/getAuthorityList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getAuthorityList(HttpServletRequest req, @RequestBody JSONObject param) {
		return classWeeklyService.getAuthorityList(param);			
	}

	@RequestMapping(value = "/getAuthority", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getAuthority(HttpServletRequest req, @RequestBody JSONObject param) {
		return classWeeklyService.getAuthority(param);			
	}

	@RequestMapping(value = "/updateClassEmblem", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject updateClassEmblem(HttpServletRequest req, @RequestBody JSONObject param) {
		try {
			return classWeeklyService.updateClassEmblem(param);
		} catch(Exception ex) {
			log.error("修改班徽异常。", ex);
			return JSONUtil.getResponse(param, "修改班徽异常。", ex, log);
		}
	}

	@RequestMapping(value = "/getClassEmblem", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getClassEmblem(HttpServletRequest req, @RequestBody JSONObject param) {
		return classWeeklyService.getClassEmblem(param);			
	}

}
