package com.talkweb.basecomp.payorder.action;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.common.util.HttpClientToken;

/**
 * 商品管理服务
 */
@Controller
@RequestMapping("/goods/manager")
public class GoodsManagerAction extends BaseAction{
	@Value("${payorder.goods.manager.queryGoodsCfg}")
	private String queryGoodsCfgUrl;
	
	@Value("${payorder.goods.manager.queryGoodsList}")
	private String queryGoodsListUrl;
	
	@Value("${payorder.goods.manager.queryOperaLog}")
	private String queryOperaLogUrl;
	
	@Value("${payorder.goods.manager.saveGoodsInfo}")
	private String saveGoodsInfoUrl;
	
	@Value("${payorder.goods.manager.updateGoodsInfo}")
	private String updateGoodsInfoUrl;
	
	/**
	 * 商品配置的学校页面查询
	 * @param param
	 * @return
	 */
	@RequestMapping("/queryGoodsCfg")
    @ResponseBody
	JSONObject queryGoodsCfg(@RequestBody JSONObject param) {
		printParam("queryGoodsCfg",param);
		return HttpClientToken.callHttpRemoteInterfacePost(queryGoodsCfgUrl, clientId,secretId, param);
	}
	
	/**
	 * 商品信息查询
	 * @param param
	 * @return
	 */
	@RequestMapping("/queryGoodsList")
    @ResponseBody
	JSONObject queryGoodsList(@RequestBody JSONObject param) {
		printParam("queryGoodsList",param);
		return HttpClientToken.callHttpRemoteInterfacePost(queryGoodsListUrl, clientId,secretId, param);
	}
	
	/**
	 * 查询管理员操作日志
	 * @param param
	 * @return
	 */
	@RequestMapping("/queryOperaLog")
    @ResponseBody
	JSONObject queryOperaLog(@RequestBody JSONObject param) {
		printParam("queryGoodsList",param);
		return HttpClientToken.callHttpRemoteInterfacePost(queryOperaLogUrl, clientId,secretId, param);
	}
	
	/**
	 * 保存商品信息
	 * @param param
	 * @return
	 */
	@RequestMapping("/saveGoodsInfo")
    @ResponseBody
	JSONObject saveGoodsInfo(@RequestBody JSONObject param) {
		printParam("saveGoodsInfo",param);
		return HttpClientToken.callHttpRemoteInterfacePost(saveGoodsInfoUrl, clientId,secretId, param);
	}
	
	/**
	 * 修改商品信息
	 * @param param
	 * @return
	 */
	@RequestMapping("/updateGoodsInfo")
    @ResponseBody
	JSONObject updateGoodsInfo(@RequestBody JSONObject param) {
		printParam("updateGoodsInfo",param);
		return HttpClientToken.callHttpRemoteInterfacePost(updateGoodsInfoUrl, clientId,secretId, param);
	}
}
