package com.talkweb.basecomp.advice.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.advice.dao.AdviceDao;
import com.talkweb.basecomp.advice.service.AdviceService;



@Service
public class AdviceServiceImpl implements AdviceService {

	@Autowired
	private AdviceDao adviceDao;

	@Override
	public int addAdvice(JSONObject param) throws Exception {
		String adviceContent = param.getString("adviceContent");
		if(StringUtils.isBlank(adviceContent)) {
			return -1;
		}
		String accountId = param.getString("accountId");
		if(StringUtils.isBlank(accountId)) {
			return -2;
		}
		String adviceType = param.getString("adviceType");
		if(StringUtils.isBlank(adviceType)) {
			return -3;
		}
		String schoolId = param.getString("schoolId");
		if(StringUtils.isBlank(schoolId)) {
			return -4;
		}
		param.put("adviceId", UUID.randomUUID().toString());
		return adviceDao.addAdvice(param);
	}

	@Override
	public int addAdviceDialogue(JSONObject param) throws Exception {
		String adviceDialogue = param.getString("adviceDialogue");
		if(StringUtils.isBlank(adviceDialogue)) {
			return -1;
		}
		String accountId = param.getString("accountId");
		if(StringUtils.isBlank(accountId)) {
			return -2;
		}
		String schoolId = param.getString("schoolId");
		if(StringUtils.isBlank(schoolId)) {
			return -3;
		}
		String adviceId = param.getString("adviceId");
		if(StringUtils.isBlank(adviceId)) {
			return -4;
		}
		//获取当前反馈所属人
		JSONObject adviceInfo = adviceDao.getAdviceInfo(param);
		String advicePerson = adviceInfo.getString("advicePerson");
		
		if(accountId.equals(advicePerson)) {//用户方
			param.put("adviceDialogueType", 0); 
			//用户方已回复
			JSONObject json = new JSONObject();
			json.put("adviceId", adviceId);
			json.put("adviceState", 1);
			adviceDao.updateAdviceState(json);
			//平台方未回复
			json.clear();
			json.put("adviceId", adviceId);
			json.put("advicePlateState",0);
			adviceDao.updateAdvicePlaceState(json);
		}else {
			param.put("adviceDialogueType", 1);//平台方
			//平台方已回复
			JSONObject json = new JSONObject();
			json.put("adviceId", adviceId);
			json.put("advicePlateState", 1);
			adviceDao.updateAdvicePlaceState(json);
			//用户未读
			json.clear();
			json.put("adviceId", adviceId);
			json.put("adviceIsRead", 0);
			adviceDao.updateAdviceIsRead(json);
			//用户方未回复
			json.clear();
			json.put("adviceId", adviceId);
			json.put("adviceState", 0);
			adviceDao.updateAdviceState(json);
		}
		//新增对话内容
		param.put("dialogueId", UUID.randomUUID().toString());
		adviceDao.addAdviceDialogue(param);
		return  1;
	}

	@Override
	public JSONObject updateGetAdviceDetail(JSONObject param) throws Exception {
		JSONObject returnObj = new JSONObject();
		String accountId = param.getString("accountId");
		if(StringUtils.isBlank(accountId)) {
			return (JSONObject) returnObj.put("num", -1);
		}
		String schoolId = param.getString("schoolId");
		if(StringUtils.isBlank(schoolId)) {
			return (JSONObject) returnObj.put("num", -2);
		}
		String adviceId = param.getString("adviceId");
		if(StringUtils.isBlank(adviceId)) {
			return (JSONObject) returnObj.put("num", -3);
		}
		//查询反馈表
		JSONObject adviceInfo = adviceDao.getAdviceInfo(param);
		String advicePerson = adviceInfo.getString("advicePerson");
		String adviceIsRead = adviceInfo.getString("adviceIsRead");
		String adviceType  = adviceInfo.getString("adviceType");
		String adviceContent= adviceInfo.getString("adviceContent");
		switch (adviceType) {
			case "0":adviceType="平台优化建议"; break;
		    case "1":adviceType="使用问题 "; break;
		    case "2":adviceType="加盟合作"; break;
		    case "3":adviceType="其他"; break;
		}
		returnObj.put("adviceType", adviceType);
		returnObj.put("adviceContent", adviceContent);
		//查询回复表
		List<JSONObject> dialogues = adviceDao.getAdviceDialogueInfos(param);
		List<JSONObject> dialoguesReturn = new ArrayList<JSONObject>();
		returnObj.put("adviceDialogues", dialoguesReturn);
		if(dialogues!=null) {
			for(JSONObject dialogue:dialogues) {
				JSONObject dialogueObj = new JSONObject();
				dialogueObj.put("adviceDialogue", dialogue.getString("adviceDialogue"));
				dialogueObj.put("adviceDialogueType", dialogue.getString("adviceDialogueType"));
				dialoguesReturn.add(dialogueObj);
			}
		}
		
		//如果是用户方查看则修改已读状态且当前状态为未读
		if(accountId.equals(advicePerson) && "0".equals(adviceIsRead)) {//用户方
			JSONObject json = new JSONObject();
			json.put("adviceId", adviceId);
			json.put("adviceIsRead", 1);
			adviceDao.updateAdviceIsRead(json);
		} 
		returnObj.put("num", 1);
		return returnObj;
	}

