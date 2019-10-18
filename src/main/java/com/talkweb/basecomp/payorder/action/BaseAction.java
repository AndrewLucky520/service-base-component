package com.talkweb.basecomp.payorder.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSONObject;

public class BaseAction {
	@Value("${clientId}")
	public String clientId;
	@Value("${secretId}")
	public String secretId;
	
	public final Log log = LogFactory.getLog(getClass());
	
	/** ---设置提示信息--- **/
	public JSONObject getResult(JSONObject response,String resultCode, String resultMsg) {
		JSONObject serverResult = new JSONObject();
		serverResult.put("resultCode", resultCode);
		serverResult.put("resultMsg", resultMsg);
		response.put("serverResult", serverResult);
		return response;
	}
	
	/**
	 * 判断返回结果是否成功
	 * @param result
	 * @return
	 */
	public boolean checkResult(JSONObject result) {
		if(result != null && result.getJSONObject("serverResult") != null) {
			JSONObject serverResult = result.getJSONObject("serverResult");
			if("200".equals(serverResult.getString("resultCode"))) {
				return true;
			}
		}
		return false;
	}
	
	public void printParam(String methodName,JSONObject param) {
		log.info(methodName + " param: " + param);
	}
}
