package com.talkweb.basecomp.payorder.action;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.common.util.HttpClientToken;

/**
 * 业务订购服务
 */
@Controller
@RequestMapping("/business/order")
public class BusinessOrderAction extends BaseAction{
	@Value("${payorder.business.order.getGoodsListBySchoolId}")
	private String getGoodsListBySchoolIdUrl;
	
	@Value("${payorder.business.order.getOrderStatisticsListByCondition}")
	private String getOrderStatisticsListByConditionUrl;
	
	@Value("${payorder.business.order.getStudentUserListByClassId}")
	private String getStudentUserListByClassIdUrl;
	
	@Value("${payorder.business.order.querySchoolOrdersDetails}")
	private String querySchoolOrdersDetailsUrl;
	
	@Value("${payorder.business.order.saveGoodsOrderRela}")
	private String saveGoodsOrderRelaUrl;
	
	@Value("${payorder.business.order.isNotRepeatOrderReminder}")
	private String isNotRepeatOrderReminderUrl;
	
	/**
	 * 根据学校Id查询该学校关联商品的信息
	 * @param param
	 * @return
	 */
	@RequestMapping("/getGoodsListBySchoolId")
    @ResponseBody
	public JSONObject getGoodsListBySchoolId(@RequestBody JSONObject param) {
		printParam("getGoodsListBySchoolId",param);
		return HttpClientToken.callHttpRemoteInterfacePostForm(getGoodsListBySchoolIdUrl, clientId,secretId, param);
	}
	
	/**
	 * 获取业务订购统计列表
	 * @param param
	 * @return
	 */
	@RequestMapping("/getOrderStatisticsListByCondition")
	@ResponseBody
	public JSONObject getOrderStatisticsListByCondition(@RequestBody JSONObject param) {
		printParam("getOrderStatisticsListByCondition",param);
		return HttpClientToken.callHttpRemoteInterfacePost(getOrderStatisticsListByConditionUrl, clientId,secretId, param);
	}
	
	/**
	 * 根据班级Id查询该班学生信息
	 * @param param
	 * @return
	 */
	@RequestMapping("/getStudentUserListByClassId")
	@ResponseBody
	public JSONObject getStudentUserListByClassId(@RequestBody JSONObject param) {
		printParam("getStudentUserListByClassId",param);
		return HttpClientToken.callHttpRemoteInterfacePostForm(getStudentUserListByClassIdUrl, clientId,secretId, param);
	}
	
	/**
	 * 根据学校Id,年级，班级等条件查询该学校订购信息
	 * @param param
	 * @return
	 */
	@RequestMapping("/querySchoolOrdersDetails")
	@ResponseBody
	public JSONObject querySchoolOrdersDetails(@RequestBody JSONObject param) {
		printParam("querySchoolOrdersDetails",param);
		return HttpClientToken.callHttpRemoteInterfacePost(querySchoolOrdersDetailsUrl, clientId,secretId, param);
	}
	
	/**
	 * 保存个人订购关系
	 * @param param
	 * @return
	 */
	@RequestMapping("/saveGoodsOrderRela")
	@ResponseBody
	public JSONObject saveGoodsOrderRela(@RequestBody JSONObject param) {
		printParam("saveGoodsOrderRela",param);
		return HttpClientToken.callHttpRemoteInterfacePost(saveGoodsOrderRelaUrl, clientId,secretId, param);
	}
	
	/**
	 * 根据学校Id,用户AccountId,判断是否有需要续费的商品套餐
	 * @param param
	 * @return
	 */
	@RequestMapping("/isNotRepeatOrderReminder")
	@ResponseBody
	public JSONObject isNotRepeatOrderReminder(@RequestBody JSONObject param) {
		printParam("isNotRepeatOrderReminder",param);
		return HttpClientToken.callHttpRemoteInterfacePostForm(isNotRepeatOrderReminderUrl, clientId,secretId, param);
	}
}
