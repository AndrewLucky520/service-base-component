package com.talkweb.basecomp.address.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.Constants;
import com.talkweb.basecomp.address.dao.AddressBookDao;
import com.talkweb.basecomp.common.util.JSONUtil;

/**
 * @Description 通讯录服务层接口
 * @author xixi1979
 * @date 2018/8/1
 */
@Service
public class AddressBookService {
    private static final Logger log = LoggerFactory.getLogger(AddressBookService.class);

	@Autowired
	private AddressBookDao addressBookDao;

	public JSONObject getSubMenus(JSONObject param){
		checkParameter(param);
		log.debug("getUserClassList，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_USERID))) {
			return JSONUtil.getResponse(param, "userId为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "学校代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_TERMINFO))) {
			return JSONUtil.getResponse(param, "学年学期信息为空，处理失败！", log);
		}

		try {
			List<JSONObject> ret = new ArrayList<JSONObject>();
			int type = param.getIntValue(Constants.FIELD_TYPE);
			if (type == TYPE_CURRENTCLASSSTUDENT) {//当前班级
				if (StringUtils.isEmpty(param.get(Constants.FIELD_CLASSID))) {
					return JSONUtil.getResponse(param, "班级代码不全，处理失败！", log);
				}
				ret = addressBookDao.getClass(param);
			} else if (type == TYPE_TEACHERCLASSSTUDENT) {//任教班级
				param.put(Constants.FIELD_CURRENTLEVEL, param.getLong(Constants.FIELD_ID));
				ret = addressBookDao.getTeacherClassList(param);
			} else if (type == TYPE_GRADESTUDENT) {//年级所有班级
				param.put(Constants.FIELD_CURRENTLEVEL, param.getLong(Constants.FIELD_ID));
				ret = addressBookDao.getClassList(param);
			} else if (type == TYPE_PARENTORG) {//子机构列表
				param.put(Constants.FIELD_ORGTYPE, param.get(Constants.FIELD_ID));
				ret = addressBookDao.getOrgList(param);
			} else if (type == TYPE_CLASSLEADER) {//班主任年级列表
				ret = addressBookDao.getClassLeaderGradeList(param);
			}
			return JSONUtil.getResponse(ret);
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "获取二级菜单数据异常。", ex, log);
		}
	}

	public JSONObject getStudentMenus(JSONObject param) {
		checkParameter(param);
		log.debug("getStudentTree，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_USERID))) {
			return JSONUtil.getResponse(param, "userId为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "学校代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_TERMINFO))) {
			return JSONUtil.getResponse(param, "学年学期信息为空，处理失败！", log);
		}

		boolean ispcmsg = isPCMessage(param);

		try {
			if (isSystemManager(param) 
				|| ispcmsg && addressBookDao.getMessageAdmin(param) == 1) {//系统管理员\消息管理员
				return getAllStudentTree(param, false);
			}
	
			return getRelatedStudentTree(param, false);
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "获取学生树数据异常。", ex, log);
		}
	}
	
	public JSONObject getTeacherMenus(JSONObject param) {
		checkParameter(param);
		log.debug("getOrgTypeList，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_USERID))) {
			return JSONUtil.getResponse(param, "userId为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "学校代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_TERMINFO))) {
			return JSONUtil.getResponse(param, "学年学期信息为空，处理失败！", log);
		}

		try {
			if (isStudent(param)) {//学生和家长
				return getStudentTeacherTree(param, false);
			}
			
			return getAllTeacherTree(param, false);
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "获取机构类型列表异常。", ex, log);
		}
	}

	public JSONObject getTeacherTree(JSONObject param) {
		checkParameter(param);
		log.debug("getTeacherTree，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_USERID))) {
			return JSONUtil.getResponse(param, "userId为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "学校代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_TERMINFO))) {
			return JSONUtil.getResponse(param, "学年学期信息为空，处理失败！", log);
		}

		try {
			if (isStudent(param)) {//学生和家长
				return getStudentTeacherTree(param, true);
			}
			
			return getAllTeacherTree(param, true);
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "获取老师树数据异常。", ex, log);
		}
	}

	public JSONObject getStudentTree(JSONObject param){
		checkParameter(param);
		log.debug("getStudentTree，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_USERID))) {
			return JSONUtil.getResponse(param, "userId为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "学校代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_TERMINFO))) {
			return JSONUtil.getResponse(param, "学年学期信息为空，处理失败！", log);
		}

		boolean ispcmsg = isPCMessage(param);

		try {
			if (isSystemManager(param) 
				|| ispcmsg && addressBookDao.getMessageAdmin(param) == 1) {//系统管理员\消息管理员
				return getAllStudentTree(param, true);
			}
	
			return getRelatedStudentTree(param, true);
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "获取学生树数据异常。", ex, log);
		}
	}

	public static void logtime(String msg, long time) {
		log.info("{} finished in {} ms ", msg, System.currentTimeMillis() - time);
	}
	
	public JSONObject getMemberList(JSONObject param){
		checkParameter(param);
		log.debug("getMemberList，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ACCOUNTID))) {
			return JSONUtil.getResponse(param, "用户账号代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "学校代码为空，处理失败！", log);
		}

		if (StringUtils.isEmpty(param.get(Constants.FIELD_TYPE))) {
			return JSONUtil.getResponse(param, "成员类型为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ID))) {
			return JSONUtil.getResponse(param, "成员id为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_TERMINFO))) {
			return JSONUtil.getResponse(param, "学年学期信息为空，处理失败！", log);
		}

		List<JSONObject> ret = new ArrayList<JSONObject>();
		Integer type = param.getInteger(Constants.FIELD_TYPE);
		if (type == TYPE_SUBORG) {//自定义机构或者子机构
			Long id = param.getLong(Constants.FIELD_ID);
			if (id == CLASS_TEACHER) {//任课老师
				if (StringUtils.isEmpty(param.get(Constants.FIELD_CLASSID))) {
					return JSONUtil.getResponse(param, "班级代码不全，处理失败！", log);
				}
			}
		} else {
			if (type == TYPE_CURRENTCLASSSTUDENT) {//当前班学生
				if (StringUtils.isEmpty(param.get(Constants.FIELD_CLASSID))) {
					return JSONUtil.getResponse(param, "班级代码不全，处理失败！", log);
				}
			} else if (type == TYPE_TEACHERCLASSSTUDENT) {//任教班级学生
				if (StringUtils.isEmpty(param.get(Constants.FIELD_USERID))) {
					return JSONUtil.getResponse(param, "用户代码为空，处理失败！", log);
				}
			}
		}

		try {
			ret = getMemberListInternal(param);
			return JSONUtil.getResponse(ret);
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "获取成员列表异常。", ex, log);
		}
	}

	public JSONObject getParentList(JSONObject param) {
		log.debug("getParentList，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "学校代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_TERMINFO))) {
			return JSONUtil.getResponse(param, "学年学期信息为空，处理失败！", log);
		}

		if (param.containsKey(Constants.FIELD_CHILDREN)) {//多个查询参数需组合查询结果
			try {
				JSONArray childs = param.getJSONArray(Constants.FIELD_CHILDREN);
				if (childs != null && childs.size() > 0) {
					return getParentListInternal(param, childs);
				}
			} catch(Exception ex) {
				return JSONUtil.getResponse(param, "获取家长列表异常.", ex, log);
			}
		}
		
		return JSONUtil.getResponse();
	}

	public JSONObject findMemberList(JSONObject param){
		checkParameter(param);
		log.debug("findMemberList，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ACCOUNTID))) {
			return JSONUtil.getResponse(param, "用户账号代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_USERID))) {
			return JSONUtil.getResponse(param, "userId为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "学校代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_TERMINFO))) {
			return JSONUtil.getResponse(param, "学年学期信息为空，处理失败！", log);
		}
		
		if (!StringUtils.isEmpty(param.get(Constants.FIELD_KEY)))
			param.put(Constants.FIELD_KEY, "%" + param.get(Constants.FIELD_KEY) + "%");

		int type = param.getIntValue(Constants.FIELD_TYPE);
		List<JSONObject> ret = new ArrayList<JSONObject>();

		try {
			if (isSystemManager(param)) {//系统管理员
				if (type == 1) {//老师
					ret = addressBookDao.getTeacherList(param);
				} else {//学生
					param.remove(Constants.FIELD_USERID);
					param.remove(Constants.FIELD_CLASSID);
					ret = addressBookDao.getStudentList(param);
				}
			} else if (isStudent(param)) {//学生和家长
				if (StringUtils.isEmpty(param.get(Constants.FIELD_CLASSID))) {
					return JSONUtil.getResponse(param, "班级代码不全，处理失败！", log);
				}
				if (type == 1) {//老师
					ret = addressBookDao.getClassTeacherList(param);
				} else {//学生
					param.remove(Constants.FIELD_USERID);
					ret = addressBookDao.getStudentList(param);
				}
			} else {
				if (type == 1) {//老师
					ret = addressBookDao.getTeacherList(param);
				} else {//学生
					param.remove(Constants.FIELD_CLASSID);
					ret = addressBookDao.getStudentList(param);
				}
			}
	
			if (isAddress(param)) {//消息通知不需要是否收藏标志
				handleStoreFlag(param, ret);
			}
			
			return JSONUtil.getResponse(ret);
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "查询成员列表异常。", ex, log);
		}
	}

	public JSONObject insertMyAddressBook(JSONObject param) {
		checkParameter(param);
		log.debug("insertMyAddressBook，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ACCOUNTID))) {
			return JSONUtil.getResponse(param, "当前用户账号为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ID))) {
			return JSONUtil.getResponse(param, "被收藏用户账号为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "学校代码为空，处理失败！", log);
		}
		
		param.put(Constants.FIELD_CREATORID, param.get(Constants.FIELD_ACCOUNTID));
		param.put(Constants.FIELD_MEMBERACCID, param.get(Constants.FIELD_ID));

		List<JSONObject> aa = addressBookDao.findMyAddressBook(param);
		if (aa != null && aa.size() > 0) {
			return JSONUtil.getResponse(param, "此用户已收藏！", log);
		}
		String uuid = UUID.randomUUID().toString();
		param.put(Constants.FIELD_ID, uuid);
		int code = addressBookDao.createMyAddressBook(param);
		return JSONUtil.getResponse(code);
	}

	public JSONObject getMyAddressBook(JSONObject param){
		checkParameter(param);
		log.debug("getMyAddressBook，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ACCOUNTID))) {
			return JSONUtil.getResponse(param, "当前用户账号为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "学校代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_TERMINFO))) {
			return JSONUtil.getResponse(param, "学年学期信息为空，处理失败！", log);
		}

		if (!StringUtils.isEmpty(param.get(Constants.FIELD_KEY))) {
			param.put(Constants.FIELD_KEY, "%" + param.get(Constants.FIELD_KEY) + "%");
		}

		param.put(Constants.FIELD_CREATORID, param.get(Constants.FIELD_ACCOUNTID));
		
		List<JSONObject> ret = new ArrayList<JSONObject>();
		int type = param.getIntValue(Constants.FIELD_TYPE);

		try {
			if (type == 1) {//老师
				ret = addressBookDao.getTeacherList(param);
			} else {//学生
				param.remove(Constants.FIELD_USERID);
				param.remove(Constants.FIELD_CLASSID);
				ret = getStudentListInternal(param);
			}
			return JSONUtil.getResponse(ret);
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "获取收藏列表异常。", ex, log);
		}
	}

	public JSONObject deleteMyAddressBook(JSONObject param){
		checkParameter(param);
		log.debug("deleteMyAddressBook，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ACCOUNTID))) {
			return JSONUtil.getResponse(param, "当前用户账号为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ID))) {
			return JSONUtil.getResponse(param, "被收藏用户账号为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "学校代码为空，处理失败！", log);
		}
		param.put(Constants.FIELD_CREATORID, param.get(Constants.FIELD_ACCOUNTID));
		param.put(Constants.FIELD_MEMBERACCID, param.get(Constants.FIELD_ID));

		int code = addressBookDao.deleteMyAddressBook(param);
		return JSONUtil.getResponse(code);
	}
	
	private JSONObject getStudentTeacherTree(JSONObject param, boolean needSub) {
		boolean ispcmsg = isPCMessage(param);
		List<JSONObject> ret = new ArrayList<JSONObject>();
		if (StringUtils.isEmpty(param.get(Constants.FIELD_CLASSID))) {
			return JSONUtil.getResponse(param, "班级代码不全，处理失败！", log);
		}
		int cc = addressBookDao.getClassTeacherCount(param);
		if (!needSub || cc > 0) {
			JSONObject obj = getObject(CLASS_TEACHER, "老师(" + cc + "人)");
			if (ispcmsg) {
				addChildren(obj, copyParam(param));
			}
			ret.add(obj);
		}
		return JSONUtil.getResponse(ret);
	}
	
	private JSONObject getAllTeacherTree(JSONObject param, boolean needSub) {
		boolean ispcmsg = isPCMessage(param);
		List<JSONObject> ret = new ArrayList<JSONObject>();

		List<JSONObject> tmp2 = addressBookDao.getOrgTypeList(param);
		for (JSONObject obj : tmp2) {
			param.put(Constants.FIELD_ORGTYPE, obj.get(Constants.FIELD_ID));
			int cc = addressBookDao.getMemberCount(param);
			if (cc > 0) {
				obj.put(Constants.FIELD_LABEL, obj.get(Constants.FIELD_NAME) + "(" + cc + "人)");
				if (needSub) {
					List<JSONObject> tmp = addressBookDao.getOrgList(param);
					obj.put(Constants.FIELD_CHILDREN, tmp);
				}
				ret.add(obj);
			}
		}
		
		int cc = addressBookDao.getClassLeaderCount(param);
		JSONObject obj = null;
		if (cc > 0) {
			obj = getObject(CLASS_LEADER, "班主任(" + cc + "人)", TYPE_CLASSLEADER);
			if (needSub) {
				List<JSONObject> tmp = addressBookDao.getClassLeaderGradeList(param);
				obj.put(Constants.FIELD_CHILDREN, tmp);
			}
			ret.add(obj);
		}
		
		param.put(Constants.FIELD_ORGTYPE, GRADE_ORG);
		cc = addressBookDao.getLeaderCount(param);
		if (cc > 0) {
			obj = getObject(GRADE_LEADER, "年级组长(" + cc + "人)");
			ret.add(obj);
		}
		
		param.put(Constants.FIELD_ORGTYPE, TEACHER_ORG);
		cc = addressBookDao.getLeaderCount(param);
		if (cc > 0) {
			obj = getObject(TEACHER_LEADER, "教研组长(" + cc + "人)");
			ret.add(obj);
		}
		
		param.put(Constants.FIELD_ORGTYPE, PREPARE_ORG);
		cc = addressBookDao.getLeaderCount(param);
		if (cc > 0) {
			obj = getObject(PREPARE_LEADER, "备课组长(" + cc + "人)");
			ret.add(obj);
		}
		
		cc = addressBookDao.getTeacherCount(param);
		if (cc > 0) {
			obj = getObject(ALL_TEACHER, "所有教师(" + cc + "人)");
			ret.add(obj);
		}

		if (ispcmsg) {//PC端消息通知
			addChildren(ret, param);
		}
		return JSONUtil.getResponse(ret);
	}
	
	private JSONObject getAllStudentTree(JSONObject param, boolean needSub){
		List<JSONObject> l = addressBookDao.getGradeList(param);
		
		if (needSub) {
			boolean ispcmsg = isPCMessage(param);
			int allstudent = 0;
			List<JSONObject> ret = new ArrayList<JSONObject>();
			for (JSONObject obj : l) {
				param.put(Constants.FIELD_CURRENTLEVEL, obj.getLong(Constants.FIELD_ID));
				List<JSONObject> tmp = addressBookDao.getClassList(param);
				
				if (ispcmsg) {//PC端消息通知
					allstudent += obj.getIntValue(Constants.FIELD_RECORDCOUNT);
					addChildren(tmp, param);
				}
				obj.put(Constants.FIELD_CHILDREN, tmp);
			}
			
			if (ispcmsg && l.size() > 0) {//消息通知
				JSONObject obj = getObject(0, "所有学生("+allstudent+"人)", TYPE_SCHOOLSTUDENT);
				obj.put(Constants.FIELD_CHILDREN, l);
				ret.add(obj);
				return JSONUtil.getResponse(ret);
			}
		}
		return JSONUtil.getResponse(l);
	}
	
	private JSONObject getRelatedStudentTree(JSONObject param, boolean needSub){
		List<JSONObject> cc = null;
		int type = TYPE_TEACHERCLASSSTUDENT;
		if (isStudent(param)) {//学生和家长
			if (StringUtils.isEmpty(param.get(Constants.FIELD_CLASSID))) {
				return JSONUtil.getResponse(param, "班级代码不全，处理失败！", log);
			}
			cc = addressBookDao.getClass(param);
			type = TYPE_CURRENTCLASSSTUDENT;
		} else {
			cc = addressBookDao.getTeacherClassList(param);
		}

		if (isPCMessage(param)) {//PC端消息通知
			addChildren(cc, param);
		}
		List<JSONObject> ret = handleGradeData(cc, type, needSub);
		return JSONUtil.getResponse(ret);
	}

	List<JSONObject> getMemberListInternal(JSONObject param){
		//log.debug("getMemberListInternal，参数：{}", param);
		List<JSONObject> ret = new ArrayList<JSONObject>();
		int type = param.getIntValue(Constants.FIELD_TYPE);
		if (type == TYPE_PARENTORG) {//主机构教师
			param.put(Constants.FIELD_ORGTYPE, param.get(Constants.FIELD_ID));
			ret = addressBookDao.getMemberList(param);
		} else if (type == TYPE_SUBORG) {//自定义机构或者子机构老师
			Long id = param.getLong(Constants.FIELD_ID);
			if (id == CLASS_TEACHER) {//任课老师
				ret = addressBookDao.getClassTeacherList(param);
			} else if (id == GRADE_LEADER) {//年级组长
				param.put(Constants.FIELD_ORGTYPE, GRADE_ORG);
				ret = addressBookDao.getLeaderList(param);
			} else if (id == TEACHER_LEADER) {//教研组长
				param.put(Constants.FIELD_ORGTYPE, TEACHER_ORG);
				ret = addressBookDao.getLeaderList(param);
			} else if (id == PREPARE_LEADER) {//备课组长
				param.put(Constants.FIELD_ORGTYPE, PREPARE_ORG);
				ret = addressBookDao.getLeaderList(param);
			} else if (id == ALL_TEACHER) {//所有教师
				ret = addressBookDao.getTeacherList(param);
			} else {//子机构教师
				param.put(Constants.FIELD_ORGID, id);
				ret = addressBookDao.getMemberList(param);
			}
		} else if (type == TYPE_CLASSLEADER) {//所有班主任
			ret = addressBookDao.getClassLeaderList(param);
		} else if (type == TYPE_GRADECLASSLEADER) {//年级班主任
			param.put(Constants.FIELD_CURRENTLEVEL, param.getLong(Constants.FIELD_ID));
			ret = addressBookDao.getClassLeaderList(param);
		} else if (type == TYPE_STUDENT) {
			param.put(Constants.FIELD_STUDENTID, param.get(Constants.FIELD_ID));
			param.remove(Constants.FIELD_CLASSID);
			param.remove(Constants.FIELD_USERID);
			ret = addressBookDao.getParentList(param);
		} else {
			if (type == TYPE_CLASSSTUDENT) {//指定班级学生或家长
				param.put(Constants.FIELD_CLASSID, param.getLong(Constants.FIELD_ID));
				param.remove(Constants.FIELD_USERID);
			} else if (type == TYPE_CURRENTCLASSSTUDENT) {//当前班学生或家长
				param.remove(Constants.FIELD_USERID);
			} else if (type == TYPE_TEACHERCLASSSTUDENT) {//任教班级学生或家长
				param.put(Constants.FIELD_CURRENTLEVEL, param.getLong(Constants.FIELD_ID));
				param.remove(Constants.FIELD_CLASSID);
			} else if (type == TYPE_GRADESTUDENT) {//年级所有学生或家长
				param.put(Constants.FIELD_CURRENTLEVEL, param.getLong(Constants.FIELD_ID));
				param.remove(Constants.FIELD_CLASSID);
				param.remove(Constants.FIELD_USERID);
			} else if (type == TYPE_SCHOOLSTUDENT) {//全校学生或家长
				param.remove(Constants.FIELD_CLASSID);
				param.remove(Constants.FIELD_USERID);
			}
			ret = getStudentListInternal(param);
		}
		
		if (isAddress(param)) {//消息通知不需要是否收藏标志
			handleStoreFlag(param, ret);
		}
		
		return ret;
	}

	private List<JSONObject> getStudentListInternal(JSONObject param){
		List<JSONObject> ret = addressBookDao.getStudentList(param);
		if (isAppAddress(param)) {
			for (JSONObject obj: ret) {
				String phone = obj.getString(Constants.FIELD_MOBILEPHONE);
				if (!StringUtils.isEmpty(phone)) {
					String[] strs = phone.split(",");
					if (strs.length == 1) {
						this.getParentObject(obj, strs[0], null);
					} else if (strs.length > 1) {
						List<JSONObject> parentList = new ArrayList<JSONObject>();
						for (String ss: strs) {
							getParentObject(obj, ss, parentList);
						}
						obj.put(Constants.FIELD_PARENTLIST, parentList);
					}
				}
			}
		}
		return ret;
	}
	
	private void getParentObject(JSONObject student, String pp, List<JSONObject> parentList) {
		String[] strs = pp.split(";");
		JSONObject obj = student;
		if (parentList != null) {
			obj = new JSONObject();
			parentList.add(obj);
		}
		obj.put(Constants.FIELD_PARENTID, strs[0]);
		obj.put(Constants.FIELD_PARENTNAME, student.getString(Constants.FIELD_NAME) + strs[1]);
		obj.put(Constants.FIELD_MOBILEPHONE, strs.length > 2 ? strs[2] : "");
	}

	private JSONObject getParentListInternal(JSONObject param, JSONArray childs) {
		List<JSONObject> ret = new ArrayList<JSONObject>();
		ArrayList<String> ids = new ArrayList<String>();
		for (int i = 0; i < childs.size(); i++) {
			JSONObject obj = childs.getJSONObject(i);
			int type = obj.getIntValue(Constants.FIELD_TYPE);
			if (type == TYPE_STUDENT) {
				if (obj.containsKey(Constants.FIELD_USERID)) {
					ids.add(obj.getString(Constants.FIELD_USERID));
				} else {
					//log.warn("节点 {} 未找到用户数据，自动过滤", obj);
				}
			}
		}
		if (ids.size() > 0) {
			param.put(Constants.FIELD_STUDENTID, ids);
			ret = addressBookDao.getParentListForMessage(param);
		}
		return JSONUtil.getResponse(ret);
	}
	
	private boolean isAddress(JSONObject param) {
		int moudleType = param.getIntValue(Constants.FIELD_MOUDLETYPE);
		return (moudleType == MOUDLE_TYPE_PC_ADDRESS || moudleType == MOUDLE_TYPE_APP_ADDRESS);
	}

	private boolean isAppAddress(JSONObject param) {
		int moudleType = param.getIntValue(Constants.FIELD_MOUDLETYPE);
		return (moudleType == MOUDLE_TYPE_APP_ADDRESS);
	}

	private boolean isPCMessage(JSONObject param) {
		int moudleType = param.getIntValue(Constants.FIELD_MOUDLETYPE);
		return (moudleType == MOUDLE_TYPE_PC_MESSAGE);
	}

	private JSONObject getObject(int id, String name) {
		return getObject(id, name, TYPE_SUBORG);
	}

	private JSONObject getObject(int id, String name, int type) {
		JSONObject all = new JSONObject();
		all.put(Constants.FIELD_ID, id);
		all.put(Constants.FIELD_LABEL, name);
		all.put(Constants.FIELD_TYPE, type);
		return all;
	}
	
	private JSONObject copyObject(JSONObject obj, JSONObject src) {
		src.put(Constants.FIELD_ID, obj.get(Constants.FIELD_ID));
		src.put(Constants.FIELD_TYPE, obj.get(Constants.FIELD_TYPE));
		return src;
	}

	private JSONObject copyParam(JSONObject src) {
		JSONObject jo = new JSONObject();
		jo.put(Constants.FIELD_ACCOUNTID, src.get(Constants.FIELD_ACCOUNTID));
		jo.put(Constants.FIELD_SCHOOLID, src.get(Constants.FIELD_SCHOOLID));
		jo.put(Constants.FIELD_CLASSID, src.get(Constants.FIELD_CLASSID));
		jo.put(Constants.FIELD_USERID, src.get(Constants.FIELD_USERID));
		jo.put(Constants.FIELD_MOUDLETYPE, src.get(Constants.FIELD_MOUDLETYPE));
		jo.put(Constants.FIELD_TERMINFOYEAR, src.get(Constants.FIELD_TERMINFOYEAR));
		jo.put(Constants.FIELD_TERMINFO, src.get(Constants.FIELD_TERMINFO));
		return jo;
	}

	private void addChildren(List<JSONObject> list, JSONObject param) {
		JSONObject jo = copyParam(param);
		for (JSONObject jj: list) {
			JSONArray c = jj.getJSONArray(Constants.FIELD_CHILDREN);
			if (c != null && c.size() > 0) {
				addChildren(c.toJavaList(JSONObject.class), jo);
			}
			Integer type = jj.getInteger(Constants.FIELD_TYPE);
			if (type == TYPE_SUBORG || type == TYPE_GRADECLASSLEADER || type == TYPE_CLASSSTUDENT) {
				addChildren(jj, jo);
			}
		}
	}

	private void addChildren(JSONObject obj, JSONObject param) {
		List<JSONObject> tmp2 = this.getMemberListInternal(copyObject(obj, param));
		obj.put(Constants.FIELD_CHILDREN, tmp2);
	}

	private List<JSONObject> handleGradeData(List<JSONObject> cc, int type, boolean needSub) {
		List<JSONObject> ret = new ArrayList<JSONObject>();
		HashMap<Long,JSONObject> grades = new HashMap<Long,JSONObject>();
		for (JSONObject obj : cc) {
			Long id = obj.getLong(Constants.FIELD_CURRENTLEVEL);
			JSONObject tmp = grades.get(id);
			if (tmp == null) {
				tmp = new JSONObject();
				tmp.put(Constants.FIELD_ID, id);
				tmp.put(Constants.FIELD_NAME, obj.get(Constants.FIELD_GRADENAME));
				tmp.put(Constants.FIELD_TYPE, type);
				tmp.put(Constants.FIELD_RECORDCOUNT, 0);
				ret.add(tmp);
				grades.put(id, tmp);
			}
			if (needSub) {
				JSONArray ch = tmp.getJSONArray(Constants.FIELD_CHILDREN);
				if (ch == null) {
					ch = new JSONArray();
					tmp.put(Constants.FIELD_CHILDREN, ch);
				}
				ch.add(obj);
			}
			tmp.put(Constants.FIELD_RECORDCOUNT, obj.getIntValue(Constants.FIELD_RECORDCOUNT) + tmp.getIntValue(Constants.FIELD_RECORDCOUNT));
		}
		for (JSONObject obj : grades.values()) {
			obj.put(Constants.FIELD_LABEL, obj.get(Constants.FIELD_NAME) + "("+obj.getIntValue(Constants.FIELD_RECORDCOUNT)+"人)");
		}
		return ret;
	}
	
	private void handleStoreFlag(JSONObject param, List<JSONObject> ret) {
		param.put(Constants.FIELD_CREATORID, param.get(Constants.FIELD_ACCOUNTID));
		List<JSONObject> mybooks = addressBookDao.findMyAddressBook(param);
		if (mybooks != null && mybooks.size() > 0) {
			HashMap<Long, Long> map = new HashMap<Long, Long>();
			for (JSONObject obj : mybooks) {
				long l = obj.getLongValue(Constants.FIELD_MEMBERACCID);
				map.put(l, l);
			}
			for (JSONObject obj : ret) {
				long l = obj.getLongValue(Constants.FIELD_ID);
				if (map.containsKey(l)) {
					obj.put(Constants.FIELD_STOREFLAG, 1);
				}
			}
		}
	}
	
	private void checkParameter(JSONObject param) {
		if (StringUtils.isEmpty(param.get(Constants.FIELD_MOUDLETYPE))) {
			param.put(Constants.FIELD_MOUDLETYPE, MOUDLE_TYPE_PC_ADDRESS);
		}
		JSONObject user = param.getJSONObject(Constants.FIELD_CURRENTUSER);
		if (user != null) {//需要当前用户的用户id、账号id，如果是家长或学生，需要班级id
			param.put(Constants.FIELD_USERID, user.get(Constants.FIELD_USERID));
			param.put(Constants.FIELD_ACCOUNTID, user.get(Constants.FIELD_ACCOUNTID));
			param.put(Constants.FIELD_CLASSID, user.get(Constants.FIELD_STUDENTCLASSID));
		}
	}

	private static boolean isSystemManager(JSONObject param) {
		return Constants.ROLE_SYSTEMMANAGER.equals(param.get(Constants.FIELD_ROLE));
	}

	private static boolean isStudent(JSONObject param) {
		return Constants.ROLE_PARENT.equals(param.get(Constants.FIELD_ROLE)) 
				|| Constants.ROLE_PARENT.equals(param.get(Constants.FIELD_ROLE));
	}

	private static final int TYPE_PARENTORG = 1;//主机构老师
	private static final int TYPE_SUBORG = 2;//子机构老师
	private static final int TYPE_GRADECLASSLEADER = 3;//年级班主任
	private static final int TYPE_CLASSSTUDENT = 4;//班级学生
	private static final int TYPE_CURRENTCLASSSTUDENT = 5;//当前班学生
	private static final int TYPE_TEACHERCLASSSTUDENT = 6;//任教班学生
	private static final int TYPE_GRADESTUDENT = 7;//年级学生
	private static final int TYPE_SCHOOLSTUDENT = 8;//全校学生
	private static final int TYPE_CLASSLEADER = 9;//所有班主任
	private static final int TYPE_STUDENT = 12;//学生

	private static final int CLASS_TEACHER = -1;//任课老师
	private static final int CLASS_LEADER = -2;//班主任
	private static final int GRADE_LEADER = -3;//年级组组长
	private static final int TEACHER_LEADER = -4;//教研组组长
	private static final int PREPARE_LEADER = -5;//备课组组长
	private static final int ALL_TEACHER = -6;//所有教师

	private static final int TEACHER_ORG = 1;//教研组
	private static final int GRADE_ORG = 2;//年级组
	private static final int PREPARE_ORG = 3;//备课组

	private static final int MOUDLE_TYPE_PC_ADDRESS = 0;//PC通讯录
	private static final int MOUDLE_TYPE_APP_ADDRESS = 1;//微信通讯录
	private static final int MOUDLE_TYPE_PC_MESSAGE = 2;//
	//private static final int MOUDLE_TYPE_APP_MESSAGE = 3;//
	//private static final int MOUDLE_TYPE_WEEKLY = 4;//

	//private static final int MESSAGE_MOUDLE_ID = 18063;//
	
}