package com.talkweb.basecomp.sportsmeet.controller;

import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.talkweb.basecomp.sportsmeet.excel.MemberExcelData;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.Constants;
import com.talkweb.basecomp.common.data.DataCache;
import com.talkweb.basecomp.common.data.DataUtil;
import com.talkweb.basecomp.common.util.JSONUtil;
import com.talkweb.basecomp.sportsmeet.excel.EventsExcelData;
import com.talkweb.basecomp.sportsmeet.service.SportsMeetService;

/**
 * 
* @Title: SportsMeetController.java 
* @Description: 赛事管理
* @author YuanBingkun14307  
* @date 2019年8月8日 上午9:51:36 
 */
@RestController
@RequestMapping("/sportsmeet")
public class SportsMeetController {
	static final Logger log = LoggerFactory.getLogger(SportsMeetController.class);

	@Autowired
	private SportsMeetService sportMeetService;
	@Autowired
	private DataCache dataCache;
	
	@RequestMapping(value = "/getSportsList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getSportsList(HttpServletRequest req, @RequestBody JSONObject param) {
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(-1, "学校id为空，处理失败！");
		}
		
		try {
			List<JSONObject> list = this.sportMeetService.getSportsList(param);
			return JSONUtil.getResponse(1, list);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "获取赛事列表异常。");
		}
	}
	
	@RequestMapping(value = "/createSports", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject createSports(HttpServletRequest req, @RequestBody JSONObject param) {
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(-1, "学校id为空，处理失败！");
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ACCOUNTID))) {
			return JSONUtil.getResponse(-1, "学校id为空，处理失败！");
		}
		if (StringUtils.isEmpty(param.get("gradeIds"))) {
			return JSONUtil.getResponse(-1, "赛事关联年级为空，处理失败！");
		}
		if (StringUtils.isEmpty(param.get("sportsName"))) {
			return JSONUtil.getResponse(-1, "学校id为空，处理失败！");
		}
		if (StringUtils.isEmpty(param.get("startTime"))) {
			return JSONUtil.getResponse(-1, "开始时间为空，处理失败！");
		}
		if (StringUtils.isEmpty(param.get("endTime"))) {
			return JSONUtil.getResponse(-1, "结束时间为空，处理失败！");
		}

		try {
			this.sportMeetService.createSports(param);
			return JSONUtil.getResponse(1);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "创建赛事异常。");
		}
	}

	@RequestMapping(value = "/updateSports", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject updateSports(HttpServletRequest req, @RequestBody JSONObject param) {
		JSONObject ck = this.checkSports(param);
		if (ck != null) {
			return ck;
		}
		if (StringUtils.isEmpty(param.get("gradeIds"))) {
			return JSONUtil.getResponse(-1, "赛事关联年级为空，处理失败！");
		}
		if (StringUtils.isEmpty(param.get("sportsName"))) {
			return JSONUtil.getResponse(-1, "学校id为空，处理失败！");
		}
		if (StringUtils.isEmpty(param.get("startTime"))) {
			return JSONUtil.getResponse(-1, "开始时间为空，处理失败！");
		}
		if (StringUtils.isEmpty(param.get("endTime"))) {
			return JSONUtil.getResponse(-1, "结束时间为空，处理失败！");
		}

		try {
			this.sportMeetService.updateSports(param);
			return JSONUtil.getResponse(1);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "修改赛事信息异常。");
		}
	}

	@RequestMapping(value = "/deleteSports", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject deleteSports(HttpServletRequest req, @RequestBody JSONObject param) {
		JSONObject ck = this.checkSports(param);
		if (ck != null) {
			return ck;
		}

		try {
			this.sportMeetService.deleteSports(param);
			return JSONUtil.getResponse(1);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "删除异常。");
		}
	}

	@RequestMapping(value = "/getEventsList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getEventsList(HttpServletRequest req, @RequestBody JSONObject param) {
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(-1, "学校id为空，处理失败！");
		}

		try {
			List<JSONObject> list = this.sportMeetService.getEventsList(param);
			return JSONUtil.getResponse(1, list);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "获取项目列表异常。");
		}
	}

	@RequestMapping(value = "/createEvents", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject createEvents(HttpServletRequest req, @RequestBody JSONObject param) {
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(-1, "学校id为空，处理失败！");
		}
		if (StringUtils.isEmpty(param.get("eventsName"))) {
			return JSONUtil.getResponse(-1, "项目名称为空，处理失败！");
		}
		if (StringUtils.isEmpty(param.get("type"))) {
			return JSONUtil.getResponse(-1, "项目类型为空，处理失败！");
		}

		try {
			int code = this.sportMeetService.createEvents(param);
			if (code == -1) {
				return JSONUtil.getResponse(-1, "项目重名。");
			}
			return JSONUtil.getResponse(1);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "创建项目异常。");
		}
	}

	@RequestMapping(value = "/deleteEvents", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject deleteEvents(HttpServletRequest req, @RequestBody JSONObject param) {
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(-1, "学校id为空，处理失败！");
		}

		try {
			this.sportMeetService.deleteEvents(param);
			return JSONUtil.getResponse(1);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "删除项目异常。");
		}
	}

	@RequestMapping(value = "/updateEvents", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject updateEvents(HttpServletRequest req, @RequestBody JSONObject param) {
		if (StringUtils.isEmpty(param.get("eventsId"))) {
			return JSONUtil.getResponse(-1, "项目Id为空，处理失败！");
		}
		if (StringUtils.isEmpty(param.get("eventsName"))) {
			return JSONUtil.getResponse(-1, "项目名称为空，处理失败！");
		}

		try {
			this.sportMeetService.updateEvents(param);
			return JSONUtil.getResponse(1);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "修改项目异常。");
		}
	}

	@RequestMapping(value = "/getSportsGradeList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getSportsGradeList(HttpServletRequest req, @RequestBody JSONObject param) {
		JSONObject ck = this.checkSports(param);
		if (ck != null) {
			return ck;
		}

		try {
			List<JSONObject> list = this.sportMeetService.getSportsGradeList(param);
			return JSONUtil.getResponse(1, list);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "获取赛事关联年级列表异常。");
		}
	}

	@RequestMapping(value = "/getGradeList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getGradeList(HttpServletRequest req, @RequestBody JSONObject param) {
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(-1, "学校id为空，处理失败！");
		}

		try {
			List<JSONObject> list = this.sportMeetService.getGradeList(param);
			return JSONUtil.getResponse(1, list);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "获取学校年级列表异常。");
		}
	}

	@RequestMapping(value = "/getClassList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getClassList(HttpServletRequest req, @RequestBody JSONObject param) {
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(-1, "学校id为空，处理失败！");
		}

		try {
			List<JSONObject> list = this.sportMeetService.getClassList(param);
			return JSONUtil.getResponse(1, list);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "获取年级关联班级列表异常。");
		}
	}

	@RequestMapping(value = "/getStudentList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getStudentList(HttpServletRequest req, @RequestBody JSONObject param) {
		JSONObject ck = this.checkEvents(param);
		if (ck != null) {
			return ck;
		}
		if (StringUtils.isEmpty(param.get("classId"))) {
			return JSONUtil.getResponse(-1, "班级ID为空，处理失败！");
		}

		try {
			List<JSONObject> list = this.sportMeetService.getStudentList(param);
			return JSONUtil.getResponse(1, list);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "获取未报名学生列表异常。");
		}
	}

	@RequestMapping(value = "/getMemberList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getMemberList(HttpServletRequest req, @RequestBody JSONObject param) {
		JSONObject ck = this.checkSports(param);
		if (ck != null) {
			return ck;
		}

		try {
			List<JSONObject> list = this.sportMeetService.getMemberList(param);
			JSONObject ret = JSONUtil.getResponse(1, list);
			ret.put("total", this.sportMeetService.getMemberCount(param));
			return ret;
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "获取报名列表异常。");
		}
	}

	@RequestMapping(value = "/createMember", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject createMember(HttpServletRequest req, @RequestBody JSONObject param) {
		JSONObject ck = this.checkEvents(param);
		if (ck != null) {
			return ck;
		}
		if (StringUtils.isEmpty(param.get("studentIds"))) {
			return JSONUtil.getResponse(-1, "学生ID为空，处理失败！");
		}

		try {
			this.sportMeetService.createMember(param);
			return JSONUtil.getResponse(1);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "报名运动会异常。");
		}	
	}

	@RequestMapping(value = "/deleteMember", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject deleteMember(HttpServletRequest req, @RequestBody JSONObject param) {
		JSONObject ck = this.checkSports(param);
		if (ck != null) {
			return ck;
		}

		try {
			this.sportMeetService.deleteMember(param);
			return JSONUtil.getResponse(1);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "取消报名异常。");
		}
	}

	@RequestMapping(value = "/getScheduleList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getScheduleList(HttpServletRequest req, @RequestBody JSONObject param) {
		JSONObject ck = this.checkSports(param);
		if (ck != null) {
			return ck;
		}

		try {
			List<JSONObject> list = this.sportMeetService.getScheduleList(param);
			return JSONUtil.getResponse(1, list);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "获取赛程列表异常。");
		}
	}

	@RequestMapping(value = "/createSchedule", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject createSchedule(HttpServletRequest req, @RequestBody JSONObject param) {
		JSONObject ck = this.checkEvents(param);
		if (ck != null) {
			return ck;
		}
		if (StringUtils.isEmpty(param.get("startTime"))) {
			return JSONUtil.getResponse(-1, "开始时间为空，处理失败！");
		}

		try {
			this.sportMeetService.createSchedule(param);
			return JSONUtil.getResponse(1);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "创建赛程异常。");
		}
	}

	@RequestMapping(value = "/deleteSchedule", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject deleteSchedule(HttpServletRequest req, @RequestBody JSONObject param) {
		JSONObject ck = this.checkSports(param);
		if (ck != null) {
			return ck;
		}

		try {
			this.sportMeetService.deleteSchedule(param);
			return JSONUtil.getResponse(1);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "删除赛程异常。");
		}
	}

	@RequestMapping(value = "/updateSchedule", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject updateSchedule(HttpServletRequest req, @RequestBody JSONObject param) {
		JSONObject ck = this.checkEvents(param);
		if (ck != null) {
			return ck;
		}

		try {
			this.sportMeetService.createSchedule(param);
			return JSONUtil.getResponse(1);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "修改赛程异常。");
		}
	}

	@RequestMapping(value = "/getRecordList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getRecordList(HttpServletRequest req, @RequestBody JSONObject param) {
		JSONObject ck = this.checkSports(param);
		if (ck != null) {
			return ck;
		}

		try {
			List<JSONObject> list = this.sportMeetService.getRecordList(param);
			JSONObject ret = JSONUtil.getResponse(1, list);
			ret.put("total", this.sportMeetService.getMemberCount(param));
			return ret;
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "获取成绩列表异常。");
		}
	}

	@RequestMapping(value = "/updateRecord", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject updateRecord(HttpServletRequest req, @RequestBody JSONObject param) {
		JSONObject ck = this.checkSports(param);
		if (ck != null) {
			return ck;
		}
		if (StringUtils.isEmpty(param.get("recordList"))) {
			return JSONUtil.getResponse(-1, "项目成绩为空，处理失败！");
		}

		try {
			this.sportMeetService.createRecord(param);
			return JSONUtil.getResponse(1);
		} catch(Exception ex) {
			log.error("操作异常。", ex);
			return JSONUtil.getResponse(-1, "修改成绩异常。");
		}
	}

	@RequestMapping(value = "/importRecord")
	@ResponseBody
	public JSONObject importRecord(@RequestParam("importFile") MultipartFile file,@RequestParam("param") String paramStr) {
		JSONObject param =  JSONObject.parseObject(paramStr);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(-1, "学校id为空，处理失败！");
		}
		if (StringUtils.isEmpty(param.get("sportsId"))) {
			return JSONUtil.getResponse(-1, "赛事id为空，处理失败！");
		}
		String fileName = file.getOriginalFilename();
		String suffix = FilenameUtils.getExtension(fileName).toLowerCase();
		if (suffix.equalsIgnoreCase("xls") || suffix.equalsIgnoreCase("xlsx")) {
			try {
				List<JSONObject> list = sportMeetService.getScheduleList(param);

				Map<String, String> map = DataUtil.getKeyValueMap(list, "eventsName", "eventsId");
				List<String> titles = new ArrayList<>(Arrays.asList("项目名称", "班级","姓名","成绩"));
				List<Integer> needs = new ArrayList<>(Arrays.asList(1, 1,1,1));
                MemberExcelData excelData = new MemberExcelData();
				excelData.setInputParam(param);
				excelData.setTitleNames(titles);
				excelData.setTitleNeeds(needs);
				excelData.setEventsNameMap(map);

				JSONObject obj = excelData.checkTitle(file.getInputStream(), file.getOriginalFilename());
				if (obj == null) {
					obj = excelData.checkData(param, dataCache);
					if (obj == null) {
						JSONObject recordListMap = new JSONObject();
						recordListMap.put("recordList", excelData.getImportResult(param));
						sportMeetService.createRecord(recordListMap);
						return JSONUtil.getResponse(1, "共计导入" + excelData.getImportCount() + "条记录");
					}
				}
				return obj;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return JSONUtil.getResponse(1, "文件格式正确");
		} else {
			return JSONUtil.getResponse(-1, "文件不是Excel格式");
		}
	}

	@RequestMapping(value = "/importEvents")
	@ResponseBody
	public JSONObject importEvents(@RequestParam("importFile") MultipartFile file,@RequestParam("param") String paramStr) {
		JSONObject param =  JSONObject.parseObject(paramStr);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(-1, "学校id为空，处理失败！");
		}
		String fileName = file.getOriginalFilename();
		String suffix = FilenameUtils.getExtension(fileName).toLowerCase();
		if (suffix.equalsIgnoreCase("xls") || suffix.equalsIgnoreCase("xlsx")) {
			try {
				List<JSONObject> list = sportMeetService.getEventsList(param);
				Map<String, String> map = DataUtil.getKeyValueMap(list, "eventsName", "eventsId");
				List<String> titles = new ArrayList<>(Arrays.asList("项目名称", "类别"));
				List<Integer> needs = new ArrayList<>(Arrays.asList(1, 1));
				EventsExcelData excelData = new EventsExcelData();
				excelData.setInputParam(param);
				excelData.setTitleNames(titles);
				excelData.setTitleNeeds(needs);
				excelData.setEventsNameMap(map);
				
				JSONObject obj = excelData.checkTitle(file.getInputStream(), file.getOriginalFilename());
				if (obj == null) {
					obj = excelData.checkData(param, dataCache);
					if (obj == null) {
						List<JSONObject> listbatch = excelData.getImportResult(excelData.getInputParam());
						sportMeetService.createEventsBatch(param,listbatch);
						return JSONUtil.getResponse(1, "共计导入" + excelData.getImportCount() + "条记录");
					}
				}
				return obj;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return JSONUtil.getResponse(1, "文件格式正确");
		} else {
			return JSONUtil.getResponse(-1, "文件不是Excel格式");
		}
	}

	@PostMapping("/importMembers")
	@ResponseBody
	public JSONObject importMembers(@RequestParam("importFile") MultipartFile file,@RequestParam("param") String paramStr){
		JSONObject param =  JSONObject.parseObject(paramStr);
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(-1, "学校id为空，处理失败！");
		}
		if (StringUtils.isEmpty(param.get("sportsId"))) {
			return JSONUtil.getResponse(-1, "赛事id为空，处理失败！");
		}
		String fileName = file.getOriginalFilename();
		String suffix = FilenameUtils.getExtension(fileName).toLowerCase();
		if (suffix.equalsIgnoreCase("xls") || suffix.equalsIgnoreCase("xlsx")) {
			try {
				List<JSONObject> list = sportMeetService.getScheduleList(param);
				List<JSONObject> memberList = sportMeetService.getMemberList(param);
				Map<String, String> map = DataUtil.getKeyValueMap(list, "eventsName", "eventsId");
				List<String> titles = new ArrayList<>(Arrays.asList("项目名称","班级","姓名"));
				List<Integer> needs = new ArrayList<>(Arrays.asList(1, 1,1));
				MemberExcelData excelData = new MemberExcelData();
				excelData.setInputParam(param);
				excelData.setTitleNames(titles);
				excelData.setTitleNeeds(needs);
				excelData.setEventsNameMap(map);

				Map<String,String> memberMap = new HashMap<>();
				for (JSONObject obj : memberList){
					String studentId = obj.getString("studentId");
					String eventId = obj.getString("eventsId");
					memberMap.put(eventId + ":" + studentId ,"");
				}
				excelData.setMemberMap(memberMap);

				JSONObject obj = excelData.checkTitle(file.getInputStream(), file.getOriginalFilename());
				if (obj == null) {
					obj = excelData.checkData(param, dataCache);
					if (obj == null) {
						List<JSONObject> listbatch = excelData.getImportResult(excelData.getInputParam());
						sportMeetService.createMemberBatch(param, listbatch);
						return JSONUtil.getResponse(1, "共计导入" + excelData.getImportCount() + "条记录");
					}
				}
				return obj;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return JSONUtil.getResponse(1, "文件格式正确");
		}
		return null;
	}

	private JSONObject checkSports(JSONObject param) {
		if (StringUtils.isEmpty(param.get(Constants.FIELD_ACCOUNTID))) {
			return JSONUtil.getResponse(-1, "当前用户账号为空，处理失败！");
		}
		if (StringUtils.isEmpty(param.get(Constants.FIELD_SCHOOLID))) {
			return JSONUtil.getResponse(-1, "学校id为空，处理失败！");
		}
		if (StringUtils.isEmpty(param.get("sportsId"))) {
			return JSONUtil.getResponse(-1, "赛事id为空，处理失败！");
		}
		return null;
	}

	private JSONObject checkEvents(JSONObject param) {
		if (StringUtils.isEmpty(param.get("eventsId"))) {
			return JSONUtil.getResponse(-1, "项目Id为空，处理失败！");
		}
		return checkSports(param);
	}
	
}
