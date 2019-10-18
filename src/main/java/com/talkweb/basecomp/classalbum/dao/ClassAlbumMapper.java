package com.talkweb.basecomp.classalbum.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;

import com.alibaba.fastjson.JSONObject;

public interface ClassAlbumMapper {
	/**
	 * 添加班级相册
	 * @param param
	 * @return
	 */
	public int createAlbum(JSONObject param);
	/**
	 * 检查相册创建人是否为自己
	 * @param param
	 * @return
	 */
	public int checkAlbumCreator(JSONObject param);
	/**
	 * 修改相册名称
	 * @param param
	 * @return
	 */
	public int editAlbumName(JSONObject param);
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
	@MapKey("name")
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
	 * @param param
	 * @return
	 */
	public int addComment(JSONObject param);
	/**
	 * 获取评论列表
	 * @param jsonObject
	 * @return
	 */
	public List<JSONObject> getCommentList(JSONObject jsonObject);
	/**
	 * 检查是否为评论创建人
	 * @param param
	 * @return
	 */
	public int checkCommentCreator(JSONObject param);
	/**
	 * 删除评论
	 * @param param
	 * @return
	 */
	public int deleteComment(JSONObject param);
	/**
	 * 点赞
	 * @param param
	 * @return
	 */
	public int addLike(JSONObject param);
	/**
	 * 取消点赞
	 * @param param
	 * @return
	 */
	public int deleteLike(JSONObject param);
	/**
	 * 获取点赞列表
	 * @param jsonObject
	 * @return
	 */
	public List<JSONObject> getLikeList(JSONObject jsonObject);
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
