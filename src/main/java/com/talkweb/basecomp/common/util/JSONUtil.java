package com.talkweb.basecomp.common.util;

import java.util.List;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSONObject;

public class JSONUtil {
	public static JSONObject getProgressStatus(int code, int progress, String msg) {
		JSONObject obj = new JSONObject();
		JSONObject data = new JSONObject();
		obj.put("code", code);
		obj.put("data", data);
		data.put("progress", progress);
		data.put("msg", msg);
		return obj;
	}

	public static JSONObject getResponse() {
		return getResponse(null, 1, null, null);
	}

	public static JSONObject getResponse(int code) {
		return getResponse(null, code, null, null);
	}

	public static JSONObject getResponse(Object param, String msg, Logger log) {
		log.info("{} 参数:{}", msg, param);
		return getResponse(param, 0, msg, null);
	}

	public static JSONObject getResponse(Object param, String msg, Exception ex, Logger log) {
		log.error("{} 参数:{}",msg, param, ex);
		return getResponse(null, 0, msg, null);
	}

	public static JSONObject getResponse(JSONObject data) {
		return getResponse(null, 1, null, data);
	}

	public static JSONObject getResponse(int code, JSONObject data) {
		return getResponse(null, code, null, data);
	}

	public static JSONObject getResponse(List<JSONObject> data) {
		return getResponse(null, 1, null, data);
	}

	public static JSONObject getResponse(int code, List<JSONObject> data) {
		return getResponse(null, code, null, data);
	}

	public static JSONObject getResponse(int code, String msg) {
		return getResponse(null, code, msg, null);
	}

	public static JSONObject getResponse(Object param, int code, String msg, Object data) {
		JSONObject ret = new JSONObject();
		ret.put("code", code);
		if (msg != null) {
			ret.put("msg", msg);
		}
		if (data != null) {
			ret.put("data", data);
		}
		return ret;
	}
}
