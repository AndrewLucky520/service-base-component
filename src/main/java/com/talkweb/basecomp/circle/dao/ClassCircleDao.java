package com.talkweb.basecomp.circle.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description 班级圈DAO层接口
 * @author xixi1979
 * @date 2018/9/11
 */
public interface ClassCircleDao {
	/**-----发布班级圈 -----**/
	int createCircle(JSONObject param);

	/**-----班级圈关联班级 -----**/
	int createCircleClass(JSONObject param);

	/**-----新增旧版班级圈数据 -----**/
	int createTempCircle(JSONObject param);

	/**-----班级圈关联图片 -----**/
	int createCircleImage(JSONObject param);

	/**-----班级圈旧版关联图片 -----**/
	int createTempCircleImage(JSONObject param);

	/**-----新增点赞 -----**/
	int createCircleLike(JSONObject param);

	/**-----新增评论 -----**/
	int createCircleComment(JSONObject param);

	/**-----获取班级圈数 -----**/
	int getCircleCount(JSONObject param);

	/**-----获取点赞数 -----**/
	int getCircleLikeCount(JSONObject param);

	/**-----获取班级圈列表 -----**/
	List<JSONObject> getCircleList(JSONObject param);

	/**-----获取图片列表 -----**/
	List<JSONObject> getCircleImageList(JSONObject param);

	/**-----获取旧版图片列表 -----**/
	List<JSONObject> getTempCircleImageList(JSONObject param);

	/**-----获取点赞列表 -----**/
	List<JSONObject> getCircleLikeList(JSONObject param);

	/**-----获取评论列表 -----**/
	List<JSONObject> getCircleCommentList(JSONObject param);
	
	/**-----获取子评论列表 -----**/
	List<String> getSubCircleCommentList(@Param("commentId") String commentId);
	
	/**-----删除班级圈-----**/
	int deleteCircle(JSONObject param);

	/**-----删除班级圈关联班级-----**/
	int deleteCircleClass(JSONObject param);

	/**-----删除班级圈关联图片-----**/
	int deleteCircleImageList(JSONObject param);

	/**-----删除班级圈指定点赞数据-----**/
	int deleteCircleLike(JSONObject param);

	/**-----删除班级圈指定评论数据-----**/
	int deleteCircleComment(JSONObject param);

	/**-----删除班级圈点赞列表-----**/
	int deleteCircleLikeList(JSONObject param);

	/**-----删除班级圈评论列表-----**/
	int deleteCircleCommentList(JSONObject param);
	
	Long getCircleCreator(JSONObject param);
}