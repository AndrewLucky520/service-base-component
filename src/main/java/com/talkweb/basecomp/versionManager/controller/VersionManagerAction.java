package com.talkweb.basecomp.versionManager.controller;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.versionManager.service.VersionManagerService;

@Controller
@RequestMapping("/versionManger")
public class VersionManagerAction {
 
	@Autowired
	private VersionManagerService versionManagerService;
	
	@RequestMapping(value = "/getVersionList",method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getVersionList(@RequestBody JSONObject param , HttpServletRequest req, HttpServletResponse res){
		 JSONObject response = new JSONObject();
		 List<JSONObject> list = versionManagerService.getVersionList(param);
		 if (list!=null) {
			 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			for (int i = 0; i < list.size(); i++) {
				JSONObject item = list.get(i);
				String type = item.getString("type");
				item.put("typeName", type.replace("pc", "pc端").replace("mobile", "移动端"));
				item.put("publishDate", format.format(item.getDate("publishDate")));
			}
		}
		 
		 response.put("data", list);
		 response.put("code", 1);
	     response.put("msg", "查询成功！"); 
		return response;
		
	}
	
	@RequestMapping(value = "/addVersion",method = RequestMethod.POST)
	@ResponseBody
	public JSONObject addVersion(@RequestBody JSONObject param , HttpServletRequest req, HttpServletResponse res){
		JSONObject response = new JSONObject();
		String versionId = UUID.randomUUID().toString();
		param.put("versionId", versionId);
		int result = versionManagerService.addVersion(param);
		 if (result > 0) {
			 response.put("code", 1);
		     response.put("msg", "添加版本成功！"); 
		 }else {
			 response.put("code", -1);
		     response.put("msg", "添加版本失败！"); 
		}
		return response;
		
	}
	
	
	@RequestMapping(value = "/delVersion",method = RequestMethod.POST)
	@ResponseBody
	public JSONObject delVersion(@RequestBody JSONObject param , HttpServletRequest req, HttpServletResponse res){
		     JSONObject response = new JSONObject();
			 int result = versionManagerService.deleteVersion(param);
			 if (result > 0) {
				 response.put("code", 1);
			     response.put("msg", "删除版本成功！"); 
			 }else {
				 response.put("code", -1);
			     response.put("msg", "删除版本失败！"); 
			 }
		   return response;
	}
	
	
	
	@RequestMapping(value = "/updateVersion",method = RequestMethod.POST)
	@ResponseBody
	public JSONObject updateVersion(@RequestBody JSONObject param , HttpServletRequest req, HttpServletResponse res){
		JSONObject response = new JSONObject();
		 int result = versionManagerService.updateVersion(param);
		 if (result > 0) {
			 response.put("code", 1);
		     response.put("msg", "更新版本成功！"); 
		 }else {
			 response.put("code", -1);
		     response.put("msg", "更新版本失败！"); 
		 }
		return response;
		
	}
	
	
	@RequestMapping(value = "/publishVersion",method = RequestMethod.POST)
	@ResponseBody
	public JSONObject publishVersion(@RequestBody JSONObject param , HttpServletRequest req, HttpServletResponse res){
		JSONObject response = new JSONObject();
		 int result = versionManagerService.updateVersion(param);
		 if (result > 0) {
			 response.put("code", 1);
		     response.put("msg", "发布版本成功！"); 
		 }else {
			 response.put("code", -1);
		     response.put("msg", "发布版本失败！"); 
		 } 
		return response;
		
	}
	
 
}
