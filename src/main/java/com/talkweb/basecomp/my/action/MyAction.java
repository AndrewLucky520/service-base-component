package com.talkweb.basecomp.my.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.druid.util.Utils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.common.util.HttpClientToken;
 
@Controller
@RequestMapping("/my")
public class MyAction {
	 @Value("${clientId}")
	 private String clientId;
	 @Value("${secretId}")
	 private String secretId;
	 @Value("${my.getPhoneValidate}")
	 private String getPhoneValidate;
	 @Value("${my.updatePhoneByValidate}")
	 private String updatePhoneByValidate;
	 @Value("${my.updateUserPhoto}")
	 private String updateUserPhoto;
	 @Value("${my.judgeValidate}")
	 private String judgeValidate;
	 @Value("${my.modifyUser}")
	 private String modifyUser;
	 @Value("${my.getUsersByCondition}")
	 private String getUsersByCondition;
	 @Value("${my.modifyPwdByVerifyCode}")
	 private String modifyPwdByVerifyCode;
	 @Value("${my.updatePwd}")
	 private String updatePwdUrl;
	 @Value("${my.getUserByUserId}")
	 private String getUserByUserId;
	 @Value("${my.modifyEmailByVerifyCode}")
	 private String modifyEmailByVerifyCode;
	 
	protected final Log log = LogFactory.getLog(getClass());
	
	/** ---设置提示信息--- **/
	private void setPromptMessage(JSONObject object, String code, String message) {
		object.put("status", code);
		object.put("code", code);
		object.put("msg", message);
	}
	
	/** ---解析结果数据--- **/
	private JSONObject parseResult(String method,JSONObject info,JSONObject rs) {
		if(info==null){
			setPromptMessage(rs, "-3", "[" + method + "]"+ "获取基础平台接口身份信息失败");
			return rs;
		}
		JSONObject serverResult = (JSONObject) info.get("serverResult");
		if(serverResult == null){
			setPromptMessage(rs, "-3", "[" + method + "]"+ "获取基础平台接口身份信息失败");
			return rs;
		}
		String resultCode = serverResult.getString("resultCode");
        String resultMsg= serverResult.getString("resultMsg");
        log.info("[jycloud] resultCode of info:"+resultCode +" resultMsg:"+resultMsg);
        if("200".equals(resultCode)){
        	setPromptMessage(rs, "1", "请求成功");
 			return rs;
        }else {
        	setPromptMessage(rs, "-1", resultMsg);
        	return rs;
        }
	}
	/**
	 * 获取手机验证码
	 * @param param
	 * @return
	 */
 	@RequestMapping("/updatePwd")
    @ResponseBody
    public JSONObject updatePwd(@RequestBody JSONObject param){
 		  // 定义方法内的全局变量
		JSONObject data = new JSONObject();
		String oldUserPwd = param.getString("oldUserPwd");
		String newUserPwd = param.getString("newUserPwd");
		if(StringUtils.isEmpty(newUserPwd)) {
			 setPromptMessage(data, "-1", "新密码为空");
			 return data;
		}
		if(newUserPwd.equals(oldUserPwd)) {
			 setPromptMessage(data, "-2", "新密码不能等于旧密码");
			 return data;
		}
		String extUserId = param.getString("extUserId");
		param.put("userId", extUserId);
		log.info("oauth updatePwd ...begin");
		if(StringUtils.isEmpty(extUserId) || StringUtils.isEmpty(extUserId)) {
			 setPromptMessage(data, "-3", "token为空");
			 return data;
			
		}
		//MD5加密
		newUserPwd=Utils.md5(newUserPwd);
		oldUserPwd=Utils.md5(oldUserPwd);
		param.put("newUserPwd", newUserPwd);
		param.put("oldUserPwd", oldUserPwd);
		log.info("oauth url:"+updatePwdUrl+" clientId:"+clientId+" secret:"+secretId+" param:"+param.toJSONString());
		JSONObject returnObj= HttpClientToken.callHttpRemoteInterfacePost(updatePwdUrl, clientId,secretId, param);
		if(returnObj==null) {
			setPromptMessage(data, "-4", "调用基础数据接口[modifyPwd]失败");
			 return data;
		}
		JSONObject serverResult = (JSONObject) returnObj.get("serverResult");
       String resultCode = serverResult.getString("resultCode");
       log.info("[jycloud]updatePwd resultCode of returnObj:"+resultCode );
       if("200".equals(resultCode)){
    	   setPromptMessage(data, "1", "更新成功");
   		   return data;
      	 
       }else if("10001".equals(resultCode)) {
    	   setPromptMessage(data, "-5", "原始密码错误");
			return data;
       }else {
    	   setPromptMessage(data, "-4", "[modifyPwd]获取基础平台接口身份信息失败");
			return data;
       }  

    }
	/**
	 * 获取手机验证码
	 * @param param
	 * @return
	 */
 	@RequestMapping("/getPhoneValidate")
    @ResponseBody
    public JSONObject getPhoneValidate(@RequestBody JSONObject param){
 		JSONObject rs = new JSONObject();
 		//String getPhoneValidate = rb.getString("getPhoneValidate");
		String phone = param.getString("phone");
		//String clientId = rb.getString("clientId");
	    //String secretId = rb.getString("secretId");
	    JSONObject remoteJSON = new JSONObject();
	     remoteJSON.put("receiver", phone);
		 JSONObject info = HttpClientToken.callHttpRemoteInterfacePost(getPhoneValidate,clientId,secretId,remoteJSON);
		 log.info("wechat getPhoneValidate...info:"+info+ "  getPhoneValidate:"+getPhoneValidate+"  remoteJSON:"+remoteJSON.toJSONString());
		    
		 if(info==null){
			setPromptMessage(rs, "-3", "[sendVerifyCode]获取基础平台接口身份信息失败");
			return rs;
		 }
		 JSONObject serverResult = (JSONObject) info.get("serverResult");
		 if(serverResult==null){
			setPromptMessage(rs, "-3", "[sendVerifyCode]获取基础平台接口身份信息失败");
			return rs;
		 }
         String resultCode = serverResult.getString("resultCode");
         String resultMsg= serverResult.getString("resultMsg");
         log.info("[jycloud] resultCode of info:"+resultCode +" resultMsg:"+resultMsg);
         if("200".equals(resultCode)){
        	setPromptMessage(rs, "1", "获取成功");
 			return rs;
         }else if ("10001".equals(resultCode)){
        	 setPromptMessage(rs, "-1", "手机号格式错误！");
	 			return rs;
         }else if ("10000".equals(resultCode)){
        	 setPromptMessage(rs, "-1", "手机号不能为空！");
	 			return rs;
         }else {
        	setPromptMessage(rs, "-1", resultMsg);
        	return rs;
         }

    }
 	
