package com.talkweb.basecomp.resource.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.Constants;
import com.talkweb.basecomp.common.util.SortComparator;
import com.talkweb.basecomp.resource.dao.ResourceDao;

/**
 * @Description 服务层接口
 * @author xixi1979
 * @date 2019/8/1
 */
@Service
public class ResourceService {
	private static final Logger log = LoggerFactory.getLogger(ResourceService.class);
	
	@Autowired
	private ResourceDao resourceDao;

	public List<JSONObject> getDirectoryList(JSONObject param) {
		log.debug("getDirectoryList，参数：{}", param);
		List<JSONObject> list = this.resourceDao.getDirectoryList(param);
		Collections.sort(list, nameSort);
		return list;
	}

	public int createDirectory(JSONObject param) {
		log.debug("createDirectory，参数：{}", param);

		List<JSONObject> list = this.resourceDao.getDirectoryList(param);
		List<String> nameList = getFieldList(list, Constants.FIELD_NAME);
		String name = param.getString(Constants.FIELD_NAME);
		if (nameList.contains(name)) {//重名
			return -1;
		}
		param.put(Constants.FIELD_ID, getUUID());
		param.put(Constants.FIELD_CREATETIME, new Date());
		return resourceDao.createDirectory(param);
	}

	public int updateDirectory(JSONObject param) {
		log.debug("updateDirectory，参数：{}", param);
		List<JSONObject> list = this.resourceDao.getDirectoryList(param);
		Map<String, String> map = getKeyValueMap(list, Constants.FIELD_NAME, Constants.FIELD_ID);
		String name = param.getString(Constants.FIELD_NAME);
		String id = param.getString(Constants.FIELD_ID);
		if (map.containsKey(name) && !id.equals(map.get(name))) {//重名
			return -1;
		}
		param.put(Constants.FIELD_CREATETIME, new Date());
		return resourceDao.updateDirectory(param);
	}

	public int deleteDirectory(JSONObject param){
		log.debug("deleteDirectory，参数：{}", param);
		
		JSONObject tmp = new JSONObject();
		tmp.put(Constants.FIELD_SCHOOLID, param.get(Constants.FIELD_SCHOOLID));
		tmp.put("parentId", param.get(Constants.FIELD_ID));
		
		List<JSONObject> list = this.resourceDao.getDirectoryList(tmp);
		List<JSONObject> reslist = this.resourceDao.getResourceList(tmp);
		if (!list.isEmpty() || !reslist.isEmpty()) {
			return -1;
		}
		
		resourceDao.deleteDirectory(param);
		return 1;
	}

	public List<JSONObject> getResourceList(JSONObject param) {
		log.debug("getResourceList，参数：{}", param);
		String name = param.getString(Constants.FIELD_NAME);
		if (!StringUtils.isEmpty(name)) {
			param.put(Constants.FIELD_NAME, "%" + name + "%");
			param.remove("parentId");
		} else {
			param.remove(Constants.FIELD_NAME);
		}
		List<JSONObject> list = this.resourceDao.getResourceList(param);
		Collections.sort(list, nameSort);
		return list;
	}

	public void createResource(JSONObject param) {
		log.debug("createResource，参数：{}", param);
		
		JSONObject tmp = getParameter(param);
		List<JSONObject> list = this.resourceDao.getResourceList(tmp);
		List<String> nameList = getFieldList(list, Constants.FIELD_NAME);

		String name = param.getString(Constants.FIELD_NAME);
		if (nameList.contains(name)) {
			String ext = "." + FilenameUtils.getExtension(name);
			String first = FilenameUtils.getBaseName(name);
			String str = first + "(1)" + ext;
			int index = 1;
			while (nameList.contains(str)) {
				index++;
				str = first + "(" + index + ")" + ext;
			}
			name = str;
			param.put(Constants.FIELD_NAME, name);
		}
		param.put(Constants.FIELD_ID, getUUID());
		param.put(Constants.FIELD_CREATETIME, new Date());
		resourceDao.createResource(param);
	}

	public int updateResource(JSONObject param) {
		log.debug("updateResource，参数：{}", param);
		param.remove(Constants.FIELD_NAME);//不允许修改文件名
		param.put(Constants.FIELD_CREATETIME, new Date());
		return resourceDao.updateResource(param);
	}

	public int deleteResource(JSONObject param){
		log.debug("deleteResource，参数：{}", param);
		resourceDao.deleteResource(param);
		return 1;
	}
	
	private JSONObject getParameter(JSONObject param) {
		JSONObject tmp = new JSONObject();
		tmp.put(Constants.FIELD_SCHOOLID, param.get(Constants.FIELD_SCHOOLID));
		tmp.put(Constants.FIELD_ACCOUNTID, param.get(Constants.FIELD_ACCOUNTID));
		tmp.put("resType", param.get("resType"));
		tmp.put("parentId", param.get("parentId"));
		return tmp;
	}
	
	private final static Comparator<JSONObject> nameSort = new SortComparator(
			SortComparator.getField(Constants.FIELD_NAME, SortComparator.FIELDTYPE_CHINESE));
	
    private static String getUUID() {
    	return UUID.randomUUID().toString().replaceAll("-", "");
    }

    private static List<String> getFieldList(Collection<JSONObject> list, String key) {
		List<String> ret = new ArrayList<>();
		if (list != null && !list.isEmpty()) {
			for (JSONObject obj : list) {
				String val = obj.getString(key);
				if (!StringUtils.isEmpty(val) && !ret.contains(val))
					ret.add(val);
			}
		}
		return ret;
	}

    private static Map<String, String> getKeyValueMap(Collection<JSONObject> list, String key, String value) {
		Map<String, String> map = new HashMap<>();
		if (list != null && !list.isEmpty()) {
			for (JSONObject account : list) {
				String id = account.getString(key);
				String name = account.getString(value);
				if (!StringUtils.isEmpty(id))
					map.put(id, name);
			}
		}
		return map;
	}

}