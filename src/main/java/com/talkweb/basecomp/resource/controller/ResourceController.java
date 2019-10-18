package com.talkweb.basecomp.resource.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.Constants;
import com.talkweb.basecomp.common.util.JSONUtil;
import com.talkweb.basecomp.resource.service.ResourceService;


/**
 * @Description 班级圈Action层
 * @author xixi1979
 * @date 2018/9/11
 */
@Controller
@RequestMapping(value = "/resource/")
public class ResourceController {
	static final Logger log = LoggerFactory.getLogger(ResourceController.class);
	
	@Autowired
	private ResourceService resourceService;

	@RequestMapping(value = "/getDirectoryList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getDirectoryList(HttpServletRequest req, @RequestBody JSONObject param) {
		JSONObject ck = this.checkParameter(param);
		if (ck != null) {
			return ck;
		}

		try {
			List<JSONObject> list = this.resourceService.getDirectoryList(param);
			return JSONUtil.getResponse(1, list);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "获取目录列表异常。");
		}	
	}

	@RequestMapping(value = "/createDirectory", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject createDirectory(HttpServletRequest req, @RequestBody JSONObject param) {
		JSONObject ck = this.checkParameter(param);
		if (ck != null) {
			return ck;
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_NAME))) {
			return JSONUtil.getResponse(-1, "目录名称为空，处理失败！");
		}

		try {
			int code = this.resourceService.createDirectory(param);
			if (code == -1) {
				return JSONUtil.getResponse(-1, "目录名称重复！");
			}
			return JSONUtil.getResponse(1);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "创建目录异常。");
		}		
	}
	
	@RequestMapping(value = "/updateDirectory", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject updateDirectory(HttpServletRequest req, @RequestBody JSONObject param) {
		JSONObject ck = this.checkParameter(param);
		if (ck != null) {
			return ck;
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ID))) {
			return JSONUtil.getResponse(-1, "目录ID为空，处理失败！");
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_NAME))) {
			return JSONUtil.getResponse(-1, "目录名称为空，处理失败！");
		}
		try {
			int code = this.resourceService.updateDirectory(param);
			if (code == -1) {
				return JSONUtil.getResponse(-1, "目录名称重复！");
			}
			return JSONUtil.getResponse(1);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "修改目录异常。");
		}		
	}
	
	@RequestMapping(value = "/deleteDirectory", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject deleteDirectory(HttpServletRequest req, @RequestBody JSONObject param) {
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ID))) {
			return JSONUtil.getResponse(-1, "目录ID为空，处理失败！");
		}
		try {
			int code = this.resourceService.deleteDirectory(param);
			if (code == -1) {
				return JSONUtil.getResponse(-1, "该目录下存在目录或文件，无法进行删除操作。");
			}
			return JSONUtil.getResponse(1);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "删除目录异常。");
		}		
	}
	
	@RequestMapping(value = "/createResource", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject createResource(HttpServletRequest req, @RequestBody JSONObject param) {
		JSONObject ck = this.checkParameter(param);
		if (ck != null) {
			return ck;
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_NAME))) {
			return JSONUtil.getResponse(-1, "文件名称为空，处理失败！");
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ACCESSURL))) {
			return JSONUtil.getResponse(-1, "文件路径为空，处理失败！");
		}

		try {
			this.resourceService.createResource(param);
			return JSONUtil.getResponse(1);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "上传文件异常。");
		}		
	}

	@RequestMapping(value = "/updateResource", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject updateResource(HttpServletRequest req, @RequestBody JSONObject param) {
		JSONObject ck = this.checkParameter(param);
		if (ck != null) {
			return ck;
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ID))) {
			return JSONUtil.getResponse(-1, "文件ID为空，处理失败！");
		}
		try {
			int code = this.resourceService.updateResource(param);
			if (code == -1) {
				return JSONUtil.getResponse(-1, "文件名重复！");
			}
			return JSONUtil.getResponse(1);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "修改文件异常。");
		}		
	}
	
	@RequestMapping(value = "/deleteResource", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject deleteResource(HttpServletRequest req, @RequestBody JSONObject param) {
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ID))) {
			return JSONUtil.getResponse(-1, "文件ID为空，处理失败！");
		}
		try {
			this.resourceService.deleteResource(param);
			return JSONUtil.getResponse(1);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "删除文件异常。");
		}		
	}

	@RequestMapping(value = "/getResourceList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getResourceList(HttpServletRequest req, @RequestBody JSONObject param) {
		JSONObject ck = this.checkParameter(param);
		if (ck != null) {
			return ck;
		}

		try {
			List<JSONObject> list = this.resourceService.getResourceList(param);
			return JSONUtil.getResponse(1, list);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "获取文件列表异常。");
		}	
	}

	private JSONObject checkParameter(JSONObject param) {
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ACCOUNTID))) {
			return JSONUtil.getResponse(-1, "当前用户账号为空，处理失败！");
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(-1, "学校id为空，处理失败！");
		}
		if (StringUtils.isEmpty(param.get(FIELD_RESTYPE))) {
			return JSONUtil.getResponse(-1, "资源类型为空，处理失败！");
		}
		if (StringUtils.isEmpty(param.getString("parentId"))) {
			param.remove("parentId");
		}
		if (StringUtils.isEmpty(param.getString(Constants.FIELD_NAME))) {
			param.remove(Constants.FIELD_NAME);
		}
		return null;
	}
	
    public final static String FIELD_RESTYPE = "resType";
}