 	/**
	 * 获取手机/邮箱验证码
	 * @param param
	 * @return
	 */
 	@RequestMapping("/getValidate")
    @ResponseBody
    public JSONObject getValidate(@RequestBody JSONObject param){
 		JSONObject rs = new JSONObject();
 		//String getPhoneValidate = rb.getString("getPhoneValidate");
		String receiver = param.getString("receiver");;
		if(StringUtils.isEmpty(receiver)) {
			setPromptMessage(rs, "-1", "手机号码或邮箱不能为空");
			return rs;
		}
	    JSONObject remoteJSON = new JSONObject();
	    remoteJSON.put("receiver", receiver);
	    JSONObject info = HttpClientToken.callHttpRemoteInterfacePost(getPhoneValidate,clientId,secretId,remoteJSON);
	    log.info("wechat getValidate...info:"+info+ "  getPhoneValidate:"+getPhoneValidate+"  remoteJSON:"+remoteJSON.toJSONString());
		    
	    if(info==null){
	    	setPromptMessage(rs, "-3", "[sendVerifyCode]获取基础平台接口身份信息失败");
			return rs;
	    }
	    JSONObject serverResult = (JSONObject) info.get("serverResult");
	    if(serverResult==null){
			setPromptMessage(rs, "-3", "[sendVerifyCode]获取基础平台接口身份信息失败");
			return rs;
	    }
	    String resultCode = serverResult.getString("resultCode");
	    String resultMsg= serverResult.getString("resultMsg");
	    log.info("[jycloud] resultCode of info:"+resultCode +" resultMsg:"+resultMsg);
	    if("200".equals(resultCode)){
        	setPromptMessage(rs, "1", "获取成功");
 			return rs;
	    }else if ("10001".equals(resultCode)){
	    	setPromptMessage(rs, "-1", "手机号格式错误！");
	    	return rs;
	    }else if ("10000".equals(resultCode)){
	    	setPromptMessage(rs, "-1", "手机号不能为空！");
	    	return rs;
	    }else {
        	setPromptMessage(rs, "-1", resultMsg);
        	return rs;
	    }
    }
 	
