package com.talkweb.basecomp.circle.service;

import java.util.ArrayList;
import java.util.Date;
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
import com.talkweb.basecomp.circle.dao.ClassCircleDao;
import com.talkweb.basecomp.common.util.DateUtil;
import com.talkweb.basecomp.common.util.JSONUtil;

/**
 * @Description 班级圈服务层接口
 * @author xixi1979
 * @date 2018/9/11
 */
@Service
public class ClassCircleService {
	private static final Logger log = LoggerFactory.getLogger(ClassCircleService.class);
	
	@Autowired
	private ClassCircleDao classCircleDao;

	public JSONObject getCircleList(JSONObject param) {
		initParameter(param);
		log.debug("getCircleList，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_CLASSID))) {
			return JSONUtil.getResponse(param, "班级代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_TERMINFO))) {
			return JSONUtil.getResponse(param, "学年学期信息为空，处理失败！", log);
		}

		try {
			param.remove(Constants.FIELD_USERID);
			return getCircleData(param, true);
		} catch(Exception ex) {
			//ex.printStackTrace();
			return JSONUtil.getResponse(param, "获取班级圈列表异常。", ex, log);
		}
	}

	public JSONObject getTopCircle(JSONObject param) {
		initParameter(param);
		log.debug("getTopCircle，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_USERID))) {
			return JSONUtil.getResponse(param, "用户代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "学校代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_TERMINFO))) {
			return JSONUtil.getResponse(param, "学年学期信息为空，处理失败！", log);
		}

		try {
			int page = param.getIntValue(Constants.FIELD_PAGENO);
			if (page <= 0)
				param.put(Constants.FIELD_PAGENO, 1);
			page = param.getIntValue(Constants.FIELD_PAGESIZE);
			if (page <= 0)
				param.put(Constants.FIELD_PAGESIZE, 1);
			param.remove(Constants.FIELD_CLASSID);
			return getCircleData(param, false);
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "获取班级圈列表异常。", ex, log);
		}
	}

	public JSONObject createCircle(JSONObject param) {
		initParameter(param);
		log.debug("createCircle，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "学校代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ACCOUNTID))) {
			return JSONUtil.getResponse(param, "当前用户账号为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_CLASSIDS))) {
			return JSONUtil.getResponse(param, "班级代码列表为空，处理失败！", log);
		}

		if (StringUtils.isEmpty(param.get(Constants.FIELD_CREATETIME)))
			param.put(Constants.FIELD_CREATETIME, new Date());
		
		if (StringUtils.isEmpty(param.get(Constants.FIELD_CREATORTYPE)))
			param.put(Constants.FIELD_CREATORTYPE, getUserType(param));
		
		param.put(Constants.FIELD_CIRCLEID, getUUID());
		
		createClasses(param);
		createImages(param);
		int code = classCircleDao.createCircle(param);
		
		JSONObject ret = JSONUtil.getResponse(code);
		ret.put(Constants.FIELD_CIRCLEID, param.get(Constants.FIELD_CIRCLEID));
		return ret;
	}

	public JSONObject deleteCircle(JSONObject param){
		log.debug("deleteCircle，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_CIRCLEID))) {
			return JSONUtil.getResponse(param, "班级圈ID为空，处理失败！", log);
		}

		classCircleDao.deleteCircleImageList(param);
		classCircleDao.deleteCircleLikeList(param);
		classCircleDao.deleteCircleCommentList(param);
		int code = classCircleDao.deleteCircle(param);
		if (code == 1) {
			//删除附件
		}
		return JSONUtil.getResponse(code);
	}

	public synchronized JSONObject createOrDeleteCircleLike(JSONObject param) {
		initParameter(param);
		log.debug("createOrDeleteCircleLike，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ACCOUNTID))) {
			return JSONUtil.getResponse(param, "当前用户账号为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_CIRCLEID))) {
			return JSONUtil.getResponse(param, "班级圈ID为空，处理失败！", log);
		}
		int code = 0, isZan = 0;
		int cc = classCircleDao.getCircleLikeCount(param);
		if (cc > 0) {
			code = classCircleDao.deleteCircleLike(param);
		} else {
			if (isParent(param)) {
				String name = param.getString(Constants.FIELD_ACCOUNTNAME);
				param.put(Constants.FIELD_ACCOUNTNAME, name 
						+ getParentRoleName(param.getIntValue("prole")));
			}
			param.put(Constants.FIELD_CREATETIME, new Date());
			param.put(Constants.FIELD_ID, getUUID());
			code = classCircleDao.createCircleLike(param);
			isZan = 1;
		}
		
		JSONObject ret = JSONUtil.getResponse(code);
		ret.put(Constants.FIELD_CIRCLEID, param.get(Constants.FIELD_CIRCLEID));
		ret.put(Constants.FIELD_ACCOUNTID, param.get(Constants.FIELD_ACCOUNTID));
		ret.put(Constants.FIELD_ISZAN, isZan);
		getCircleLikeList(ret, Constants.FIELD_DATA);
		try {
			if (isZan == 1)
				ret.put(Constants.FIELD_CREATORID, classCircleDao.getCircleCreator(param));
		} catch(Exception ex) {}
		return ret;
	}

	public JSONObject createCircleComment(JSONObject param) {
		initParameter(param);
		log.debug("createCircleComment，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "学校代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ACCOUNTID))) {
			return JSONUtil.getResponse(param, "当前用户账号为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_CIRCLEID))) {
			return JSONUtil.getResponse(param, "班级圈ID为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_CONTENT))) {
			return JSONUtil.getResponse(param, "评论内容为空，处理失败！", log);
		}
		
		if (isParent(param)) {
			String name = param.getString(Constants.FIELD_ACCOUNTNAME);
			param.put(Constants.FIELD_ACCOUNTNAME, name 
					+ getParentRoleName(param.getIntValue("prole")));
		}
		param.put(Constants.FIELD_CREATETIME, new Date());
		param.put(Constants.FIELD_COMMENTID, getUUID());
		if (param.containsKey(Constants.FIELD_PARENT)) {
			JSONObject parent = param.getJSONObject(Constants.FIELD_PARENT);
			param.put(Constants.FIELD_PARENTID, parent.get(Constants.FIELD_COMMENTID));
			param.put(Constants.FIELD_PARENTNAME, parent.get(Constants.FIELD_ACCOUNTNAME));
		}
		
		int code = classCircleDao.createCircleComment(param);

		JSONObject ret = JSONUtil.getResponse(code);
		ret.put(Constants.FIELD_CIRCLEID, param.get(Constants.FIELD_CIRCLEID));
		ret.put(Constants.FIELD_ACCOUNTID, param.get(Constants.FIELD_ACCOUNTID));
		getCircleCommentList(ret, Constants.FIELD_DATA);
		try {
			ret.put(Constants.FIELD_CREATORID, classCircleDao.getCircleCreator(param));
		} catch(Exception ex) {}
		return ret;
	}

	public JSONObject deleteCircleComment(JSONObject param) {
		initParameter(param);
		log.debug("deleteCircleComment，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_COMMENTID))) {
			return JSONUtil.getResponse(param, "评论ID为空，处理失败！", log);
		}

		ArrayList<String> ids = new ArrayList<String>();
		getSubCommentIds(ids, param.getString(Constants.FIELD_COMMENTID));
		param.put(Constants.FIELD_COMMENTIDS, ids);
		//log.debug("deleteCircleComment，参数：{}", ids);
		classCircleDao.deleteCircleComment(param);
		
		JSONObject ret = JSONUtil.getResponse();
		ret.put(Constants.FIELD_CIRCLEID, param.get(Constants.FIELD_CIRCLEID));
		ret.put(Constants.FIELD_ACCOUNTID, param.get(Constants.FIELD_ACCOUNTID));
		getCircleCommentList(ret, Constants.FIELD_DATA);
		return ret;
	}

	private void initParameter(JSONObject param) {
		JSONObject user = param.getJSONObject(Constants.FIELD_CURRENTUSER);
		if (user != null) {//需要当前用户的用户id、账号id和账号名称，如果是家长，需要将账号映射到学生账号，如果是学生，需要当前班级id
			param.put(Constants.FIELD_USERID, user.get(Constants.FIELD_USERID));
			param.put(Constants.FIELD_ACCOUNTID, user.get(Constants.FIELD_ACCOUNTID));
			param.put(Constants.FIELD_ACCOUNTNAME, user.get(Constants.FIELD_NAME));
			if (StringUtils.isEmpty(param.get(Constants.FIELD_CLASSID))) {
				param.put(Constants.FIELD_CLASSID, user.get(Constants.FIELD_STUDENTCLASSID));
			}
			if (isParent(param)) {
				//param.put(Constants.FIELD_ACCOUNTID, param.get(Constants.FIELD_STUDENTACCOUNTID));
				param.put("prole", user.get("psRelationship"));
			}
		}
	}

	private JSONObject getCircleData(JSONObject param, boolean needCount) {
		List<JSONObject> ret = classCircleDao.getCircleList(param);
		for (JSONObject obj: ret) {
			String time = DateUtil.showTimeText(obj.getDate(Constants.FIELD_CREATETIME));
			obj.put(Constants.FIELD_CREATETIMETEXT, time);
			obj.put(Constants.FIELD_ACCOUNTID, param.get(Constants.FIELD_ACCOUNTID));
			obj.put("roleName", getUserRoleName(obj));
			getCircleImageList(obj);
			getCircleLikeList(obj, Constants.FIELD_ZANLIST);
			getCircleCommentList(obj, Constants.FIELD_COMMENTS);
		}
		JSONObject obj = JSONUtil.getResponse(ret);
		if (needCount) {
			obj.put(Constants.FIELD_TOTAL, classCircleDao.getCircleCount(param));
		}
		//log.info("返回数据:{}", ret);
		return obj;
	}
	
	private void getSubCommentIds(ArrayList<String> ids, String commentId) {
		List<String> subs = classCircleDao.getSubCircleCommentList(commentId);
		for (String s: subs) {
			getSubCommentIds(ids, s);
		}
		ids.add(commentId);
	}
	
	private void createClasses(JSONObject param) {
		if (param.containsKey(Constants.FIELD_CLASSIDS)) {
			JSONArray childs = param.getJSONArray(Constants.FIELD_CLASSIDS);
			if (childs.size() > 0) {
				for (Object obj: childs) {
					param.put(Constants.FIELD_ID, getUUID());
					param.put(Constants.FIELD_CLASSID, obj);
					classCircleDao.createCircleClass(param);
				}
			} else if (param.containsKey(Constants.FIELD_CLASSID)) {
				param.put(Constants.FIELD_ID, getUUID());
				classCircleDao.createCircleClass(param);
			} else {
				throw new RuntimeException("未传入班级代码，创建班级圈失败。");
			}
		}
	}
	
	private void createImages(JSONObject param) {
		if (param.containsKey(Constants.FIELD_IMAGELIST)) {
			JSONArray images = param.getJSONArray(Constants.FIELD_IMAGELIST);
			if (images.size() > 0) {
				for (int i = 0; i < images.size(); i++) {
					JSONObject img = images.getJSONObject(i);
					img.put(Constants.FIELD_ID, getUUID());
					img.put(Constants.FIELD_IMGINDEX, i + 1);
				}
				param.put(Constants.FIELD_IMAGELIST, images);
				classCircleDao.createCircleImage(param);
			}
		}
	}

	private void getCircleImageList(JSONObject circle) {
		List<JSONObject> images = null;
		if (circle.getIntValue(Constants.FIELD_FLAG) == 1) {//老版班级圈
			images = classCircleDao.getTempCircleImageList(circle);
		} else {
			images = classCircleDao.getCircleImageList(circle);
		}
		String[] strs = new String[images.size()];
		if (images.size() > 0) {
			int i = 0;
			for (JSONObject img: images) {
				strs[i++] = img.getString(Constants.FIELD_ACCESSURL);
			}
		}
		circle.put(Constants.FIELD_IMGURLS, strs);
	}

	private void getCircleCommentList(JSONObject circle, String feildName) {
		List<JSONObject> comments = classCircleDao.getCircleCommentList(circle);
		circle.put(Constants.FIELD_COMMENTCOUNT, comments.size());
		circle.put(feildName, comments);
	}

	private void getCircleLikeList(JSONObject circle, String feildName) {
		List<JSONObject> likes = classCircleDao.getCircleLikeList(circle);
		circle.put(Constants.FIELD_LIKECOUNT, likes.size());
		circle.put(feildName, likes);
	}
	
	static String concatUrl(String url1, String url2) {
		if (!StringUtils.isEmpty(url1)) {
			if (url2 == null) {
				return url1;
			} else {
				return url2 + ";" + url1;
			}
		}
		return url2;
	}
	
    private static String getUUID() {
    	return UUID.randomUUID().toString().replaceAll("-", "");
    }

	static boolean isParent(JSONObject param) {
		return Constants.ROLE_PARENT.equals(param.get(Constants.FIELD_ROLE));
	}

    private static int getUserType(JSONObject param) {
    	Object obj = param.get(Constants.FIELD_ROLE);
    	if (Constants.ROLE_SYSTEMMANAGER.equals(obj)) return 1;
    	if (Constants.ROLE_TEACHER.equals(obj)) return 2;
    	if (Constants.ROLE_STUDENT.equals(obj)) return 3;
    	if (Constants.ROLE_PARENT.equals(obj)) 
    		return param.getIntValue("prole") + 4;
    	return 0;
    }

    private static String getUserRoleName(JSONObject obj) {
    	int type = obj.getIntValue(Constants.FIELD_CREATORTYPE);
    	if (type == 1) return "管理员";
    	if (type == 2) return "老师";
    	if (type == 3) return "学生";
    	obj.put(Constants.FIELD_CREATORTYPE, 4);
    	return getParentRoleName(type - 4);
    }

	public final static String ROLENAME_PARENT_FATHER = "爸爸";
	public final static String ROLENAME_PARENT_MOTHER = "妈妈";
	public final static String ROLENAME_PARENT_GRANDFATHER = "爷爷";
	public final static String ROLENAME_PARENT_GRANDMOTHER = "奶奶";
	public final static String ROLENAME_PARENT_GRANDPA = "外公";
	public final static String ROLENAME_PARENT_GRANDMA = "外婆";
	public final static String ROLENAME_PARENT_PARENT = "家长";

    public final static int PROLE_FATHER = 1;
    public final static int PROLE_MOTHER = 2;
    public final static int PROLE_GRANDFATHER = 3;
    public final static int PROLE_GRANDMOTHER = 4;
    public final static int PROLE_GRANDPA = 5;
    public final static int PROLE_GRANDMA = 6;
    public final static int PROLE_PARENT = 7;

    private static String getParentRoleName(int prole) {
    	switch(prole) {
    	case PROLE_FATHER:
    		return ROLENAME_PARENT_FATHER;
    	case PROLE_MOTHER:
    		return ROLENAME_PARENT_MOTHER;
    	case PROLE_GRANDFATHER:
    		return ROLENAME_PARENT_GRANDFATHER;
    	case PROLE_GRANDMOTHER:
    		return ROLENAME_PARENT_GRANDMOTHER;
    	case PROLE_GRANDPA:
    		return ROLENAME_PARENT_GRANDPA;
    	case PROLE_GRANDMA:
    		return ROLENAME_PARENT_GRANDMA;
    	}
		return ROLENAME_PARENT_PARENT;
	}

}