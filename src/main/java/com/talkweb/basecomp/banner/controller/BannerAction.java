package com.talkweb.basecomp.banner.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.banner.entity.BannerInfo;
import com.talkweb.basecomp.banner.entity.BannerRelation;
import com.talkweb.basecomp.banner.entity.FrontPageBannerVo;
import com.talkweb.basecomp.banner.service.BannerService;

@Controller
@RequestMapping("/banner")
public class BannerAction {

	@Autowired
	private BannerService bannerService;

	@RequestMapping(value = "/publishBannerPage", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public HashMap<String, Object> publishBannerPage(HttpServletRequest req, @RequestBody JSONObject param) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		try {
			String str = param.getString("param");
			String schoolId = param.getString("schoolId");
			String userId = param.getString("userId");
			String type = param.getString("type");
			if (str == null || schoolId == null || userId == null || type == null) {
				resultMap.put("status", 2);
				resultMap.put("msg", "参数不齐全");
				return resultMap;
			}
			List<BannerRelation> rList = new ArrayList<>();
			List<FrontPageBannerVo> list = JSONObject.parseArray(str, FrontPageBannerVo.class);
			for (FrontPageBannerVo vo : list) {
				Map form = vo.getForm();
				String imgUrl = (String) form.get("imgUrl");
				String pathUrl = (String) form.get("pathUrl");
				Integer id = vo.getId();
				List<Map> gradeList = (List<Map>) form.get("gradeList");
				List<Map> roleList = (List<Map>) form.get("roleList");
				BannerInfo bannerInfo = new BannerInfo();
				bannerInfo.setImgSource(imgUrl);
				bannerInfo.setPublishTime(new Date());
				bannerInfo.setPublishUser(userId);
				bannerInfo.setSchoolId(schoolId);
				bannerInfo.setStatus(0);
				bannerInfo.setType(type);
				bannerInfo.setUpdateTime(new Date());
				bannerInfo.setUpdateUser(userId);
				bannerInfo.setUrlPath(pathUrl);
				if (id != null) {
					bannerInfo.setId(id);
					bannerService.updateBanner(bannerInfo);
				} else {
					bannerService.insertBannerInfo(bannerInfo);
				}
				for (Map gradeMap : gradeList) {
					Boolean checked = (Boolean) gradeMap.get("checked");
					if (checked == null || !checked) {
						continue;
					}
					String gradeId = String.valueOf(gradeMap.get("value"));
					for (Map roleMap : roleList) {
						Boolean checked2 = (Boolean) roleMap.get("checked");
						if (checked2 == null || !checked2) {
							continue;
						}
						String roleId = String.valueOf(roleMap.get("value"));
						BannerRelation bannerRelation = new BannerRelation();
						bannerRelation.setBannerId(bannerInfo.getId());
						bannerRelation.setGradeId(gradeId);
						bannerRelation.setRoleId(roleId);
						rList.add(bannerRelation);
					}
				}
				JSONObject delParam = new JSONObject();
				delParam.put("bannerId", bannerInfo.getId());
				bannerService.deleteBannerRelation(delParam);
			}
			if (!rList.isEmpty()) {
				bannerService.insertRelationBatch(rList);
			}
			resultMap.put("status", 1);
			resultMap.put("msg", "请求数据成功");
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("status", 2);
			resultMap.put("msg", "应用中心接口异常，请求失败。");
		}
		return resultMap;
	}

	@RequestMapping(value = "/getBannerPage", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public HashMap<String, Object> getBannerPage(HttpServletRequest req, @RequestBody JSONObject param) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		try {
			String schoolId = param.getString("schoolId");
			String gradeId = param.getString("gradeId");
			String userId = param.getString("userId");
			String type = param.getString("plante");
			if (type == null) {
				resultMap.put("status", 2);
				resultMap.put("msg", "参数不齐全");
				return resultMap;
			}

			if (type.equals("1")) {
				if (schoolId == null || gradeId == null || userId == null) {
					resultMap.put("status", 2);
					resultMap.put("msg", "参数不齐全");
					return resultMap;
				}
				List<JSONObject> data = bannerService.getBannerInfo(param);
				resultMap.put("result", data);
			} else if (type.equals("2")) {
				if (schoolId == null) {
					resultMap.put("status", 2);
					resultMap.put("msg", "参数不齐全");
					return resultMap;
				}
				List<JSONObject> list = bannerService.getBannerByPublish(param);
				for (JSONObject obj : list) {
					JSONObject rParam = new JSONObject();
					rParam.put("bannerId", obj.getString("id"));
					List<JSONObject> rList = bannerService.getBannerRelation(rParam);
					List<String> gradeList = new ArrayList<>();
					List<String> roleList = new ArrayList<>();
					for (JSONObject rObj : rList) {
						String role = rObj.getString("ROLE_ID");
						String grade = rObj.getString("GRADE_ID");
						if (!gradeList.contains(grade)) {
							gradeList.add(grade);
						}
						if (!roleList.contains(role)) {
							roleList.add(role);
						}
					}
					obj.put("roleList", roleList);
					obj.put("gradeList", gradeList);
				}
				resultMap.put("result", list);
			}
			resultMap.put("status", 1);
			resultMap.put("msg", "请求数据成功");
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("status", 2);
			resultMap.put("msg", "应用中心接口异常，请求失败。");
		}
		return resultMap;
	}

