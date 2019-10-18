package com.talkweb.basecomp.classalbum.controller;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.classalbum.service.ClassAlbumService;
import com.talkweb.basecomp.classalbum.service.impl.ClassAlbumServiceImpl;
import com.talkweb.basecomp.common.inter.DocumentInterface;

@RequestMapping("/classAlbum")
@Controller
public class ClassAlbumController {
	@Autowired
	private ClassAlbumService classAlbumService;
	
	@Autowired
	private DocumentInterface documentInterface;
	
//	@Autowired
//	private BasePlatformService basePlatformService;
	
//	@Autowired
//	private RedisUtils redisUtils;

	private Logger log = Logger.getLogger(ClassAlbumController.class);

	/**
	 * 创建相册
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/createAlbum", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject createAlbum(@RequestBody JSONObject param){
		JSONObject response = new JSONObject();
		
		//查询特定名称的相册是否存在
		if(classAlbumService.checkAlbumExistByName(param) > 0) {
			return setPromptMessage(response,1,RETURN_MSG_ALBUM_ISEXIST);
		}
		
		//该字段为保留，默认设置 0
		param.put("type", 0);
		classAlbumService.createAlbum(param);
		
		//返回处理
		long id = param.getLong("id");//获取相册id
		JSONObject data = new JSONObject();
		data.put("albumId", id);
		response.put("data", data);
		return setPromptMessage(response,0,RETURN_MSG_SUCCESS);
	}
	
	/**
	 * 修改相册名称
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/changeAlbumName", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject changeAlbumName(@RequestBody JSONObject param){
		JSONObject response = new JSONObject();
		if(classAlbumService.checkAlbumCreator(param) == 0) {
			//不是创建人
			return setPromptMessage(response,2,RETURN_MSG_NO_CHANGE_PERMISSION);
		}
		//查询特定名称的相册是否存在
		if(classAlbumService.checkAlbumExistByName(param) > 0) {
			return setPromptMessage(response,1,RETURN_MSG_ALBUM_ISEXIST);
		}
		classAlbumService.editAlbumName(param);		
		return setPromptMessage(response,0,RETURN_MSG_SUCCESS);
	}
	
	/**
	 * 添加图片
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/addImage", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject addImage(@RequestBody JSONObject param){
		JSONObject response = new JSONObject();
		
		Long albumId = param.getLong("albumId");
		if(albumId == null) {
			return setPromptMessage(response,-1,RETURN_MSG_ERROR);
		}
		JSONArray imageList = param.getJSONArray("imageList");
		if(imageList == null || imageList.size() == 0) {
			return setPromptMessage(response,-1,RETURN_MSG_ERROR);
		} else {
			Map<String,String> imageNameMap = classAlbumService.getImageNameMap(param);
			
			//检查是否重名
			JSONArray newImageList = new JSONArray();
			for(int i=0; i<imageList.size(); i++) {
				JSONObject imageInfo = imageList.getJSONObject(i);
				String imageName = imageInfo.getString("name");
				if(imageNameMap.containsKey(imageName)) {
					return setPromptMessage(response,-1,RETURN_MSG_ALBUM_IMAGE_ISEXIST);
				} else {
					imageNameMap.put(imageName, imageName);
				}
			}
				
			//图片压缩
			boolean compressSuccess = imageCompress(param);
			if(compressSuccess) {
				//压缩成功
				imageList = param.getJSONArray("imageList");
			}
			for(int i=0; i<imageList.size(); i++) {
				JSONObject imageInfo = imageList.getJSONObject(i);
				if(!compressSuccess) {
					//压缩失败时，设置缩略图为原图
					imageInfo.put("thumFileId", imageInfo.getString("fileId"));
					imageInfo.put("thumAccessUrl", imageInfo.getString("accessUrl"));
				}
				imageInfo.put("albumId", albumId);
				imageInfo.put("creator", param.getString("creator"));
				imageInfo.put("role", param.getInteger("role"));
				imageInfo.put("creatorName", param.getString("creatorName"));
				imageInfo.put("createTime", ClassAlbumServiceImpl.getNowDate());
				imageInfo.put("extUserId", param.getString("extUserId"));
				imageInfo.put("classId",param.getString("classId"));
				imageInfo.put("schoolId", param.getString("schoolId"));
				
				newImageList.add(imageInfo);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			param.put("imageList", newImageList);
		}
		
		classAlbumService.addImage(param);
		return setPromptMessage(response,0,RETURN_MSG_SUCCESS);
	}
	
	private boolean imageCompress(JSONObject param) {
		log.info("imageCompress start param : " + param);
		long startTime = System.currentTimeMillis();
		try {
			JSONObject result = documentInterface.compress(param);
			log.info("imageCompress result : " + result);
			if("1".equals(result.getString("code"))) {
				JSONArray datas =result.getJSONArray("data");
				JSONArray imageList = param.getJSONArray("imageList");//原数据
				JSONArray newImageList = new JSONArray();
				for(int i=0; i<imageList.size();i++) {
					JSONObject imageInfo = imageList.getJSONObject(i);
					imageInfo.put("thumFileId", datas.getJSONObject(i).getString("fileId"));
					imageInfo.put("thumAccessUrl", datas.getJSONObject(i).getString("accessUrl"));
					newImageList.add(imageInfo);
				}
				param.put("imageList", newImageList);
				log.info("imageCompress end param : " + param);
				long totalTime = System.currentTimeMillis() - startTime;
				log.info("imageCompress total time : " + totalTime + " ms " + (totalTime)/1000 + " s");
				return true;
			}
		} catch (Exception e) {
			log.info("imageCompress Exception : " + e.toString());
		}
		return false;
	}
	
	/**
	 * 修改单个图片名称
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/changeImageName", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject changeImageName(@RequestBody JSONObject param){
		JSONObject response = new JSONObject();
		if(classAlbumService.checkImageCreator(param) == 0) {
			//不是创建人
			return setPromptMessage(response,0,RETURN_MSG_NO_CHANGE_PERMISSION);
		}
		
		//检查是否存在重名
		String imageName = param.getString("name");
		Map<String,String> imageNameMap = classAlbumService.getImageNameMap(param);
		if(imageNameMap.containsKey(imageName)) {
			return setPromptMessage(response,1,RETURN_MSG_ALBUM_IMAGE_ISEXIST);
		}
		classAlbumService.editImageName(param);
		return setPromptMessage(response,0,RETURN_MSG_SUCCESS);
	}
	
	
	/**
	 * 添加评论
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/addComment", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject addComment(@RequestBody JSONObject param){
		JSONObject response = new JSONObject();
		String content = param.getString("content");
		if(StringUtils.isEmpty(content)) {
			return setPromptMessage(response,-1,RETURN_MSG_COMMENT_CONTENT_IS_MUST);
		} else if(content.length() > COMMENT_CONTENT_MAX) {
			return setPromptMessage(response,-1,RETURN_MSG_COMMENT_CONTENT_OVER);
		}
		
		response = classAlbumService.addComment(param);
		return setPromptMessage(response,0,RETURN_MSG_SUCCESS);
	}
	
	/**
	 * 删除评论
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/deleteComment", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject deleteComment(@RequestBody JSONObject param){
		JSONObject response = new JSONObject();
		//相册创建者，班主任。发布评论的本人拥有评论删除权限
		boolean isPermission = false;
		if(classAlbumService.checkAlbumCreator(param) > 0) {
			//是相册创建人
			isPermission = true;
		} else if(classAlbumService.checkCommentCreator(param) > 0) {
			//评论创建人
			isPermission = true;
		} else {
			Integer isDean = param.getInteger("isDean");
			if(isDean != null && isDean == 1) {
				//班主任
				isPermission = true;
			}
		}

		if(isPermission) {
			classAlbumService.deleteComment(param);
			return setPromptMessage(response,0,RETURN_MSG_SUCCESS);
		}
		return setPromptMessage(response,2,RETURN_MSG_NO_DELETE_PERMISSION);
	}
	
	/**
	 * 点赞
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/addLike", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject addLike(@RequestBody JSONObject param){
		JSONObject response = new JSONObject();
		Integer value = param.getInteger("value");
		if(value == null) {
			return setPromptMessage(response,-1,RETURN_MSG_ERROR);
		}
		response = classAlbumService.addLike(param);
		return setPromptMessage(response,0,RETURN_MSG_SUCCESS);
	}
	
	/**
	 * 获取相册首页
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/getAlbumHome", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getAlbumHome(@RequestBody JSONObject param){
		JSONObject response = new JSONObject();
		param.put("limit", HOME_IMAGE_LIMIT);
		List<String> data = classAlbumService.getAlbumHome(param);
		response.put("data", data);
		return setPromptMessage(response,0,RETURN_MSG_SUCCESS);
	}
	
	/**
	 * 获取相册
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/getAlbum", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getAlbum(@RequestBody JSONObject param){
		JSONObject response = new JSONObject();
		List<JSONObject> data = classAlbumService.getAlbum(param);
		response.put("data", data);
		return setPromptMessage(response,0,RETURN_MSG_SUCCESS);
	}
	
	/**
	 * 获取相册详情
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/getAlbumDetail", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getAlbumDetail(@RequestBody JSONObject param){
		JSONObject response = new JSONObject();

		//获取相册相册数据
		JSONObject data = classAlbumService.getAlbumDetail(param);
		
		//分页参数
		Integer page = param.getIntValue("page");
		Integer pageSize = param.getIntValue("pageSize");
		page = (page == null || page <=0) ? 1:page;
		pageSize = (pageSize == null || pageSize <=0 ) ? 10 : pageSize;
		int position = (page -1 ) * pageSize;
		param.put("position", position);
		param.put("pageSize", pageSize);
		
		//获取相册详情 图片分页列表
		List<JSONObject> imagePage = classAlbumService.getAlbumImagePage(param);
		data.put("ImageList", imagePage);
		
		response.put("data", data);
		return setPromptMessage(response,0,RETURN_MSG_SUCCESS);
	}
	
	/**
	 * 删除相册
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/deleteAlbum", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject deleteAlbum(@RequestBody JSONObject param){
		JSONObject response = new JSONObject();
		boolean isPermission = false;
		Integer isDean = param.getInteger("isDean");
		if(isDean != null && isDean == 1) {
			//班主任
			isPermission = true;
		} else if(classAlbumService.checkAlbumCreator(param) > 0) {
			//是创建人
			isPermission = true;
		}
		if(isPermission) {
			classAlbumService.deleteAlbum(param);
			return setPromptMessage(response,0,RETURN_MSG_SUCCESS);
		}
		return setPromptMessage(response,1,RETURN_MSG_NO_DELETE_PERMISSION);
	}
	
	/**
	 * 删除相册图片
	 * @param param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/deleteAlbumImage", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject deleteAlbumImage(@RequestBody JSONObject param){
		JSONObject response = new JSONObject();
		List<Long> imageIds = (List<Long>) param.get("imageIds");
		if(imageIds == null || imageIds.size() == 0) {
			return setPromptMessage(response,-1,RETURN_MSG_ERROR);
		}
		classAlbumService.deleteAlbumImage(param);
		return setPromptMessage(response,0,RETURN_MSG_SUCCESS);
	}
	
	private static final int HOME_IMAGE_LIMIT = 3;
	private static final int COMMENT_CONTENT_MAX = 100;

	private static final String RETURN_MSG_SUCCESS = "操作成功";
	private static final String RETURN_MSG_ERROR = "参数错误，请联系管理员！";
	private static final String RETURN_MSG_ALBUM_ISEXIST = "相册名称重复";
	private static final String RETURN_MSG_ALBUM_IMAGE_ISEXIST = "相册图片名称重复";
	private static final String RETURN_MSG_COMMENT_CONTENT_IS_MUST = "评论内容为必填";
	private static final String RETURN_MSG_COMMENT_CONTENT_OVER = "评论内容超长";
	private static final String RETURN_MSG_NO_CHANGE_PERMISSION = "没有修改权限";
	private static final String RETURN_MSG_NO_DELETE_PERMISSION = "没有删除权限";
	private JSONObject setPromptMessage(JSONObject object, int code, String message) {
		object.put("code", code);
		object.put("msg", message);
		return object;
	}
}
