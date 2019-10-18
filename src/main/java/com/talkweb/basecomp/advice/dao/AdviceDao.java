package com.talkweb.basecomp.advice.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.alibaba.fastjson.JSONObject;

public interface AdviceDao {

	int addAdvice(JSONObject param);

	int addAdviceDialogue(JSONObject param);

	JSONObject getAdviceInfo(JSONObject param);

	int updateAdviceState(JSONObject json);

	List<JSONObject> getAdviceDialogueInfos(JSONObject param);

	int updateAdvicePlaceState(JSONObject json);

	List<JSONObject> getAdviceList(JSONObject param);

	List<JSONObject> getAccountListByIds(JSONObject obj);

	int updateAdviceIsRead(JSONObject json);

	List<JSONObject> getSchoolListByIds(JSONObject obj);

	Integer getUnreadAdviceCount(@Param("accountId") Long accountId);


}
