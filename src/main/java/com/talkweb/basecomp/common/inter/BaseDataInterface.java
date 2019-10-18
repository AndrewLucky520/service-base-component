package com.talkweb.basecomp.common.inter;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;

@FeignClient(value = "SERVICE-BASEDATA")
public interface BaseDataInterface {
	/**
	 * 获取当前学年学期信息
	 */
	 @RequestMapping(value = "/BaseDataService/getCurrentTermInfoId", method = RequestMethod.GET)
	 JSONObject getCurrentTermInfoId();
}
	 