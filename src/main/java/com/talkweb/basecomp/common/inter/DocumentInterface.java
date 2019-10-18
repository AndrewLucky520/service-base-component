package com.talkweb.basecomp.common.inter;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;

@FeignClient(value = "SERVICE-DOCUMENT")
public interface DocumentInterface {
	/**
	 * 压缩图片
	 */
	 @RequestMapping(value = "/documentservice/documentConvert/compress", method = RequestMethod.POST)
	 JSONObject compress(@RequestBody JSONObject param);

	 @RequestMapping(value = "/documentservice/documentConvert/convertToPNG", method = RequestMethod.POST)
	 JSONObject convertToPNG(@RequestBody JSONObject param);
}
	 