	@Override
	public int updateIgnoreAdviceState(JSONObject param) throws Exception {
		String adviceId = param.getString("adviceId");
		if(StringUtils.isBlank(adviceId)) {
			return -1;
		}
		//更新状态为忽略
		//平台方忽略
		JSONObject json = new JSONObject();
		json.put("adviceId", adviceId);
		json.put("advicePlateState", 2);
		adviceDao.updateAdvicePlaceState(json);
		return 1;
	}

	@Override
	public int getUnreadAdviceCount(JSONObject param) throws Exception {
		Long accountId = param.getLong("accountId");
		if (accountId == null) {
			return -1;
		}
		Integer cc = adviceDao.getUnreadAdviceCount(accountId);
		return cc == null ? 0 : cc;
	}

	@Override
	public List<JSONObject> getAdviceList(JSONObject param) throws Exception {
		String from = param.getString("from");
		//处理时间
		String startTime = param.getString("startTime");
		String endTime = param.getString("endTime");
		if(StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime)) {
			startTime="1922-01-01";
			endTime="2099-01-01";
			param.put("startTime", startTime);
			param.put("endTime", endTime);
		}
		
		List<JSONObject> adviceList = adviceDao.getAdviceList(param);
		List<String> advicePersons = new ArrayList<String>();
		List<String> schoolIds = new ArrayList<>();
		for(JSONObject advice :adviceList) {
			String advicePerson = advice.getString("advicePerson");
			String schoolId = advice.getString("schoolId");
			advice.getString("adviceTime");
			if(!advicePersons.contains(advicePerson)) {
				advicePersons.add(advicePerson);
			}
			if(!schoolIds.contains(schoolId)) {
				schoolIds.add(schoolId);
			}
		}
		List<JSONObject> accountList = new ArrayList<JSONObject>();
		if(advicePersons.size()>0) {
			JSONObject obj = new JSONObject();
			obj.put("ids", advicePersons);
			obj.put("termInfoId", param.getString("termInfoId"));
			 accountList = adviceDao.getAccountListByIds(obj);
		}
		List<JSONObject> schoolList = new ArrayList<JSONObject>();
		if(schoolIds.size()>0) {
			JSONObject obj = new JSONObject();
			obj.put("ids", schoolIds);
			obj.put("termInfoId", param.getString("termInfoId"));
			 schoolList = adviceDao.getSchoolListByIds(obj);
		}
		Map<String,String> nameAccountIdMap = new HashMap<String,String>(); 
		Map<String,String> phoneAccountIdMap = new HashMap<String,String>(); 
		for(JSONObject accountObj:accountList) {
			String name = accountObj.getString("name");
			String accountId = accountObj.getString("id");
			String mobilePhone = accountObj.getString("mobilePhone");
			nameAccountIdMap.put(accountId, name);
			phoneAccountIdMap.put(accountId, mobilePhone);
		}
		Map<String,String> schoolMap = new HashMap<>();
		for(JSONObject school:schoolList) {
			String schoolId=  school.getString("schoolId");
			String schoolName = school.getString("schoolName");
			schoolMap.put(schoolId, schoolName);
		}
		int no = 0;
		for(JSONObject advice :adviceList) {
			String advicePerson = advice.getString("advicePerson");
			String name = nameAccountIdMap.get(advicePerson);
			if(StringUtils.isBlank(name)) {
				advice.put("advicePerson", "[已删除]");
			}else{
				advice.put("advicePerson", name);
			}
			String schoolId = advice.getString("schoolId");
			String schoolName = schoolMap.get(schoolId);
			if(StringUtils.isBlank(schoolName)) {
				advice.put("schoolName", "[已删除]");
			}else {
				advice.put("schoolName", schoolName);
			}
			String adviceType = advice.getString("adviceType");
			switch (adviceType) {
				case "0":adviceType="平台优化建议"; break;
			    case "1":adviceType="使用问题 "; break;
			    case "2":adviceType="加盟合作"; break;
			    case "3":adviceType="其他"; break;
			}
		    advice.put("adviceType", adviceType);
		    String advicePhone = advice.getString("advicePhone");
		    if(StringUtils.isBlank(advicePhone)) {
		    	String phone = phoneAccountIdMap.get(advicePerson);
		    	if(!StringUtils.isBlank(phone)) {
		    		advice.put("advicePhone",phone);
		    	}
		    }
		    Date time = advice.getDate("adviceTime");
		    if("0".equals(from)) {//h5
			    String str = (new SimpleDateFormat("yyyy-MM-dd")).format(time);
			    advice.put("adviceTime", str);
		    }else {//pc 后台
			    String str = (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(time);
			    advice.put("adviceTime", str);
		    }
		    no++;
		    advice.put("adviceNo", no);
		 }
		return adviceList;
	}

}
