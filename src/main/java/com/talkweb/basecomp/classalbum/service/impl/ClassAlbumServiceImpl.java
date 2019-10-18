package com.talkweb.basecomp.classalbum.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.classalbum.dao.ClassAlbumMapper;
import com.talkweb.basecomp.classalbum.service.ClassAlbumService;

@Service
public class ClassAlbumServiceImpl implements ClassAlbumService {
	@Autowired
	ClassAlbumMapper classAlbumMapper;

	@Override
	public int createAlbum(JSONObject jsonObject) {
		jsonObject.put("createTime", getNowDate());
		return classAlbumMapper.createAlbum(jsonObject);
	}
	
	@Override
	public int checkAlbumCreator(JSONObject jsonObject) {
		return classAlbumMapper.checkAlbumCreator(jsonObject);
	}
	
	@Override
	public int editAlbumName(JSONObject jsonObject) {
		return classAlbumMapper.editAlbumName(jsonObject);
	}
	
	@Override
	public int checkAlbumExistByName(JSONObject jsonObject) {
		return classAlbumMapper.checkAlbumExistByName(jsonObject);
	}
	
	@Override
	public int addImage(JSONObject jsonObject) {
		return classAlbumMapper.addImage(jsonObject);
	}
	
	@Override
	public Map<String, String> getImageNameMap(JSONObject jsonObject) {
		return classAlbumMapper.getImageNameMap(jsonObject);
	}
	
	@Override
	public int checkImageCreator(JSONObject jsonObject) {
		return classAlbumMapper.checkImageCreator(jsonObject);
	}
	
	@Override
	public int editImageName(JSONObject param) {
		return classAlbumMapper.editImageName(param);
	}

	@Override
	public JSONObject addComment(JSONObject jsonObject) {
		JSONObject response = new JSONObject();
		jsonObject.put("createTime", getNowDate());
		classAlbumMapper.addComment(jsonObject);
		
		//返回处理
		long id = jsonObject.getLong("id");//获取评论id
		List<JSONObject> commentList = classAlbumMapper.getCommentList(jsonObject);
		JSONObject data = new JSONObject();
		data.put("commentId", id);
		data.put("commentList", commentList);
		response.put("data", data);
		return response;
	}
	
	@Override
	public int checkCommentCreator(JSONObject param) {
		return classAlbumMapper.checkCommentCreator(param);
	}
	
	@Override
	public int deleteComment(JSONObject jsonObject) {
		return classAlbumMapper.deleteComment(jsonObject);
	}
	
	@Override
	public JSONObject addLike(JSONObject jsonObject) {
		JSONObject response = new JSONObject();
		Integer value = jsonObject.getInteger("value");
		//取消点赞
		classAlbumMapper.deleteLike(jsonObject);
		if(value == 1) {
			//点赞
			jsonObject.put("createTime", getNowDate());
			classAlbumMapper.addLike(jsonObject);
		}
		//返回处理
		JSONObject data = new JSONObject();
		List<JSONObject> likeList = classAlbumMapper.getLikeList(jsonObject);
		data.put("likeList", likeList);
		response.put("data", data);
		return response;
	}
	
	@Override
	public List<String> getAlbumHome(JSONObject jsonObject) {
		return classAlbumMapper.getAlbumHome(jsonObject);
	}

	@Override
	public List<JSONObject> getAlbum(JSONObject jsonObject) {
		return classAlbumMapper.getAlbum(jsonObject);
	}

	@Override
	public JSONObject getAlbumDetail(JSONObject jsonObject) {
		return classAlbumMapper.getAlbumDetail(jsonObject);
	}
	
	@Override
	public List<JSONObject> getAlbumImagePage(JSONObject jsonObject) {
		return classAlbumMapper.getAlbumImagePage(jsonObject);
	}

	@Override
	public int deleteAlbum(JSONObject jsonObject) {
		return classAlbumMapper.deleteAlbum(jsonObject);
	}

	@Override
	public int deleteAlbumImage(JSONObject jsonObject) {
		return classAlbumMapper.deleteAlbumImage(jsonObject);
	}
	
	public static String getNowDate() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return sdf.format(date);
	}
}
