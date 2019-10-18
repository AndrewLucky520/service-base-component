package com.talkweb.basecomp.common.data;

import java.util.Collection;
import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

@FeignClient(value = "SERVICE-BASEDATA")
public interface BaseDataInterface {
	/**
	 * 获取学校机构列表
	 * @param <E>
	 */
	@RequestMapping(value = "/BaseDataService/getSimpleSchoolBatch", method = RequestMethod.POST)
	@ResponseBody
	<E> List<JSONObject> getSimpleSchoolBatch(@RequestBody List<Long> schoolIds,
			@RequestParam("termInfoId") String termInfoId);

	/**
	 * 获取当前学年学期信息
	 */
	@RequestMapping(value = "/BaseDataService/getCurrentTermInfoId", method = RequestMethod.GET)
	@ResponseBody
	JSONObject getCurrentTermInfoId();

	/**
	 * 获取当前学年学期信息
	 */
	@RequestMapping(value = "/BaseDataService/getFirstTermInfoId", method = RequestMethod.GET)
	@ResponseBody
	JSONObject getFirstTermInfoId();

	/**
	 * 获取学校指定年级班级列表
	 */
	@RequestMapping(value = "/BaseDataService/getClassList", method = RequestMethod.POST)
	@ResponseBody
	List<JSONObject> getClassList(@RequestBody JSONObject param);

	/**
	 * 获取账号详情
	 * 
	 * @param <E>
	 */
	@RequestMapping(value = "/BaseDataService/updateClassroom", method = RequestMethod.POST)
	void updateClassroom(@RequestParam("schoolId") Long schoolId, @RequestBody JSONObject classObj,
			@RequestParam("termInfoId") String termInfoId);

	/**
	 * 获取学校老师列表
	 */
	@RequestMapping(value = "/BaseDataService/getCourseTeacherList2", method = RequestMethod.POST)
	@ResponseBody
	List<JSONObject> getCourseTeacherList(@RequestBody JSONObject param);

	/**
	 * 获取学校指定年级班级列表
	 */
	@RequestMapping(value = "/BaseDataService/getGradeList", method = RequestMethod.POST)
	@ResponseBody
	List<JSONObject> getGradeList(
			@RequestBody JSONObject school, 
			@RequestParam("termInfoId") String termInfoId);

	/**
	 * 获取学校教师列表
	 */
	@RequestMapping(value = "/BaseDataService/getAllSchoolEmployees", method = RequestMethod.POST)
	@ResponseBody
	List<JSONObject> getAllSchoolEmployees(@RequestParam("name") String name,
			@RequestBody JSONObject school, 
			@RequestParam("termInfoId") String termInfoId);

	/**
	 * 获取学校班主任列表
	 */
	@RequestMapping(value = "/BaseDataService/getDeanList", method = RequestMethod.POST)
	@ResponseBody
	List<JSONObject> getDeanList(@RequestBody JSONObject param);

	/**
	 * 获取账号详情
	 * 
	 * @param <E>
	 */
	@RequestMapping(value = "/BaseDataService/getAccountBatch", method = RequestMethod.POST)
	@ResponseBody
	<E> List<JSONObject> getAccountBatch(@RequestBody Collection<E> accountIds, @RequestParam("schoolId") Long schoolId,
			@RequestParam("termInfoId") String termInfoId);

	/**
	 * 获取用户详情
	 * 
	 * @param <E>
	 */
	@RequestMapping(value = "/BaseDataService/getUserBatch", method = RequestMethod.POST)
	@ResponseBody
	<E> List<JSONObject> getUserBatch(@RequestBody Collection<E> userIds, @RequestParam("schoolId") Long schoolId,
			@RequestParam("termInfoId") String termInfoId);

	/**
	 * 获取家长详情
	 * 
	 * @param <E>
	 */
	@RequestMapping(value = "/BaseDataService/getSimpleParentByStuMsg", method = RequestMethod.POST)
	@ResponseBody
	<E> List<JSONObject> getSimpleParentByStuMsg(@RequestBody Collection<E> accountIds,
			@RequestParam("schoolId") Long schoolId, @RequestParam("termInfoId") String termInfoId);

	/**
	 * 获取班级详情
	 * 
	 * @param <E>
	 */
	@RequestMapping(value = "/BaseDataService/getClassroomBatch", method = RequestMethod.POST)
	@ResponseBody
	<E> List<JSONObject> getClassroomBatch(@RequestBody Collection<E> ids, @RequestParam("schoolId") Long schoolId,
			@RequestParam("termInfoId") String termInfoId);

	/**
	 * 获取学校指定年级班级列表
	 */
	@RequestMapping(value = "/BaseDataService/getSimpleClassListBy", method = RequestMethod.POST)
	@ResponseBody
	List<JSONObject> getSimpleClassList(@RequestBody JSONObject param);

	/**
	 * 获取学校机构列表
	 */
	@RequestMapping(value = "/BaseDataService/getSchoolOrgList2", method = RequestMethod.POST)
	@ResponseBody
	List<JSONObject> getSchoolOrgList2(@RequestParam("schoolId") Long schoolId,
			@RequestParam("termInfoId") String termInfoId);

	/**
	 * 获取学校指定年级班级列表
	 */
	@RequestMapping(value = "/BaseDataService/getStudentList2", method = RequestMethod.POST)
	@ResponseBody
	List<JSONObject> getStudentList(@RequestBody JSONObject param);

	/**
	 * 获取学校指定年级班级列表
	 */
	@RequestMapping(value = "/BaseDataService/getSimpleStudentList", method = RequestMethod.POST)
	@ResponseBody
	List<JSONObject> getSimpleStudentList(@RequestBody JSONObject param);

	/**
	 * 获取学校学年学期科目列表
	 */
	@RequestMapping(value = "/lesson/listLessonBySchoolId", method = RequestMethod.GET)
	@ResponseBody
	List<JSONObject> getLessonInfoList(@RequestParam("schoolId") Long schoolId,
			@RequestParam("termInfo") String termInfo);

	/**
	 * 获取学校对象
	 */
	@RequestMapping(value = "/school/getSchoolByExtId", method = RequestMethod.GET)
	@ResponseBody
	JSONObject getSchoolByExtId(@RequestParam("extSchoolId") String extSchoolId,
			@RequestParam("termInfo") String termInfo);

	/**
	 * 获取账号列表
	 * 
	 * @param <E>
	 */
	@RequestMapping(value = "/account/listAccountByExtUserId", method = RequestMethod.GET)
	@ResponseBody
	<E> List<JSONObject> listAccountByExtUserId(@RequestBody Collection<E> extUserIds,
			@RequestParam("termInfo") String termInfo);

	/**
	 * 获取账号列表
	 * 
	 * @param <E>
	 */
	@RequestMapping(value = "/account/listAccountById", method = RequestMethod.GET)
	@ResponseBody
	<E> List<JSONObject> listAccountById(@RequestParam("ids") List<Long> ids,
			@RequestParam("termInfo") String termInfo);

}
