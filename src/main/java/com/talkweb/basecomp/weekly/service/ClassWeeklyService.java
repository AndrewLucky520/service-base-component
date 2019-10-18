package com.talkweb.basecomp.weekly.service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.Constants;
import com.talkweb.basecomp.common.inter.BaseDataInterface;
import com.talkweb.basecomp.common.inter.DocumentInterface;
import com.talkweb.basecomp.common.util.DateUtil;
import com.talkweb.basecomp.common.util.ExcelUtil;
import com.talkweb.basecomp.common.util.FileUtil;
import com.talkweb.basecomp.common.util.JSONUtil;
import com.talkweb.basecomp.common.util.MimeUtils;
import com.talkweb.basecomp.common.util.ZipUtil;
import com.talkweb.basecomp.weekly.dao.ClassWeeklyDao;

/**
 * @Description 班级周刊服务层接口实现类
 * @author xixi1979
 * @date 2018/8/1
 */
@Service
public class ClassWeeklyService {
    private static final Logger log = LoggerFactory.getLogger(ClassWeeklyService.class);
	
	@Autowired
	private ClassWeeklyDao classWeeklyDao;

	@Autowired
	private BaseDataInterface baseDataInterface;

	@Autowired
	private DocumentInterface documentInterface;

    private ExecutorService executor;

    @Value("${weekly.threadPoolSize}")
    protected int threadPoolSize;

	@PostConstruct
	public void applicationStart(){
        if (executor == null) {
        	executor = Executors.newFixedThreadPool(threadPoolSize);
        }
	}

	@PreDestroy
	public void applicationEnd(){
		shutdownAndAwaitTermination(executor);
	}

