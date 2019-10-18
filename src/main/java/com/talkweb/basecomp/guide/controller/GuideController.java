package com.talkweb.basecomp.guide.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.guide.service.GuideService;

/**
 * @ClassName:  引导
 * @version:1.0
 * @Description 
 * @date 2019.0727
   @author zhanghuihui15222
 */
@Controller
@RequestMapping(value = "/guide/")
public class GuideController {
    private static final Logger log = LoggerFactory.getLogger(GuideController.class);
	
	@Autowired
	private GuideService  guideService;
	/** ---设置提示信息--- **/
	private void setPromptMessage(JSONObject object, String code, String message) {
		object.put("status", code);
		object.put("msg", message);
	}
	@RequestMapping(value = "/addBanMyMenuGuide", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject addBanMyMenuGuide(HttpServletRequest req, @RequestBody JSONObject param) {
		JSONObject rs = new JSONObject();
		List<JSONObject> data = guideService.getBanMyMenuGuides(param);
		if(data!=null&&data.size()>0) {
			setPromptMessage(rs,"-1", "请误重复添加");
		}else {
			guideService.addBanMyMenuGuide(param);
			setPromptMessage(rs,"1", "添加成功");
		}
		return rs;
		 
	}

	@RequestMapping(value = "/getBanMyMenuGuides", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getBanMyMenuGuides(HttpServletRequest req, @RequestBody JSONObject param) {
		JSONObject rs = new JSONObject();
		List<JSONObject> data = guideService.getBanMyMenuGuides(param);
		String menuId = param.getString("menuId");
		if(StringUtils.isBlank(menuId)) {
			setPromptMessage(rs,"1", "查询成功");
			rs.put("data", data);
		}else {
			if(data!=null && data.size()>0) {
				setPromptMessage(rs,"1", "该功能禁止展示");
			}else {
				setPromptMessage(rs,"-1", "未禁止");
			}
		}
		return rs;
	}
	 
	
}
