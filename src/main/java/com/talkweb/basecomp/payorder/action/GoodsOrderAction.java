package com.talkweb.basecomp.payorder.action;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.common.util.HttpClientToken;

/**
 * 商品订购服务
 */
@Controller
@RequestMapping("/goods/order")
public class GoodsOrderAction extends BaseAction{
	@Value("${payorder.goods.order.getStudentList}")
	private String getStudentListUrl;
	
	@Value("${payorder.goods.order.getStudentAndAccountByAccountId}")
	private String getStudentAndAccountByAccountIdUrl;
	
	@Value("${payorder.goods.order.checkSchoolNeedPay}")
	private String checkSchoolNeedPayUrl;
	
	@Value("${payorder.goods.order.checkUserProduct}")
	private String checkUserProductUrl;
	
	@Value("${payorder.goods.order.queryOrdersByAccount}")
	private String queryOrdersByAccountUrl;
	
	@Value("${payorder.goods.order.queryOrdersByCondition}")
	private String queryOrdersByConditionUrl;
	
	@Value("${payorder.goods.order.userOrderGoodsByWeiXin}")
	private String userOrderGoodsByWeiXinUrl;
	
	/**
	 * 获取付费学校学生列表
	 * @param param
	 * @return
	 */
	@RequestMapping("/getOrderStudentList")
    @ResponseBody
	public JSONObject getOrderStudentList(@RequestBody JSONObject param) {
		printParam("getOrderStudentList",param);
		int role = param.getIntValue("role");
		String accountId = param.getString("accountId");
		if(role == 0) {
			//家长
			param.put("parentId", accountId);
		}
		
		JSONArray studentList = new JSONArray();
		JSONObject response = new JSONObject();
		String resultCode = "200";
		String resultMsg = "操作成功";
		
		JSONObject data = null;
		if(role == 0) {
			//家长
			data = HttpClientToken.callHttpRemoteInterface(getStudentListUrl + accountId, clientId,secretId);
			if(!checkResult(data)) {
				return data;
			}
			if(data != null && data.get("pageInfo") != null) {
				JSONObject pageInfo = (JSONObject) data.get("pageInfo");
				if(pageInfo != null) {
					JSONArray list = pageInfo.getJSONArray("list");
					if(list != null && list.size() > 0) {
						for(int i=0; i<list.size();i++) {
							JSONObject student = list.getJSONObject(i).getJSONObject("student");
							JSONObject user = list.getJSONObject(i).getJSONObject("user");
							JSONObject orderStudent = checkOrderStudent(student,user);
							if(orderStudent != null) {
								studentList.add(orderStudent);
							}
						} 
					}
				}
			}
		} else {
			//学生
			data = HttpClientToken.callHttpRemoteInterface(getStudentAndAccountByAccountIdUrl + accountId, clientId,secretId);
			if(!checkResult(data)) {
				return data;
			}
			if(data != null && data.get("responseEntity") != null) {
				JSONObject responseEntity = data.getJSONObject("responseEntity");
				if(responseEntity != null) {
					JSONObject student = responseEntity.getJSONObject("student");
					JSONObject user = responseEntity.getJSONObject("user");
					JSONObject orderStudent = checkOrderStudent(student,user);
					if(orderStudent != null) {
						studentList.add(orderStudent);
					}
				}
			}
		}
		log.info("getOrderStudentList data : " + data);
		
		response = getResult(response,resultCode,resultMsg);
		response.put("studentList", studentList);
		return response;
	}
	
	private JSONObject checkOrderStudent(JSONObject student,JSONObject user) {
		student.put("userId", user.getString("userId"));
		student.put("userName", user.getString("userName"));
		String schoolId = student.getString("schoolId");
		JSONObject checkParam = new JSONObject();
		checkParam.put("schoolID", schoolId);
		//判断是否需要收费
		JSONObject needPayresult = checkSchoolNeedPay(checkParam);
		log.info("checkSchoolNeedPay result : " + needPayresult);
		if(checkResult(needPayresult)) {
			//成功
			String responseEntity = needPayresult.getString("responseEntity");
			log.info("needPayresult : " + needPayresult);
			if("NEEDPAY".equals(responseEntity)) {
				//需要收费
				//判断是否已付费
				checkParam.put("useAccountID", student.getString("accountId"));
				JSONObject payResult = checkUserProduct(checkParam);
				if(checkResult(payResult)) {
					//成功
					JSONObject resultEntity = payResult.getJSONObject("responseEntity");
					if("100".equals(resultEntity.getString("result_code"))) {
						//用户已订购该业务，付费成功
						student.put("isPaded", "1");
					} else {
						//701:用户业务订购关系不存在等
						student.put("isPaded", "0");
					}
					return student;
				}
			} else {
				//不需要收费，忽略该student
			}
		} 
		return null;
	}
	
	/**
	 * 检测学校是否需要收费 NEEDPAY：需要收费 NOPAY:不需要收费
	 * @param param
	 * @return
	 */
	@RequestMapping("/checkSchoolNeedPay")
    @ResponseBody
	public JSONObject checkSchoolNeedPay(@RequestBody JSONObject param) {
		printParam("checkSchoolNeedPay",param);
		return HttpClientToken.callHttpRemoteInterfacePostForm(checkSchoolNeedPayUrl, clientId,secretId, param);
	}
	
	/**
	 * 检测用户是否付费,检测用户是否有订购关系
	 * @param param
	 * @return
	 */
	@RequestMapping("/checkUserProduct")
    @ResponseBody
	public JSONObject checkUserProduct(@RequestBody JSONObject param) {
		printParam("checkUserProduct",param);
		return HttpClientToken.callHttpRemoteInterfacePostForm(checkUserProductUrl, clientId,secretId, param);
	}
	
	/**
	 * 根据使用用户身份ID查询用户商品订单
	 * @param param
	 * @return
	 */
	@RequestMapping("/queryOrdersByAccount")
    @ResponseBody
	public JSONObject queryOrdersByAccount(@RequestBody JSONObject param) {
		printParam("queryOrdersByAccount",param);
		return HttpClientToken.callHttpRemoteInterfacePostForm(queryOrdersByAccountUrl, clientId,secretId, param);
	}
	
	/**
	 * 根据条件查询订单信息
	 * @param param
	 * @return
	 */
	@RequestMapping("/queryOrdersByCondition")
    @ResponseBody
	public JSONObject queryOrdersByCondition(@RequestBody JSONObject param) {
		printParam("queryOrdersByCondition",param);
		return HttpClientToken.callHttpRemoteInterfacePost(queryOrdersByConditionUrl, clientId,secretId, param);
	}
	
	/**
	 * 用户通过微信支付订购商品
	 * @param param
	 * @return
	 */
	@RequestMapping("/userOrderGoodsByWeiXin")
    @ResponseBody
	public JSONObject userOrderGoodsByWeiXin(@RequestBody JSONObject param) {
		printParam("userOrderGoodsByWeiXin",param);
		return HttpClientToken.callHttpRemoteInterfacePostForm(userOrderGoodsByWeiXinUrl, clientId,secretId, param);
	}
}