	@RequestMapping(value = "/removeBanner", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public HashMap<String, Object> removeBanner(HttpServletRequest req, @RequestBody JSONObject param) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		try {
			String bannerId = param.getString("bannerId");
			if (bannerId == null) {
				resultMap.put("status", 2);
				resultMap.put("msg", "参数不齐全");
				return resultMap;
			}
			bannerService.deleteBanner(param);
			resultMap.put("status", 1);
			resultMap.put("msg", "请求数据成功");
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("status", 2);
			resultMap.put("msg", "应用中心接口异常，请求失败。");
		}
		return resultMap;
	}

	@RequestMapping(value = "/getSchoolList", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public HashMap<String, Object> getSchoolList(HttpServletRequest req, @RequestBody JSONObject param) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		try {
			String areaCode = param.getString("areaCode");
			Integer termInfoId = Integer.parseInt(param.getString("termInfoId"));
			if (areaCode == null) {
				resultMap.put("status", 2);
				resultMap.put("msg", "参数不齐全");
				return resultMap;
			}
			JSONObject inParam = new JSONObject();
			if (!areaCode.equals("0")) {
				inParam.put("areaCode", areaCode);
			}
			inParam.put("termInfoId", termInfoId);
			List<JSONObject> result = bannerService.getSchoolList(inParam);
			resultMap.put("result", result);
			resultMap.put("status", 1);
			resultMap.put("msg", "请求数据成功");
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("status", 2);
			resultMap.put("msg", "应用中心接口异常，请求失败。");
		}
		return resultMap;
	}

	@RequestMapping("/getGradeList")
	@ResponseBody
	public HashMap<String, Object> getGradeList(HttpServletRequest req, @RequestBody JSONObject param) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		try {
			String termYear = param.getString("termInfoId").substring(0, 4);
			param.put("termYear", Integer.parseInt(termYear));
			List<JSONObject> returnList = bannerService.queryGradeList(param);
			List<JSONObject> tmpReturn = new ArrayList<>();
			List<String> tmpList = new ArrayList<>();
			for (JSONObject object : returnList) {
				String currLvl = object.getString("currentLevel");
				if (tmpList.contains(currLvl)) {
					continue;
				}
				tmpList.add(currLvl);
				object.put("name", getGradeName(currLvl));
				tmpReturn.add(object);
			}
			resultMap.put("status", 1);
			resultMap.put("msg", "请求数据成功");
			resultMap.put("result", tmpReturn);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("status", 2);
			resultMap.put("msg", "应用中心接口异常，请求失败。");
		}
		return resultMap;
	}

	private String getGradeName(String lvl) {
		if ("6".equals(lvl)) {
			return "小小班";
		}
		if ("7".equals(lvl)) {
			return "小班";
		}
		if ("8".equals(lvl)) {
			return "中班";
		}
		if ("9".equals(lvl)) {
			return "大班";
		}
		if ("10".equals(lvl)) {
			return "一年级";
		}
		if ("11".equals(lvl)) {
			return "二年级";
		}
		if ("12".equals(lvl)) {
			return "三年级";
		}
		if ("13".equals(lvl)) {
			return "四年级";
		}
		if ("14".equals(lvl)) {
			return "五年级";
		}
		if ("15".equals(lvl)) {
			return "六年级";
		}
		if ("16".equals(lvl)) {
			return "初一";
		}
		if ("17".equals(lvl)) {
			return "初二";
		}
		if ("18".equals(lvl)) {
			return "初三";
		}
		if ("19".equals(lvl)) {
			return "高一";
		}
		if ("20".equals(lvl)) {
			return "高二";
		}
		if ("21".equals(lvl)) {
			return "高三";
		}
		if ("22".equals(lvl)) {
			return "高四";
		}
		return null;
	}

}
