package com.talkweb.basecomp.payorder.action;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.common.util.HttpClientToken;

@Controller
@RequestMapping("/user")
public class UserAction extends BaseAction{
	@Value("${payorder.user.getOpenIdByUserIds}")
	private String getOpenIdByUserIdsUrl;
	
	/**
	 * 根据userId查询微信openId
	 * @param param
	 * @return
	 */
	@RequestMapping("/getOpenIdByUserIds")
    @ResponseBody
	JSONObject getOpenIdByUserIds(@RequestBody JSONObject param) {
		printParam("getOpenIdByUserIds",param);
		return HttpClientToken.callHttpRemoteInterfacePost(getOpenIdByUserIdsUrl, clientId,secretId,param);
	}
}