	public JSONObject getManagedClassList(JSONObject param){
		initParameter(param);
		log.debug("getManagedClassList，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "学校代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ACCOUNTID))) {
			return JSONUtil.getResponse(param, "当前用户账号为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_TERMINFO))) {
			return JSONUtil.getResponse(param, "学年学期信息为空，处理失败！", log);
		}

		try {
			List<JSONObject> ret = classWeeklyDao.getManagedClassList(param);
			return JSONUtil.getResponse(ret);
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "获取可管理的班级列表失败。", ex, log);
		}
	}
	
	public JSONObject getGradeList(JSONObject param){
		log.debug("getGradeList，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "学校代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_TERMINFO))) {
			return JSONUtil.getResponse(param, "学年学期信息为空，处理失败！", log);
		}

		List<JSONObject> ret = new ArrayList<JSONObject>();
		JSONObject defaul = new JSONObject();
		defaul.put(Constants.FIELD_GRADENAME, "全部");
		ret.add(defaul);

		try {
			List<JSONObject> tmp = classWeeklyDao.getGradeList(param);
			ret.addAll(tmp);
			return JSONUtil.getResponse(ret);
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "获取年级列表失败。", ex, log);
		}
	}
 
	public JSONObject getClassList(JSONObject param){
		log.debug("getClassList，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "学校代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_TERMINFO))) {
			return JSONUtil.getResponse(param, "学年学期信息为空，处理失败！", log);
		}

		List<JSONObject> ret = new ArrayList<JSONObject>();
		JSONObject defaul = new JSONObject();
		defaul.put(Constants.FIELD_CLASSNAME, "全部");
		ret.add(defaul);

		try {
			if (!StringUtils.isEmpty(param.get(Constants.FIELD_GRADEID))) {
				List<JSONObject> tmp = classWeeklyDao.getClassList(param);
				ret.addAll(tmp);
			}
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "获取班级列表失败。", ex, log);
		}
		return JSONUtil.getResponse(ret);
	}
 
	public JSONObject getWeeklyClassList(JSONObject param){
		initTermInfo(param);
		initParameter(param);
		param.put(Constants.FIELD_NEED_RECORDCOUNT, 1);
		log.debug("getWeeklyClassList，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "学校代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_TERMINFO))) {
			return JSONUtil.getResponse(param, "学年学期信息为空，处理失败！", log);
		}

		List<JSONObject> ret = new ArrayList<JSONObject>();

		try {
			List<JSONObject> tt = getUserClassListInternal(param);
			if (tt.size() > 0) {
				JSONObject obj = new JSONObject();
				obj.put(Constants.FIELD_GRADENAME, "我的班级");
				obj.put(Constants.FIELD_CLASSLIST, tt);
				ret.add(obj);
			}
			
			List<JSONObject> l = classWeeklyDao.getGradeList(param);
			for (JSONObject obj : l) {
				param.put(Constants.FIELD_GRADEID, obj.get(Constants.FIELD_GRADEID));
				List<JSONObject> tmp = classWeeklyDao.getClassList(param);
				if (tmp.size() > 0) {
					ret.add(obj);
					obj.put(Constants.FIELD_CLASSLIST, tmp);
				}
			}
			
			return JSONUtil.getResponse(ret);
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "获取周刊班级列表数据失败。", ex, log);
		}
	}

	public JSONObject getWeeklyTypeList(JSONObject param) {
		try {
			List<JSONObject> ret = classWeeklyDao.getWeeklyTypeList();
			return JSONUtil.getResponse(ret);
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "获取周刊类型列表失败。", ex, log);
		}
	}

	public JSONObject getWeeklyStatistics(JSONObject param) {
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "学校代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_TERMINFO))) {
			return JSONUtil.getResponse(param, "学年学期信息为空，处理失败！", log);
		}

		try {
			return JSONUtil.getResponse(getWeeklyStatisticsInternal(param));
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "获取周刊统计数据失败。", ex, log);
		}
	}

	protected List<JSONObject> getWeeklyStatisticsInternal(JSONObject param) {
		if (!StringUtils.isEmpty(param.get(Constants.FIELD_BEGINDATE)) 
				|| !StringUtils.isEmpty(param.get(Constants.FIELD_ENDDATE))) {
			if (StringUtils.isEmpty(param.get(Constants.FIELD_BEGINDATE))) {
				param.put(Constants.FIELD_BEGINDATE, param.get(Constants.FIELD_ENDDATE));
			}
			if (StringUtils.isEmpty(param.get(Constants.FIELD_ENDDATE))) {
				param.put(Constants.FIELD_ENDDATE, param.get(Constants.FIELD_BEGINDATE));
			}
		}
				
		List<JSONObject> ret = classWeeklyDao.getWeeklyStatistics(param);
		return ret;
	}

 
	public ResponseEntity<byte[]> exportStatistics(HttpServletRequest req) {
		String schoolId = req.getParameter(Constants.FIELD_SCHOOLID);
		if (StringUtils.isEmpty(schoolId)) {
			log.error("未传入学校代码，处理失败。");
        	return new ResponseEntity<byte[]>(new byte[0], new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		String classId = req.getParameter(Constants.FIELD_CLASSID);
		String gradeId = req.getParameter(Constants.FIELD_GRADEID);
		String start = req.getParameter(Constants.FIELD_BEGINDATE);
		String end = req.getParameter(Constants.FIELD_ENDDATE);
		JSONObject param = new JSONObject();
		param.put(Constants.FIELD_SCHOOLID, schoolId);
		param.put(Constants.FIELD_CLASSID, classId);
		param.put(Constants.FIELD_GRADEID, gradeId);
		param.put(Constants.FIELD_BEGINDATE, start);
		param.put(Constants.FIELD_ENDDATE, end);

		try {
			List<JSONObject> ret = getWeeklyStatisticsInternal(param);
			return exportExcelTable(req, ret);
        } catch(Exception ex) {
			log.error("导出周刊统计数据失败。", ex);
        	return new ResponseEntity<byte[]>(new byte[0], new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}

	public ResponseEntity<byte[]> downloadAttachments(HttpServletRequest req) {
		String str = req.getParameter(Constants.FIELD_WEEKLYIDS);
		if (StringUtils.isEmpty(str)) {
			log.error("未传入周刊ID，处理失败。");
        	return new ResponseEntity<byte[]>(new byte[0], new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		List<String> names = new ArrayList<>();
		List<String> urls = new ArrayList<>();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
        	String fileName = "班级周刊.zip";
        	getFileAndNames(str, names, urls);
        	ZipUtil.zip(out, names, urls);
        	return FileUtil.downloadData(req, fileName, out.toByteArray());
        } catch(Exception ex) {
			log.error("下载周刊附件处理失败。", ex);
        	return new ResponseEntity<byte[]>(new byte[0], new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
 
	public ResponseEntity<byte[]> downloadAttachment(HttpServletRequest req) {
		String weeklyId = req.getParameter(Constants.FIELD_WEEKLYID);
		if (StringUtils.isEmpty(weeklyId)) {
			log.error("未传入周刊ID，处理失败。");
        	return new ResponseEntity<byte[]>(new byte[0], new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		JSONObject param = new JSONObject();
		param.put(Constants.FIELD_WEEKLYID, weeklyId);

		JSONObject weekly = classWeeklyDao.getWeekly(param);
		String str = weekly.getString(Constants.FIELD_ACCESSURL);
		String fileName = weekly.getString(Constants.FIELD_DOCUMENTNAME);

        try {
        	return FileUtil.downloadData(req, fileName, str);
        } catch(Exception ex) {
			log.error("下载周刊附件处理失败。", ex);
        	return new ResponseEntity<byte[]>(new byte[0], new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}

 
	public JSONObject getWeeklyList(JSONObject param) {
		initTermInfo(param);
	    initParameter(param);
		log.debug("getWeeklyList，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_CLASSID))) {
			return JSONUtil.getResponse(param, "班级代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			List<JSONObject> list = classWeeklyDao.getClass(param);
			if (list.size() > 0) {
				param.put(Constants.FIELD_SCHOOLID, list.get(0).get(Constants.FIELD_SCHOOLID));
			}
		}

		try {
			List<JSONObject> ret = classWeeklyDao.getWeeklyList(param);
			handleFirstImage(ret);
			
			JSONObject obj = JSONUtil.getResponse(ret);
			obj.put(Constants.FIELD_TOTAL, classWeeklyDao.getWeeklyCount(param));
			return obj;
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "获取班级周刊列表失败。", ex, log);
		}
	}

 
	public JSONObject updateAndGetWeekly(JSONObject param) {
		log.debug("accessWeekly，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_WEEKLYID))) {
			return JSONUtil.getResponse(param, "周刊ID为空，处理失败！", log);
		}
		classWeeklyDao.updateViews(param);
		JSONObject weekly = classWeeklyDao.getWeekly(param);
		if (weekly == null) {
			return JSONUtil.getResponse(param, "未找到指定周刊。", log);
		}
		if (StringUtils.isEmpty(weekly.getString(Constants.FIELD_DOCUMENTID))) {
			return JSONUtil.getResponse(param, "未找到周刊相关附件，处理失败。", log);
		}

		handleDocumentImage(weekly);
		//获取附件相关信息
		//downloadDocuments(weekly);
		return JSONUtil.getResponse(weekly);
	}

 
	public JSONObject createWeekly(JSONObject param) {
	    initParameter(param);
		log.debug("createWeekly，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "学校代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ACCOUNTID))) {
			return JSONUtil.getResponse(param, "当前用户账号为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_TITLE))) {
			return JSONUtil.getResponse(param, "周刊标题为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_CLASSIFYID))) {
			return JSONUtil.getResponse(param, "周刊分类Id为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_CLASSID))) {
			return JSONUtil.getResponse(param, "班级代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_DOCUMENTLIST))) {
			return JSONUtil.getResponse(param, "附件信息为空，处理失败！", log);
		}
		
		if (StringUtils.isEmpty(param.get(Constants.FIELD_CREATETIME)))
			param.put(Constants.FIELD_CREATETIME, new Date());
		param.put(Constants.FIELD_WEEKLYID, getUUID());
		param.put(Constants.FIELD_FIRSTIMAGE, getFirstImage(param));
		
		//createClasses(param);
		createDocuments(param);
		int code = classWeeklyDao.createWeekly(param);

		try {
			Thread t = createCompressThread(param);
			t.start();
		} catch(Exception ex) {
			log.error("压缩图片异常。", ex);
		}
		return JSONUtil.getResponse(code);
	}

 
	public JSONObject deleteWeekly(JSONObject param){
		log.debug("deleteWeekly，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_WEEKLYID))) {
			return JSONUtil.getResponse(param, "周刊ID为空，处理失败！", log);
		}

		classWeeklyDao.deleteDocumentImage(param);
		classWeeklyDao.deleteWeeklyAttachment(param);
		int code = classWeeklyDao.deleteWeekly(param);
		if (code == 1) {
			//删除附件
		}
		return JSONUtil.getResponse(code);
	}

 
	public JSONObject getAuthorityList(JSONObject param) {
		log.debug("getAuthorityList，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_CLASSID))) {
			return JSONUtil.getResponse(param, "班级代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "学校代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_TERMINFO))) {
			return JSONUtil.getResponse(param, "学年学期信息为空，处理失败！", log);
		}
		
		try {
			List<JSONObject> ret = classWeeklyDao.getAuthorityList(param);
			return JSONUtil.getResponse(ret);
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "获取周刊管理员列表失败。", ex, log);
		}
	}

 
	public JSONObject getAuthority(JSONObject param) {
	    initParameter(param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_CLASSID))) {
			return JSONUtil.getResponse(param, "班级代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ACCOUNTID))) {
			return JSONUtil.getResponse(param, "用户账号代码为空，处理失败！", log);
		}
		param.put(Constants.FIELD_AUTHORIZEDID, param.get(Constants.FIELD_ACCOUNTID));
		try {
			int cc = classWeeklyDao.getAuthorityCount(param);
			return JSONUtil.getResponse(cc == 0 ? 0 : 1);
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "判断周刊管理员处理失败。", ex, log);
		}
	}

	public JSONObject createAuthority(JSONObject param) {
	    initParameter(param);
		log.debug("createAuthority，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(param, "学校代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_CLASSID))) {
			return JSONUtil.getResponse(param, "班级代码为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ACCOUNTID))) {
			return JSONUtil.getResponse(param, "当前用户账号为空，处理失败！", log);
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_USERLIST))) {
			return JSONUtil.getResponse(param, "被授权用户账号列表为空，处理失败！", log);
		}

		List<JSONObject> addUsers = new ArrayList<JSONObject>();
		JSONArray users = param.getJSONArray(Constants.FIELD_USERLIST);
		for (int i = 0; i < users.size(); i++) {
			JSONObject user = users.getJSONObject(i);
			user.put(Constants.FIELD_AUTHORIZEDID, user.get(Constants.FIELD_ID));
			user.put(Constants.FIELD_CLASSID, param.get(Constants.FIELD_CLASSID));
			if (classWeeklyDao.getAuthorityCount(user) == 0) {
				user.put(Constants.FIELD_SCHOOLID, param.get(Constants.FIELD_SCHOOLID));
				user.put(Constants.FIELD_CREATORID, param.get(Constants.FIELD_ACCOUNTID));
				user.put(Constants.FIELD_USERTYPE, user.get(Constants.FIELD_TYPE));
				user.put(Constants.FIELD_CREATETIME, new Date());
				user.put(Constants.FIELD_ID, getUUID());
				addUsers.add(user);
			}
		}
		
		int code = 1;
		if (addUsers.size() > 0) {
			param.put(Constants.FIELD_USERLIST, addUsers);
			code = classWeeklyDao.createAuthority(param);
		}
		return JSONUtil.getResponse(code);
	}

 
	public JSONObject deleteAuthority(JSONObject param){
		log.debug("deleteAuthority，参数：{}", param);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_AUTHORITYID))) {
			return JSONUtil.getResponse(param, "授权ID为空，处理失败！", log);
		}
		List<String> ids = stringToList(param.getString(Constants.FIELD_AUTHORITYID));
		param.put(Constants.FIELD_AUTHORITYID, ids);
		int code = classWeeklyDao.deleteAuthority(param);
		return JSONUtil.getResponse(code);
	}

	public JSONObject updateClassEmblem(JSONObject param) {
	    if (StringUtils.isEmpty(param.get(Constants.FIELD_CLASSID))) {
	    	return JSONUtil.getResponse(param, "班级代码为空，处理失败！", log);
	    }
	    if (StringUtils.isEmpty(param.get(Constants.FIELD_FILEID))) {
	    	return JSONUtil.getResponse(param, "附件为空，处理失败！", log);
	    }
	    if (StringUtils.isEmpty(param.get(Constants.FIELD_ACCESSURL))) {
	    	return JSONUtil.getResponse(param, "附件为空，处理失败！", log);
	    }

	    int code = 0;
	    JSONObject obj = this.classWeeklyDao.getClassEmblem(param);
	    if (obj != null) {
	    	code = this.classWeeklyDao.updateClassEmblem(param);
	    } else {
	    	code = this.classWeeklyDao.createClassEmblem(param);
	    }
	    return JSONUtil.getResponse(code);
	}

	public JSONObject getClassEmblem(JSONObject param) {
	    if (StringUtils.isEmpty(param.get(Constants.FIELD_CLASSID)))
	    	return JSONUtil.getResponse(param, "班级代码为空，处理失败！", log);
	    try {
	    	JSONObject obj = this.classWeeklyDao.getClassEmblem(param);
	    	return JSONUtil.getResponse(obj == null ? 0 : 1, obj); 
	    } catch (Exception ex) {
	    	return JSONUtil.getResponse(param, "获取班级班徽处理异常。", ex, log);
	    }
	}

	private void initParameter(JSONObject param) {
	    if (StringUtils.isEmpty(param.get(Constants.FIELD_CLASSID)) && 
	    		!StringUtils.isEmpty(param.get("extClassId"))) {
	    	List<JSONObject> data = this.classWeeklyDao.getClass(param);
	    	if (!data.isEmpty()) {
	    		JSONObject obj = data.get(0);
	    		param.put(Constants.FIELD_CLASSID, obj.get(Constants.FIELD_CLASSID));
	    		param.put(Constants.FIELD_SCHOOLID, obj.get(Constants.FIELD_SCHOOLID));
	    	}
	    } else {
			JSONObject user = param.getJSONObject(Constants.FIELD_CURRENTUSER);
			if (user != null) {//需要当前用户的用户id、账号id和账号名称
				param.put(Constants.FIELD_USERID, user.get(Constants.FIELD_USERID));
				param.put(Constants.FIELD_ACCOUNTID, user.get(Constants.FIELD_ACCOUNTID));
				param.put(Constants.FIELD_ACCOUNTNAME, user.get(Constants.FIELD_NAME));
			}
	    }
	}

	private void initTermInfo(JSONObject param) {
	    if (StringUtils.isEmpty(param.get(Constants.FIELD_TERMINFO))) {
	    	try {
				JSONObject ret = baseDataInterface.getCurrentTermInfoId();
				if (ret != null && ret.containsKey(Constants.FIELD_TERMINFO)) {
					param.put(Constants.FIELD_TERMINFO, ret.getString(Constants.FIELD_TERMINFO));
					param.put(Constants.FIELD_TERMINFOYEAR, ret.getString("schoolYear"));
				}
	        } catch(Exception ex) {
	        	log.error("获取学年学期信息异常。", ex);
	        }
	    }
	    if (StringUtils.isEmpty(param.get(Constants.FIELD_TERMINFO))) {
	    	DateUtil.initTermInfo(param);
	    }
	}

	private void getFileAndNames(String ids, List<String> names, List<String> urls) {
		JSONObject param = new JSONObject();
		String[] strs = ids.split(",");
		for (String s : strs) {
			if (s.isEmpty())
				continue;
			param.put(Constants.FIELD_WEEKLYID, s);
			JSONObject obj = this.classWeeklyDao.getWeekly(param);
			if (obj != null) {
				String url = obj.getString(Constants.FIELD_ACCESSURL);
				String ext = "." + FilenameUtils.getExtension(url);
				String title = obj.getString(Constants.FIELD_TITLE);
				String name = title + ext;
				if (names.contains(name)) {
					String tmp = title + "(1)" + ext;
					int index = 1;
					while (names.contains(tmp)) {
						index++;
						tmp = title + "(" + index + ")" + ext;
					}
					name = tmp;
				}
				names.add(name);
				urls.add(url);
			}
		}
	}
	
	private ResponseEntity<byte[]> exportExcelTable(HttpServletRequest req, List<JSONObject> weeklies) {
		String[] hh = new String[]{"年级", "班级", "周刊数"};
		List<String[]> data = new ArrayList<String[]>();
		for (JSONObject obj : weeklies) {
			String[] ss = new String[]{obj.getString("gradeName"), obj.getString("className"), obj.getString("recordCount")};
			data.add(ss);
		}

        ByteArrayOutputStream fOut = new ByteArrayOutputStream();
        try{    
            ExcelUtil.exportTable("周刊统计表", hh, data, fOut);
        } catch(Exception e) {  
			log.error("导出excel文件处理失败。", e);
        	return new ResponseEntity<byte[]>(new byte[0], new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        } finally{
            IOUtils.closeQuietly(fOut);
        }
        byte[] bt = fOut.toByteArray();

        try {
        	return FileUtil.downloadData(req, "周刊统计表.xlsx", bt);
        } catch(Exception ex) {
			log.error("导出excel文件处理失败。", ex);
        	return new ResponseEntity<byte[]>(new byte[0], new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	private List<JSONObject> getUserClassListInternal(JSONObject param) {
		List<JSONObject> ret = new ArrayList<JSONObject>();
		if (!StringUtils.isEmpty(param.get(Constants.FIELD_USERID))) {
			ret = this.classWeeklyDao.getUserWeeklyClassList(param);
		}
		
		return ret;
	}

	private void createDocuments(JSONObject param) {
		JSONArray docs = param.getJSONArray(Constants.FIELD_DOCUMENTLIST);
		for (int j = 0; j < docs.size(); j++) {
			JSONObject doc = docs.getJSONObject(j);
			doc.put(Constants.FIELD_ID, getUUID());
			doc.put(Constants.FIELD_WEEKLYID, param.get(Constants.FIELD_WEEKLYID));
			doc.put(Constants.FIELD_SCHOOLID, param.get(Constants.FIELD_SCHOOLID));
			classWeeklyDao.createWeeklyAttachment(doc);
			if (doc.containsKey(Constants.FIELD_IMAGELIST)) {
				JSONArray images = doc.getJSONArray(Constants.FIELD_IMAGELIST);
				for (int i = 0; i < images.size(); i++) {
					JSONObject img = images.getJSONObject(i);
					img.put(Constants.FIELD_ID, getUUID());
				}
				doc.put(Constants.FIELD_IMAGELIST, images);
				classWeeklyDao.createDocumentImage(doc);
			}
		}
	}

	private void handleDocumentImage(JSONObject weekly) {
		List<JSONObject> images = classWeeklyDao.getDocumentImage(weekly);
		String[] strs = new String[images.size()];
		if (images.size() > 0) {
			int i = 0;
			for (JSONObject img: images) {
				strs[i++] = img.getString(Constants.FIELD_ACCESSURL);
			}
		} else {
			String fileName = weekly.getString(Constants.FIELD_DOCUMENTNAME);
			if (MimeUtils.isSupportImage(fileName)) {
				strs = new String[1];
				strs[0] = weekly.getString(Constants.FIELD_ACCESSURL);
			}
		}
		weekly.put(Constants.FIELD_FIRSTIMAGE, strs);
	}

	private void handleFirstImage(List<JSONObject> weeklys) {
		for (JSONObject obj: weeklys) {
			String first = obj.getString(Constants.FIELD_FIRSTIMAGE);
			if (!StringUtils.isEmpty(first)) {
				String[] strs = first.split(";");
				obj.put(Constants.FIELD_FIRSTIMAGE, strs);
			}
		}
	}
	
	private String getFirstImage(JSONObject param) {
		String url = null;
		int index = 0;
		List<JSONObject> list = new ArrayList<>();
		JSONArray docs = param.getJSONArray(Constants.FIELD_DOCUMENTLIST);
		for (int j = 0; j < docs.size(); j++) {
			JSONObject doc = docs.getJSONObject(j);
			if (doc.containsKey(Constants.FIELD_IMAGELIST)) {
				JSONArray images = doc.getJSONArray(Constants.FIELD_IMAGELIST);
				for (int i = 0; i < images.size(); i++) {
					if (i >= 3) break;
					JSONObject img = images.getJSONObject(i);
					list.add(img);
				}
				if (list.size() > 0) {
					break;
				}
			} else {
				String fileName = doc.getString(Constants.FIELD_NAME);
				if (MimeUtils.isSupportImage(fileName)) {
					list.add(doc);
					if (index++ >= 3) {
						break;
					}
				}
			}
		}
		if (list.size() > 0) {
			for (JSONObject doc: list) {
				url = concatUrl(doc.getString(Constants.FIELD_ACCESSURL), url);
			}
			param.put(Constants.FIELD_FIRSTIMAGELIST, list);
		}
		return url;
	}

	private Thread createCompressThread(final JSONObject param) {
		Thread compressThread = new Thread() {
            public void run() {
	        	log.info("开始压缩 {} ", param.get(Constants.FIELD_FIRSTIMAGELIST));
	    		String compressFirstImage = compressFirstImage(param);
		        try {
					if (!StringUtils.isEmpty(compressFirstImage)) {
						param.put(Constants.FIELD_FIRSTIMAGE, compressFirstImage);
						classWeeklyDao.updateFirstImage(param);
					}
		        } catch(Exception ex) {
		        	log.error("压缩图片异常。", ex);
		        }
            }
        };
        return compressThread;
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

	private void shutdownAndAwaitTermination(ExecutorService pool) {
		pool.shutdown();
		try {
			if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
				pool.shutdownNow();
				if (!pool.awaitTermination(20, TimeUnit.SECONDS))
					log.error("线程池无法被终止。");
			}
			//log.info("线程池已终止。");
		} catch (InterruptedException ie) {
			pool.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	private void createConvertDocument(JSONObject weekly) {
		JSONObject param = documentInterface.convertToPNG(weekly);
		if (param.getIntValue(Constants.FIELD_CODE) == 0) {//转换出错
			return ;
		}
		
		JSONArray arr = param.getJSONArray(Constants.FIELD_DATA);
		param.put(Constants.FIELD_DOCUMENTLIST, arr);
		
		//获取并压缩列表展示图片
		String firstImage = getFirstImage(param);
		
		String compressFirstImage = compressFirstImage(param);
		if (!StringUtils.isEmpty(compressFirstImage))
			firstImage = compressFirstImage;
		
		param.put(Constants.FIELD_FIRSTIMAGE, firstImage);
		classWeeklyDao.updateFirstImage(param);
		this.createDocuments(param);
	}
	
	private String compressFirstImage(JSONObject param) {
    	String url = null;
        try {
			JSONObject obj = new JSONObject();
	    	obj.put(Constants.FIELD_IMAGELIST, param.get(Constants.FIELD_FIRSTIMAGELIST));
			JSONObject ret = documentInterface.compress(obj);
			if (ret != null && ret.containsKey(Constants.FIELD_DATA)) {
				JSONArray arr2 = ret.getJSONArray(Constants.FIELD_DATA);
				for (int i = 0; i < arr2.size(); i++) {
					url = concatUrl(arr2.getJSONObject(i).getString(Constants.FIELD_ACCESSURL), url);
				}
			}
        } catch(Exception ex) {
        	log.error("压缩图片异常。", ex);
        }
		return url;
	}
	
	protected final class ConvertTask implements Runnable {
        private JSONObject weekly;

        public ConvertTask(JSONObject weekly) {
            this.weekly = weekly;
        }

        @Override
        public void run() {
            long startAt = System.currentTimeMillis();
            try {
            	createConvertDocument(weekly);
            } catch (Exception e) {
                log.error("转换文档出错。", e);
            } finally {
	            log.debug("convert finished in {} ms ", System.currentTimeMillis() - startAt);
	        }
        }
    }

	private static List<String> stringToList(String str) {
		List<String> ret = new ArrayList<>();
		if (StringUtils.isEmpty(str))
			return ret;

		for (String sub : str.split(",")) {
			if (!ret.contains(sub))
				ret.add(sub);
		}
		return ret;
	}

}