 	@RequestMapping("/judgeValidate")
    @ResponseBody
    public JSONObject judgeValidate(@RequestBody JSONObject param){
 		JSONObject rs = new JSONObject();
 		//String judgeValidate = rb.getString("judgeValidate");
		String phone = param.getString("phone");
		String validateString = param.getString("validateString");
		//String clientId = rb.getString("clientId");
	    //String secretId = rb.getString("secretId");
	    JSONObject remoteJSON = new JSONObject();
	    remoteJSON.put("receiver", phone);
	    remoteJSON.put("verifyCode", validateString);
	    JSONObject info = HttpClientToken.callHttpRemoteInterfacePost(judgeValidate,clientId,secretId,remoteJSON);
	    log.info("wechat judgeValidate...info:"+info+ "  judgeValidate:"+judgeValidate+"  remoteJSON:"+remoteJSON.toJSONString());
		    
	    if(info==null){
	    	setPromptMessage(rs, "-3", "[checkVerifyCode]获取基础平台接口身份信息失败");
			return rs;
	    }
	    JSONObject serverResult = (JSONObject) info.get("serverResult");
	    if(serverResult==null){
			setPromptMessage(rs, "-3", "[checkVerifyCode]获取基础平台接口身份信息失败");
			return rs;
	    }
	    String resultCode = serverResult.getString("resultCode");
	    String resultMsg= serverResult.getString("resultMsg");
	    log.info("[jycloud] resultCode of info:"+resultCode +" resultMsg:"+resultMsg);
	    if("200".equals(resultCode)){
        	setPromptMessage(rs, "1", "验证码正确");
 			return rs;
	    }else if("10000".equals(resultCode)){
	    	setPromptMessage(rs, "-1", "手机号或验证码不能为空");
	    	return rs;
	    }else if ("10001".equals(resultCode)){
	    	setPromptMessage(rs, "-1", "手机号格式错误！");
	    	return rs;
	    }else if ("20001".equals(resultCode)){ //需要基础平台改
	    	setPromptMessage(rs, "-1", "验证码错误！");
	    	return rs;
	    }else if("20002".equals(resultCode)){//未知验证码或者验证码已失效！ //需要基础平台改
	    	setPromptMessage(rs, "-1", "验证码失效！");
	    	return rs;
	    }else {
        	setPromptMessage(rs, "-1",resultMsg);
        	return rs;
	    }
    }
 	
