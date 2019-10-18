package com.talkweb.basecomp.advice.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.advice.service.AdviceService;


@SuppressWarnings("restriction")
@Controller
@RequestMapping("/advice")
public class AdviceAction {
	 
    @Value("${advice.loginName}")
	private String loginName;
    
	protected final Log log = LogFactory.getLog(getClass());
	@Autowired
	private AdviceService adviceService;
	/** ---设置提示信息--- **/
	private void setPromptMessage(JSONObject object, String code, String message) {
		object.put("status", code);
		object.put("msg", message);
	}
	 	@RequestMapping("/getAdviceList")
	    @ResponseBody
	    public JSONObject getAdviceList(@RequestBody JSONObject param){
	 		JSONObject rs = new JSONObject();
	 		List<JSONObject> resultList = null;
	 		try {
	 			String accountId =	param.getString("accountId");
	 			String from =	param.getString("from");
	 			//String loginName = rb.getString("loginName");
	 			List<String> loginNames = Arrays.asList(loginName.split(","));
	 			if("1".equals(from)&& !loginNames.contains(accountId)) {
	 				setPromptMessage(rs,"-1", "权限不够，无法登陆！");
	 			    return rs;
	 			}
	 			if("1".equals(from) && loginName.equals(accountId)) {
	 				param.put("accountId", "");
	 				param.put("schoolId", "");
	 			}
	 			resultList = adviceService.getAdviceList(param);
			} catch (Exception e) {
				e.printStackTrace();
			}
	 		setPromptMessage(rs,"1", "查询成功");
	 		if(resultList==null) {
	 			rs.put("result", new ArrayList<JSONObject>());
	 		}else {
	 			rs.put("result", resultList);
	 		}
	 		return rs;
	 		 
	    }
		@RequestMapping("/getAdviceListForPc")
	    @ResponseBody
	    public JSONObject getAdviceListForPc(@RequestBody JSONObject param){
	 		JSONObject rs = new JSONObject();
	 		List<JSONObject> resultList = null;
	 		try {
	 			resultList = adviceService.getAdviceList(param);
			} catch (Exception e) {
				e.printStackTrace();
			}
	 		setPromptMessage(rs,"1", "查询成功");
	 		if(resultList==null) {
	 			rs.put("result", new ArrayList<JSONObject>());
	 		}else {
	 			rs.put("result", resultList);
	 		}
	 		return rs;
	 		 
	    }
		@RequestMapping("/addAdvice")
	    @ResponseBody
	    public JSONObject addAdvice(@RequestBody JSONObject param){
	 		JSONObject rs = new JSONObject();
	 		try {
				int i = adviceService.addAdvice(param);
				if(i==-1) {
					setPromptMessage(rs,"-1", "反馈内容不能为空！");
				}else if (i==-2){
					setPromptMessage(rs,"-2", "当前登录人不能为空！");
				}else if (i==-3){
					setPromptMessage(rs,"-3", "反馈类型不能为空！");
				}else if (i==-4){
					setPromptMessage(rs,"-4", "当前登录学校不能为空！");
				}else {
					setPromptMessage(rs,"1", "添加反馈成功");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return rs;
	 		 
	    }
		@RequestMapping("/addAdviceDialogue")
	    @ResponseBody
	    public JSONObject addAdviceDialogue(@RequestBody JSONObject param){
	 		JSONObject rs = new JSONObject();
	 		try {
				int i =adviceService.addAdviceDialogue(param);
				if(i==-1) {
					setPromptMessage(rs,"-1", "回复内容不能为空！");
				}else if(i==-2) {
					setPromptMessage(rs,"-2", "当前登录人不能为空！");
				}else if(i==-3) {
					setPromptMessage(rs,"-3", "当前登录学校不能为空！");
				}else if(i==-4) {
					setPromptMessage(rs,"-4", "adviceId不能为空！");
				} else {
					setPromptMessage(rs,"1", "添加回复成功");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return rs;
	 		 
	    }
		@RequestMapping("/addAdviceDialogueForPc")
	    @ResponseBody
	    public JSONObject addAdviceDialogueForPc(@RequestBody JSONObject param){
	 		JSONObject rs = new JSONObject();
	 		try {
				int i =adviceService.addAdviceDialogue(param);
				if(i==-1) {
					setPromptMessage(rs,"-1", "回复内容不能为空！");
				}else if(i==-2) {
					setPromptMessage(rs,"-2", "当前登录人不能为空！");
				}else if(i==-3) {
					setPromptMessage(rs,"-3", "当前登录学校不能为空！");
				}else if(i==-4) {
					setPromptMessage(rs,"-4", "adviceId不能为空！");
				} else {
					setPromptMessage(rs,"1", "添加回复成功");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return rs;
	 		 
	    }
		@RequestMapping("/getAdviceDetail")
	    @ResponseBody
	    public JSONObject getAdviceDetail(@RequestBody JSONObject param){
	 		JSONObject rs = new JSONObject();
	 		JSONObject result = null;
	 		try {
				result = adviceService.updateGetAdviceDetail(param);
				int i = result.getIntValue("num");
				result.remove("num");
				if(i==-1) {
					setPromptMessage(rs,"-1", "当前登录人不能为空！");
				}else if(i==-2) {
					setPromptMessage(rs,"-2", "当前登录学校不能为空！");
				}else if(i==-3) {
					setPromptMessage(rs,"-3", "adviceId不能为空！");
				}else {
					setPromptMessage(rs,"1", "查询成功");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	 		rs.put("result", result);
			return rs;
	 		 
	    }
		@RequestMapping("/getAdviceDetailForPc")
	    @ResponseBody
	    public JSONObject getAdviceDetailForPc(@RequestBody JSONObject param){
	 		JSONObject rs = new JSONObject();
	 		JSONObject result = null;
	 		try {
				result = adviceService.updateGetAdviceDetail(param);
				int i = result.getIntValue("num");
				result.remove("num");
				if(i==-1) {
					setPromptMessage(rs,"-1", "当前登录人不能为空！");
				}else if(i==-2) {
					setPromptMessage(rs,"-2", "当前登录学校不能为空！");
				}else if(i==-3) {
					setPromptMessage(rs,"-3", "adviceId不能为空！");
				}else {
					setPromptMessage(rs,"1", "查询成功");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	 		rs.put("result", result);
			return rs;
	 		 
	    }
		@RequestMapping("/updateIgnoreAdviceState")
	    @ResponseBody
	    public JSONObject updateIgnoreAdviceState(@RequestBody JSONObject param){
			JSONObject rs = new JSONObject();
	 		try {
				int i = adviceService.updateIgnoreAdviceState(param);
				if(i==-1) {
					setPromptMessage(rs,"-1", "adviceId不能为空");
				}else {
					setPromptMessage(rs,"1", "修改成功");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return rs;
	 		 
	    }
		@RequestMapping("/getUnreadAdviceCount")
	    @ResponseBody
	    public JSONObject getUnreadAdviceCount(@RequestBody JSONObject param){
			JSONObject rs = new JSONObject();
	 		try {
				int i = adviceService.getUnreadAdviceCount(param);
				if (i==-1) {
					setPromptMessage(rs, "-1", "accountId不能为空");
				} else {
					setPromptMessage(rs, "1", "");
				}
				rs.put("total", i);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return rs;
	 		 
	    }
	/*	@RequestMapping("/login")
	    @ResponseBody
	    public JSONObject login(@RequestBody JSONObject param){
			JSONObject rs = new JSONObject();
	 		String loginName = rb.getString("loginName");
	 		String psw = rb.getString("psw");
	 		String login = param.getString("loginName");
	 		String pwd = param.getString("psw");
	 		if(!(loginName.equals(login)&& psw.equals(pwd))) {
	 			setPromptMessage(rs,"-1", "登录失败");
	 		}
	 		setPromptMessage(rs,"1", "登录成功");
			return rs;
	 		 
	    }*/
}
