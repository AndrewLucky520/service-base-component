package com.talkweb.basecomp.payorder.action;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.common.util.HttpClientToken;

/**
 * 学校配置服务
 */
@Controller
@RequestMapping("/school/config")
public class SchoolConfigAction extends BaseAction{
	@Value("${payorder.school.config.addSchoolGoods}")
	private String addSchoolGoodsUrl;
	
	@Value("${payorder.school.config.modifySchoolGoods}")
	private String modifySchoolGoodsUrl;
	
	@Value("${payorder.school.config.offLoadingSchoolGoods}")
	private String offLoadingSchoolGoodsUrl;
	
	@Value("${payorder.school.config.querySchoolCfg}")
	private String querySchoolCfgUrl;
	
	@Value("${payorder.school.config.querySchoolCfgProducts}")
	private String querySchoolCfgProductsUrl;
	
	
	/**
	 * 添加学校商品配置
	 * @param param
	 * @return
	 */
	@RequestMapping("/addSchoolGoods")
    @ResponseBody
	public JSONObject addSchoolGoods(@RequestBody JSONObject param) {
		printParam("addSchoolGoods",param);
		return HttpClientToken.callHttpRemoteInterfacePost(addSchoolGoodsUrl, clientId,secretId, param);
	}
	
	/**
	 * 修改学校配置
	 * @param param
	 * @return
	 */
	@RequestMapping("/modifySchoolGoods")
    @ResponseBody
	public JSONObject modifySchoolGoods(@RequestBody JSONObject param) {
		printParam("modifySchoolGoods",param);
		return HttpClientToken.callHttpRemoteInterfacePost(modifySchoolGoodsUrl, clientId,secretId, param);
	}
	
	/**
	 * 下架学校商品
	 * @param param
	 * @return
	 */
	@RequestMapping("/offLoadingSchoolGoods")
    @ResponseBody
	public JSONObject offLoadingSchoolGoods(@RequestBody JSONObject param) {
		printParam("offLoadingSchoolGoods",param);
		return HttpClientToken.callHttpRemoteInterfacePostForm(offLoadingSchoolGoodsUrl, clientId,secretId, param);
	}
	
	/**
	 * 学校配置页面查询
	 * @param param
	 * @return
	 */
	@RequestMapping("/querySchoolCfg")
    @ResponseBody
	public JSONObject querySchoolCfg(@RequestBody JSONObject param) {
		printParam("querySchoolCfg",param);
		return HttpClientToken.callHttpRemoteInterfacePost(querySchoolCfgUrl, clientId,secretId, param);
	}
	
	@RequestMapping("/querySchoolCfgProducts")
    @ResponseBody
	public JSONObject querySchoolCfgProducts(@RequestBody JSONObject param) {
		printParam("querySchoolCfgProducts",param);
		return HttpClientToken.callHttpRemoteInterfacePost(querySchoolCfgProductsUrl, clientId,secretId, param);
	}
}
