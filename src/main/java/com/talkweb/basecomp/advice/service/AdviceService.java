package com.talkweb.basecomp.advice.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

public interface AdviceService {

	int addAdvice(JSONObject param)throws Exception;

	int addAdviceDialogue(JSONObject param)throws Exception;

	JSONObject updateGetAdviceDetail(JSONObject param)throws Exception;

	int updateIgnoreAdviceState(JSONObject param)throws Exception;

	List<JSONObject> getAdviceList(JSONObject param)throws Exception;

	int getUnreadAdviceCount(JSONObject param) throws Exception;

}
