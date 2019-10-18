package com.talkweb.basecomp.payorder.action;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.common.util.HttpClientToken;

/**
 * 支付服务
 */
@Controller
@RequestMapping("/wxpay")
public class WxPayAction extends BaseAction {
	@Value("${payorder.wxpay.weiXinOrderClose}")
	private String weiXinOrderCloseUrl;
	
	@Value("${payorder.wxpay.weiXinOrderQuery}")
	private String weiXinOrderQueryUrl;
	
	@Value("${payorder.wxpay.weiXinPayNotify}")
	private String weiXinPayNotifyUrl;
	
	/**
	 * 微信支付订单取消
	 * @param param
	 * @return
	 */
	@RequestMapping("/weiXinOrderClose")
    @ResponseBody
	public JSONObject weiXinOrderClose(@RequestBody JSONObject param) {
		printParam("weiXinOrderClose",param);
		return HttpClientToken.callHttpRemoteInterfacePostForm(weiXinOrderCloseUrl, clientId,secretId, param);
	}
	
	/**
	 * 微信支付结果查询
	 * @param param
	 * @return
	 */
	@RequestMapping("/weiXinOrderQuery")
    @ResponseBody
	public JSONObject weiXinOrderQuery(@RequestBody JSONObject param) {
		printParam("weiXinOrderQuery",param);
		return HttpClientToken.callHttpRemoteInterfacePostForm(weiXinOrderQueryUrl, clientId,secretId, param);
	}
	
	/**
	 * 微信支付结果回调地址
	 * @param param
	 * @return
	 */
	@RequestMapping("/weiXinPayNotify")
    @ResponseBody
	public JSONObject weiXinPayNotify() {
		JSONObject param = new JSONObject();
		return HttpClientToken.callHttpRemoteInterfacePostForm(weiXinPayNotifyUrl, clientId,secretId, param);
	}
}
