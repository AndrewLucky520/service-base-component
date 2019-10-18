package com.talkweb.basecomp.address.dao;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description 通讯录DAO层接口
 * @author xixi1979
 * @date 2018/8/1
 */
public interface AddressBookDao {

	/**-----获取机构类型列表 -----**/
	List<JSONObject> getOrgTypeList(JSONObject param);

	/**-----获取指定类型的子机构列表，必须指定机构类型 orgType -----**/
	List<JSONObject> getOrgList(JSONObject param);

	/**-----获取子机构成员列表，包括组长，必须指定子机构orgId -----**/
	List<JSONObject> getMemberList(JSONObject param);

	/**-----获取指定类型的机构所有成员列表，包括组长，必须指定机构类型orgType -----**/
	//List<JSONObject> getAllMemberList(JSONObject param);

	/**-----获取指定类型的机构组长列表，必须指定机构类型orgType  -----**/
	List<JSONObject> getLeaderList(JSONObject param);

	/**-----获取所有班主任列表 ，如果指定gradeId，则获取指定年级的所有班主任列表-----**/
	List<JSONObject> getClassLeaderList(JSONObject param);
	
	/**-----获取班主任的年级列表 -----**/
	List<JSONObject> getClassLeaderGradeList(JSONObject param);

	/**-----获取老师列表，
	 * 可指定creatorId，查询指定用户收藏的老师列表 ；
	 * 可指定key（老师名称或电话号码），查询相关的老师数据
	 */
	List<JSONObject> getTeacherList(JSONObject param);

	/**-----获取指定班级的基本信息，必须指定classId -----**/
	List<JSONObject> getClass(JSONObject param);

	/**-----获取老师任教的班级或者班主任管理的班级列表，必须指定老师的userId -----**/
	List<JSONObject> getTeacherClassList(JSONObject param);

	/**-----获取年级列表 -----**/
	List<JSONObject> getGradeList(JSONObject param);

	/**-----获取指定年级的班级列表，必须指定年级Id -----**/
	List<JSONObject> getClassList(JSONObject param);

	/**-----获取学生列表。
	 * 指定userId，获取老师任教相关的班级学生列表；
	 * 指定classId，获取指定班级的学生列表；
	 * 指定 creatorId，查询指定用户收藏的学生列表
	 * 指定gradeId，查询指定年级的学生列表
	 * 指定key（学生名称或电话号码），查询相关的学生列表
	 */
	List<JSONObject> getStudentList(JSONObject param);

	/**-----获取学生家长列表。
	 * 指定userId，获取老师任教相关的班级学生家长列表；
	 * 指定classId，获取指定班级的学生家长列表；
	 * 指定gradeId，查询指定年级的学生家长列表
	 * 指定studentId，查询指定学生的家长列表
	 */
	List<JSONObject> getParentList(JSONObject param);

	/**-----获取学生家长列表。
	 * 指定userId，获取老师任教相关的班级学生家长列表；
	 * 指定classId，获取指定班级的学生家长列表；
	 * 指定gradeId，查询指定年级的学生家长列表
	 * 指定studentId，查询指定学生的家长列表
	 */
	List<JSONObject> getParentListForMessage(JSONObject param);

	/**-----获取班级相关的老师列表，包括任课老师和班主任，可指定key（老师名称或电话号码），查询相关的老师数据-----**/
	List<JSONObject> getClassTeacherList(JSONObject param);

	/**-----收藏用户到我的通讯录 -----**/
	int createMyAddressBook(JSONObject param);
	
	/**-----查询我的通讯录。可指定成员账号编码memberAccId，查询是否已收藏了指定用户 -----**/
	List<JSONObject> findMyAddressBook(JSONObject param);

	/**-----取消收藏-----**/
	int deleteMyAddressBook(JSONObject param);

	/**-----获取子机构成员数，包括组长，必须指定子机构orgId -----**/
	int getMemberCount(JSONObject param);

	/**-----获取指定类型的机构组长人数，必须指定机构类型orgType -----**/
	int getLeaderCount(JSONObject param);

	/**-----获取所有班主任人数 -----**/
	int getClassLeaderCount(JSONObject param);

	/**-----获取所有老师人数-----**/
	int getTeacherCount(JSONObject param);

	/**-----获取班级对应的老师人数，包括任课老师和班主任-----**/
	int getClassTeacherCount(JSONObject param);

	/**-----获取学校所有学生人数 -----**/
	int getStudentCount(JSONObject param);
	
	int getMessageAdmin(JSONObject param);
}