 	/**
 	 * 判断验证码是否正确
 	 * @param param
 	 * @return
 	 */
 	@RequestMapping("/checkValidate")
    @ResponseBody
    public JSONObject checkValidate(@RequestBody JSONObject param){
 		JSONObject rs = new JSONObject();
 		JSONObject info = HttpClientToken.callHttpRemoteInterfacePost(judgeValidate,clientId,secretId,param);
 		log.info("wechat judgeValidate...info:"+info+ "  judgeValidate:"+judgeValidate+"  remoteJSON:"+param.toJSONString());
		    
 		if(info==null){
 			setPromptMessage(rs, "-3", "[checkVerifyCode]获取基础平台接口身份信息失败");
			return rs;
 		}
 		JSONObject serverResult = (JSONObject) info.get("serverResult");
 		if(serverResult==null){
			setPromptMessage(rs, "-3", "[checkVerifyCode]获取基础平台接口身份信息失败");
			return rs;
 		}
 		String resultCode = serverResult.getString("resultCode");
 		String resultMsg= serverResult.getString("resultMsg");
 		log.info("[jycloud] resultCode of info:"+resultCode +" resultMsg:"+resultMsg);
 		if("200".equals(resultCode)){
        	setPromptMessage(rs, "1", "验证码正确");
 			return rs;
 		}else if("10000".equals(resultCode)){
 			setPromptMessage(rs, "-1", "手机号或验证码不能为空");
 			return rs;
 		}else if ("10001".equals(resultCode)){
 			setPromptMessage(rs, "-1", "手机号格式错误！");
 			return rs;
 		}else if ("20001".equals(resultCode)){ //需要基础平台改
 			setPromptMessage(rs, "-1", "验证码错误！");
 			return rs;
 		}else if("20002".equals(resultCode)){//未知验证码或者验证码已失效！ //需要基础平台改
 			setPromptMessage(rs, "-1", "验证码失效！");
 			return rs;
 		}else {
 			setPromptMessage(rs, "-1",resultMsg);
        	return rs;
 		}
    }
 	
 	
	@RequestMapping("/updatePhoneByValidate")
    @ResponseBody
    public JSONObject updatePhoneByValidate(@RequestBody JSONObject param){
		JSONObject rs = new JSONObject();
 		//String updatePhoneByValidate = rb.getString("updatePhoneByValidate");
		String phone = param.getString("phone");
		String validateString = param.getString("validateString");
		String extUserId = param.getString("extUserId");
		//String clientId = rb.getString("clientId");
	    //String secretId = rb.getString("secretId");
	    JSONObject remoteJSON = new JSONObject();
	     remoteJSON.put("receiver", phone);
	     remoteJSON.put("verifyCode", validateString);
	     remoteJSON.put("userId", extUserId);
		 JSONObject info = HttpClientToken.callHttpRemoteInterfacePost(updatePhoneByValidate,clientId,secretId,remoteJSON);
		 log.info("wechat updatePhone...info:"+info+ "  updatePhoneByValidate:"+updatePhoneByValidate+"  remoteJSON:"+remoteJSON.toJSONString());
		    
		 if(info==null){
			setPromptMessage(rs, "-3", "[modifyMobileByVerifyCode]获取基础平台接口身份信息失败");
			return rs;
		 }
		 JSONObject serverResult = (JSONObject) info.get("serverResult");
		 if(serverResult==null){
			setPromptMessage(rs, "-3", "[modifyMobileByVerifyCode]获取基础平台接口身份信息失败");
			return rs;
		 }
         String resultCode = serverResult.getString("resultCode");
         String resultMsg= serverResult.getString("resultMsg");
         log.info("[jycloud] resultCode of info:"+resultCode +" resultMsg:"+resultMsg);
         if("200".equals(resultCode)){
        	setPromptMessage(rs, "1", "修改成功");
 			return rs;
         }else {
        	setPromptMessage(rs, "-1", resultMsg);
        	return rs;
         }
    }
	@RequestMapping("/updateUserPhoto")
    @ResponseBody
    public  JSONObject updateUserPhoto(@RequestBody JSONObject param){
		JSONObject  rs = new JSONObject();
		//String clientId = rb.getString("clientId");
	    //String secretId = rb.getString("secretId");
		String extUserId = param.getString("extUserId");
		Object userPhoto = param.get("userPhoto");
		//String  updateUserPhoto = rb.getString("updateUserPhoto");
		 JSONObject remoteJSON = new JSONObject();
	     remoteJSON.put("userId", extUserId);
	     remoteJSON.put("userPhotoData", userPhoto);
	     log.info("updateUserPhoto..param..userPhotoData:"+userPhoto.toString());
		 JSONObject info = HttpClientToken.callHttpRemoteInterfacePost(updateUserPhoto,clientId,secretId,remoteJSON);
		 log.info("wechat updateUserPhoto...info:"+info+ "  updateUserPhoto:"+updateUserPhoto+"  clientId:"+clientId+" secretId:"+secretId);
		 log.info("updateUserPhoto..param..userId:"+extUserId);
		 if(info==null){
			setPromptMessage(rs, "-3", "[uploadUserPhoto]获取基础平台接口身份信息失败");
			return rs;
		 }
		 JSONObject serverResult = (JSONObject) info.get("serverResult");
		 if(serverResult==null){
			setPromptMessage(rs, "-3", "[uploadUserPhoto]获取基础平台接口身份信息失败");
			return rs;
		 }
		 String resultCode = serverResult.getString("resultCode");
         String resultMsg= serverResult.getString("resultMsg");
         log.info("[jycloud] resultCode of info:"+resultCode +" resultMsg:"+resultMsg);
         if("200".equals(resultCode)){
        	setPromptMessage(rs, "1", "修改成功");
 			return rs;
         }else {
        	setPromptMessage(rs, "-1", resultMsg);
        	return rs;
         }
    }
	
