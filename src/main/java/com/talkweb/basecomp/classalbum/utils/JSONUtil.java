package com.talkweb.basecomp.classalbum.utils;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

public class JSONUtil {
    private static final Logger log = LoggerFactory.getLogger(JSONUtil.class);

	public static JSONObject getResponseJSON(int code) {
		return getResponseJSON(code, null, null);
	}

	public static JSONObject getResponseJSON(int code, String msg) {
		return getResponseJSON(code, msg, null);
	}
	
	public static JSONObject getResponseJSON(int code, String msg, JSONObject data) {
		JSONObject ret = new JSONObject();
		ret.put("code", code);
		if (msg != null)
			ret.put("msg", msg);
		if (data != null)
			ret.put("data", data);
		if (code != 1 && msg != null) {
			log.error("服务层报错，错误信息：{}", msg);
		}
		return ret;
	}

	public static JSONObject getResponseJSON2(int code, String msg, List<JSONObject> data) {
		JSONObject ret = new JSONObject();
		ret.put("code", code);
		if (msg != null)
			ret.put("msg", msg);
		if (data != null)
			ret.put("data", data);
		if (code != 1 && msg != null) {
			log.error("服务层报错，错误信息：{}", msg);
		}
		return ret;
	}
}
