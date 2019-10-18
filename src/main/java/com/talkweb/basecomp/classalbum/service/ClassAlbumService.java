package com.talkweb.basecomp.classalbum.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public interface ClassAlbumService {
	/**
	 * 创建相册
	 * @param jsonObject
	 * @return
	 */
	public int createAlbum(JSONObject jsonObject);
	/**
	 * 检查相册创建人是否为自己
	 * @param jsonObject
	 * @return
	 */
	public int checkAlbumCreator(JSONObject jsonObject);
	/**
	 * 修改相册名称
	 * @param jsonObject
	 * @return
	 */
	public int editAlbumName(JSONObject jsonObject);
	/**
	 * 查询特定名称的相册是否存在
	 * @param param
	 * @return
	 */
	public int checkAlbumExistByName(JSONObject param);
	/**
	 * 添加相册图片
	 * @param jsonObject
	 * @return
	 */
	public int addImage(JSONObject jsonObject);
	/**
	 * 获取同意相册下的图片名称列表
	 * @param jsonObject
	 * @return
	 */
	public Map<String,String> getImageNameMap(JSONObject jsonObject);
	/**
	 * 检查图片创建人是否为自己
	 * @param jsonObject
	 * @return
	 */
	public int checkImageCreator(JSONObject jsonObject);
	/**
	 * 修改单个图片名称
	 * @param param
	 * @return
	 */
	public int editImageName(JSONObject param);
	/**
	 * 添加评论
	 * @param jsonObject
	 * @return
	 */
	public JSONObject addComment(JSONObject jsonObject);
	/**
	 * 检查是否为评论创建人
	 * @param param
	 * @return
	 */
	public int checkCommentCreator(JSONObject param);
	/**
	 * 删除评论
	 * @param jsonObject
	 * @return
	 */
	public int deleteComment(JSONObject jsonObject);
	/**
	 * 点赞
	 * @param jsonObject
	 * @return
	 */
	public JSONObject addLike(JSONObject jsonObject);
	/**
	 * 获取相册首页
	 * @param jsonObject
	 * @return
	 */
	public List<String> getAlbumHome(JSONObject jsonObject);
	/**
	 * 获取相册
	 * @param jsonObject
	 * @return
	 */
	public List<JSONObject> getAlbum(JSONObject jsonObject);
	/**
	 * 获取相册详情
	 * @param jsonObject
	 * @return
	 */
	public JSONObject getAlbumDetail(JSONObject jsonObject);
	/**
	 * 获取相册详情 图片分页列表
	 * @param jsonObject
	 * @return
	 */
	public List<JSONObject> getAlbumImagePage(JSONObject jsonObject);
	/**
	 * 删除相册
	 * @param jsonObject
	 * @return
	 */
	public int deleteAlbum(JSONObject jsonObject);
	/**
	 * 删除相册图片
	 * @param jsonObject
	 * @return
	 */
	public int deleteAlbumImage(JSONObject jsonObject);
}