	/**
	 * 获取用户信息
	 * @param param
	 * @return
	 */
	@RequestMapping("/getUserInfo")
    @ResponseBody
    public  JSONObject getUserInfo(@RequestBody JSONObject param){
		JSONObject  rs = new JSONObject();
		String extUserId = param.getString("extUserId");
		if(StringUtils.isEmpty(extUserId)) {
			setPromptMessage(rs, "-1", "extUserId不能为空");
			return rs;
		}
		JSONObject info = HttpClientToken.callHttpRemoteInterface(getUserByUserId + extUserId,clientId,secretId);
		rs = parseResult("getUserInfo",info,rs);
		if(rs.getInteger("status") == 1) {
			//成功
			JSONObject entity = info.getJSONObject("responseEntity");
			rs.put("data", entity);
		}
		return rs;
	}
	
	/**
	 * 修改用户信息
	 * @param param
	 * @return
	 */
	@RequestMapping("/updatetUserInfo")
    @ResponseBody
    public  JSONObject updatetUserInfo(@RequestBody JSONObject param){
		JSONObject  rs = new JSONObject();
		JSONObject info = HttpClientToken.callHttpRemoteInterfacePost(modifyUser,clientId,secretId,param);
		rs = parseResult("updatetUserInfo",info,rs);
		return rs;
	}
	
	/**
	 * 判断手机号码是否绑定其它账号
	 * @param param
	 * @return
	 */
	@RequestMapping("/checkPhoneOccupy")
    @ResponseBody
    public  JSONObject checkPhoneOccupy(@RequestBody JSONObject param){
		JSONObject  rs = new JSONObject();
		String extUserId = param.getString("extUserId");
		if(StringUtils.isEmpty(extUserId)) {
			setPromptMessage(rs, "-1", "extUserId不能为空");
			return rs;
		}
		
		JSONObject info = HttpClientToken.callHttpRemoteInterfacePost(getUsersByCondition,clientId,secretId,param);
		rs = parseResult("checkPhoneOccupy",info,rs);
		if(rs.getInteger("status") == 1) {
			//成功
			JSONObject data = new JSONObject();
			data.put("occupy", 0);
			JSONObject pageInfo = info.getJSONObject("pageInfo");
			if(pageInfo != null) {
				JSONArray list = pageInfo.getJSONArray("list");
				if(list != null && list.size() > 0) {
					JSONObject entity = list.getJSONObject(0);
					if(entity != null) {
						JSONObject user = entity.getJSONObject("user");
						if(user != null) {
							String userId = user.getString("userId");
							if(!extUserId.equals(userId)) {
								//不是自己，该号码被占用
								data.put("occupy", 1);
							}
						}
					}
				}
			}
			rs.put("data", data);
		}
		return rs;
	}
	
	/**
	 * 通过验证码修改邮箱
	 * @param param
	 * @return
	 */
	@RequestMapping("/updateEMailByValidate")
    @ResponseBody
    public  JSONObject updateEMailByValidate(@RequestBody JSONObject param){
		JSONObject  rs = new JSONObject();
		JSONObject info = HttpClientToken.callHttpRemoteInterfacePost(modifyEmailByVerifyCode,clientId,secretId,param);
		rs = parseResult("updateEMailByValidate",info,rs);
		return rs;
	}
	
	/**
	 * 通过验证码修改密码
	 * @param param
	 * @return
	 */
	@RequestMapping("/updatePasswordByValidate")
    @ResponseBody
    public  JSONObject updatePasswordByValidate(@RequestBody JSONObject param){
		JSONObject  rs = new JSONObject();
		JSONObject info = HttpClientToken.callHttpRemoteInterfacePost(modifyPwdByVerifyCode,clientId,secretId,param);
		rs = parseResult("updatePasswordByValidate",info,rs);
		return rs;
	}
}
