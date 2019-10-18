package com.talkweb.basecomp.common.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@SuppressWarnings("unchecked")
public class SchoolData implements Serializable {
	private static final long serialVersionUID = 1L;
	protected List<JSONObject> orgList;
	protected Map<String, JSONObject> parentMap;
	protected Map<String, JSONObject> studentMap;
	protected Map<String, JSONObject> teacherMap;
	protected Map<String, JSONObject> gradeMap;
	protected Map<String, JSONObject> classMap;
	protected Map<String, JSONObject> lessonMap;
	protected Map<String, JSONObject> orgMap;
	protected Long schoolId;
	protected String termInfo;

	private Map<String, List<String>> studentNameMap;
	private Map<String, List<String>> teacherNameMap;
	private Map<String, List<String>> studentParentMap;
	private Map<String, List<String>> usedGradeMap;
	private Map<String, String> userNameMap;
	private DataCache dataCache;

	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		obj.put("schoolId", schoolId);
		obj.put("termInfo", termInfo);
		obj.put("orgList", orgList);
		obj.put("studentMap", studentMap);
		obj.put("parentMap", parentMap);
		obj.put("teacherMap", teacherMap);
		obj.put("gradeMap", gradeMap);
		obj.put("classMap", classMap);
		obj.put("lessonMap", lessonMap);
		return obj;
	}
	
	public void fromJSONObject(JSONObject obj) {
		this.schoolId = obj.getLong("schoolId");
		this.termInfo = obj.getString("termInfo");
		this.orgList = (List<JSONObject>) obj.get("orgList");
		this.studentMap = (Map<String, JSONObject>) obj.get("studentMap");
		this.parentMap = (Map<String, JSONObject>) obj.get("parentMap");
		this.teacherMap = (Map<String, JSONObject>) obj.get("teacherMap");
		this.gradeMap = (Map<String, JSONObject>) obj.get("gradeMap");
		this.classMap = (Map<String, JSONObject>) obj.get("classMap");
		this.lessonMap = (Map<String, JSONObject>) obj.get("lessonMap");
	}
	
	public void setDataCache(DataCache dataCache) {
		this.dataCache = dataCache;
	}
	public String cacheKey() {
		return schoolId + "." + termInfo + ".schooldata";
	}

	public List<JSONObject> getOrgList() {
		if (this.orgList == null && this.dataCache != null) {
			this.dataCache.initOrgList(this);
		}
		return orgList == null ? new ArrayList<>() : this.orgList;
	}

	public Map<String, JSONObject> getOrgMap() {
		if (this.orgMap == null && this.getOrgList() != null && this.orgList != null) {
			this.orgMap = DataUtil.getIdMap(orgList);
		}
		return orgMap == null ? new HashMap<>() : this.orgMap;
	}

	public Map<String, JSONObject> getGradeMap() {
		if (this.gradeMap == null && this.dataCache != null) {
			this.dataCache.initGradeMap(this);
		}
		return gradeMap == null ? new HashMap<>() : this.gradeMap;
	}

	public Map<String, JSONObject> getTeacherMap() {
		if (this.teacherMap == null && this.dataCache != null) {
			this.dataCache.initTeacherMap(this);
		}
		return teacherMap == null ? new HashMap<>() : this.teacherMap;
	}

	public Map<String, JSONObject> getClassMap() {
		if (this.classMap == null && this.dataCache != null) {
			this.dataCache.initClassMap(this);
		}
		return classMap == null ? new HashMap<>() : this.classMap;
	}

	public Map<String, JSONObject> getLessonMap() {
		if (this.lessonMap == null && this.dataCache != null) {
			this.dataCache.initLessonMap(this);
		}
		return lessonMap == null ? new HashMap<>() : this.lessonMap;
	}

	public Map<String, JSONObject> getStudentMap() {
		if (this.studentMap == null && this.dataCache != null) {
			this.dataCache.initStudentMap(this);
		}
		return studentMap == null ? new HashMap<>() : this.studentMap;
	}

	public Map<String, JSONObject> getParentMap() {
		if (this.parentMap == null && this.dataCache != null) {
			this.dataCache.initParentMap(this);
		}
		return parentMap == null ? new HashMap<>() : this.parentMap;
	}

	//根据机构类型获取子机构列表
	public List<String> getOrgList(int type) {
		List<String> ret = new ArrayList<>();
		for (JSONObject obj: this.getOrgList()) {
			int t = obj.getIntValue("orgType");
			if (t == type) {
				ret.add(obj.getString("id"));
			}
		}
		return ret;
	}

	//年级id和使用年级映射表
	public Map<String, String> getGradeYearMap() {
		return DataUtil.getKeyValueMap(this.getGradeMap().values(), "id", "usedGrade");
	}

	//使用年级和年级名称映射表
	public Map<String, String> getGradeNameMap() {
		return DataUtil.getKeyValueMap(this.getGradeMap().values(), "usedGrade", "name");
	}

	//年级id和年级level映射表
	public Map<String, Integer> getGradeLevelMap() {
		Map<String, Integer> map = new HashMap<>();
		for (JSONObject grade: this.getGradeMap().values()) {
			map.put(grade.getString("id"), grade.getInteger("currentLevel"));
		}
		return map;
	}

	public Collection<JSONObject> getGradeList() {
		List<JSONObject> ret = new ArrayList<>(this.getGradeMap().values());
		Collections.sort(ret, SortComparator.gradeSort);
		return ret;
	}

	public List<String> getSortUsedGradeList() {
		return DataUtil.getFieldList(getGradeList(), "usedGrade");
	}

	//班级名字和id映射表
	public Map<String, String> getClassNameMap() {
		return DataUtil.getKeyValueMap(this.getClassMap().values(), "name", "id");
	}

	public Collection<JSONObject> getLessonList() {
		List<JSONObject> ret = new ArrayList<>(this.getLessonMap().values());
		Collections.sort(ret, SortComparator.lessonSort);
		return ret;
	}

	//科目名称和id映射表
	public Map<String, String> getLessonNameMap() {
		return DataUtil.getKeyValueMap(this.getLessonMap().values(), "name", "id");
	}

	//学生学号和id映射表
	public Map<String, String> getStudentNumberMap() {
		return DataUtil.getKeyValueMap(this.getStudentMap().values(), "schoolNumber", "id");
	}

	//根据教师名称获取id
	public List<String> getTeacherIdByName(String tname) {
		List<String> id = null;
		if (this.initTeacherNameMap() != null)
			id = this.teacherNameMap.get(tname);
		return id;
	}

	//获取教师名
	public String getTeacherName(String teacherId) {
		String name = "";
		JSONObject oo = getTeacherObject(teacherId);
		if (oo != null)
			name = oo.getString("name");
		return name == null ? "" : name;
	}

	//获取教师userId
	public String getTeacherUserId(String teacherId) {
		String name = "";
		JSONObject oo = getTeacherObject(teacherId);
		if (oo != null)
			name = oo.getString("userId");
		return name == null ? "" : name;
	}

	//获取教师角色展示名，XX老师
	public String getTeacherDisplayName(String teacherId) {
		String name = "";
		JSONObject oo = getTeacherObject(teacherId);
		if (oo != null) {
			name = oo.getString("name");
			if (!StringUtils.isEmpty(name)) {
				name += ROLENAME_TEACHER;
			}
		}
		return name == null ? "" : name;
	}

	//获取教师基础平台extUserId
	public String getTeacherExtId(String teacherId) {
		String name = "";
		JSONObject oo = getTeacherObject(teacherId);
		if (oo != null)
			name = oo.getString("extId");
		return name == null ? "" : name;
	}

	//获取教师电话号码
	public String getTeacherPhone(String teacherId) {
		String str = "";
		JSONObject oo = this.getTeacherObject(teacherId);
		if (oo != null)
			str = oo.getString("mobilePhone");
		return str == null ? "" : str;
	}

	//获取年级名称
	public String getGradeName(String usedGrade) {
		String name = "";
		if (usedGrade != null && getGradeMap() != null && this.gradeMap != null) {
			name = this.getGradeNameMap().get(usedGrade);
		}
		return name == null ? "" : name;
	}

	//获取班级名称
	public String getClassName(String classId) {
		String name = "";
		JSONObject oo = this.getClassObject(classId);
		if (oo != null)
			name = oo.getString("name");
		return name == null ? "" : name;
	}

	//获取科目名称
	public String getLessonName(String subjectId) {
		String name = "";
		if (subjectId != null && getLessonMap() != null && this.lessonMap != null) {
			JSONObject oo = lessonMap.get(subjectId);
			if (oo != null)
				name = oo.getString("name");
		}
		return name == null ? "" : name;
	}

	//根据学生名称获取id
	public List<String> getStudentIdByName(String studentName) {
		List<String> id = null;
		if (this.initStudentNameMap() != null)
			id = this.studentNameMap.get(studentName);
		return id;
	}

	//根据学生名称获取指定班级的学生id
	public List<String> getStudentIdByName(String studentName, String classId) {
		List<String> ret = new ArrayList<>();
		if (this.initStudentNameMap() != null) {
			List<String> idList = this.studentNameMap.get(studentName);
			if (idList != null) {
				if (StringUtils.isEmpty(classId)) 
					return idList;
				List<String> stuList = this.getStudentAccountIds(classId);
				for (String sid: idList) {
					if (stuList.contains(sid)) {
						ret.add(sid);
					}
				}
			}
		}
		return ret;
	}

	//获取学生名称
	public String getStudentName(String studentId) {
		String name = "";
		JSONObject oo = getStudentObject(studentId);
		if (oo != null)
			name = oo.getString("name");
		return name == null ? "" : name;
	}

	//获取学生所在的使用年级
	public String getStudentUsedGrade(String studentId) {
		String str = this.getStudentClassId(studentId);
		return this.getUsedGrade(str);
	}

	//获取学生所在的班级名称
	public String getStudentClassName(String studentId) {
		String str = this.getStudentClassId(studentId);
		return this.getClassName(str);
	}

	//获取学生所在的班级id
	public String getStudentClassId(String studentId) {
		String str = "";
		JSONObject oo = getStudentObject(studentId);
		if (oo != null)
			str = oo.getString("classId");
		return str == null ? "" : str;
	}

	//获取学生学号
	public String getStudentNumber(String studentId) {
		String str = "";
		JSONObject oo = getStudentObject(studentId);
		if (oo != null)
			str = oo.getString("schoolNumber");
		return str == null ? "" : str;
	}

	//获取学生基础平台对应的extUserId
	public String getStudentExtId(String studentId) {
		String str = "";
		JSONObject oo = getStudentObject(studentId);
		if (oo != null)
			str = oo.getString("extAccountId");
		return str == null ? "" : str;
	}

	//获取学生对应的userId
	public String getStudentUserId(String studentId) {
		String str = "";
		JSONObject oo = getStudentObject(studentId);
		if (oo != null)
			str = oo.getString("userId");
		return str == null ? "" : str;
	}

	//获取学生对应家长的电话号码
	public String getStudentPhone(String studentId) {
		if (studentId != null && this.initStudentParentMap() != null) {
			List<String> list = this.studentParentMap.get(studentId);
			if (list != null && list.size() > 0) {
				for (String parentId: list) {
					String str = this.getParentPhone(parentId);
					if (!StringUtils.isEmpty(str))
						return str;
				}
			}
		}
		return "";
	}

	//获取学生对应家长列表
	public List<String> getStudentParentList(String studentId) {
		if (studentId != null && this.initStudentParentMap() != null) {
			List<String> list = this.studentParentMap.get(studentId);
			if (list != null) {
				return list;
			}
		}
		return new ArrayList<>();
	}

	//获取家长名称
	public String getParentName(String parentId) {
		String name = "";
		JSONObject oo = this.getParentObject(parentId);
		if (oo != null)
			name = oo.getString("name");
		return name == null ? "" : name;
	}

	//获取家长extId
	public String getParentExtId(String parentId) {
		String name = "";
		JSONObject oo = this.getParentObject(parentId);
		if (oo != null)
			name = oo.getString("extId");
		return name == null ? "" : name;
	}

	//获取家长角色展示名，XX爸爸
	public String getParentDisplayName(String parentId) {
		String name = "";
		JSONObject oo = this.getParentObject(parentId);
		if (oo != null) {
			name = oo.getString("studentId");
			name = this.getStudentName(name);
			if (!StringUtils.isEmpty(name)) {
				name += getParentRoleName(oo.getInteger("prole"));
			}
		}
		return name == null ? "" : name;
	}

	//获取家长电话号码
	public String getParentPhone(String parentId) {
		String str = "";
		JSONObject oo = this.getParentObject(parentId);
		if (oo != null)
			str = oo.getString("mobilePhone");
		return str == null ? "" : str;
	}

	//获取家长对应的学生id
	public String getParentStudentId(String parentId) {
		String str = "";
		JSONObject oo = this.getParentObject(parentId);
		if (oo != null)
			str = oo.getString("studentId");
		return str == null ? "" : str;
	}

	//获取学生所在的班级名称
	public String getParentClassName(String parentId) {
		String studentId = this.getParentStudentId(parentId);
		return this.getStudentClassName(studentId);
	}

	//获取学生所在的班级id
	public String getParentClassId(String parentId) {
		String studentId = this.getParentStudentId(parentId);
		return this.getStudentClassId(studentId);
	}

	//获取机构名称
	public String getOrgName(String orgId) {
		String name = "";
		JSONObject org = this.getOrgObject(orgId);
		if (org != null)
			name = org.getString("orgName");
		return name == null ? "" : name;
	}

	//获取机构类型名称
	public String getOrgTypeName(int type) {
		String name = orgTypeNameMap.get(type);
		return name == null ? "" : name;
	}

	//根据userId获取用户名称
	public String getUserName(String userId) {
		return getUserName(userId, "");
	}

	//根据userId获取用户名称
	public String getUserName(String userId, String defaul) {
		String name = "";
		try {
			if (this.initUserNameMap() != null && this.userNameMap.containsKey(userId)){
				name = this.userNameMap.get(userId);
				if (!StringUtils.isEmpty(name)) {
					name += ROLENAME_TEACHER;
				}
			} else if (this.getParentMap().containsKey(userId)) {
				name = this.getParentDisplayName(userId);
			}
			//System.out.println("#####getUserName#####"+this.userNameMap);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return StringUtils.isEmpty(name) ? defaul : name;
	}

	//根据名称查询老师列表
	public List<JSONObject> queryTeacherList(String name) {
		List<JSONObject> list = new ArrayList<>();
		List<String> ids = this.queryTeacherIdList(name);
		for (String id: ids) {
			JSONObject t = this.teacherMap.get(id);
			if (t != null) {
				JSONObject obj = new JSONObject();
				obj.putAll(t);
				list.add(obj);
			}
		}
		return list;
	}

	//根据名称查询老师ID列表
	public List<String> queryTeacherIdList(String name) {
		return queryTeacherIdList(name, false);
	}

	//根据名称查询老师ID列表
	public List<String> queryTeacherIdList(String name, boolean queryPhone) {
		return queryTeacherIdList(this.getTeacherMap().keySet(), name, queryPhone);
	}

	//根据名称查询老师ID列表
	public List<String> queryTeacherIdList(Collection<String> list, String name, boolean queryPhone) {
		List<String> idlist = new ArrayList<>();
		if (StringUtils.isEmpty(name)) {
			idlist.addAll(list);
		} else if (queryPhone && StringUtils.isNumber(name)) {
			for (String teacherId: list) {
				String nn = this.getTeacherPhone(teacherId);
				if (!StringUtils.isEmpty(nn) && nn.contains(name)) {
					idlist.add(teacherId);
				}
			}
		} else {
			String[] strs = name.split(" ");
			for (String teacherId: list) {
				String nn = this.getTeacherName(teacherId);
				if (containString(nn, strs)) {
					idlist.add(teacherId);
				}
			}
		}
		return idlist;
	}

	//根据名称查询学生Id列表
	public List<String> queryStudentIdList(String name) {
		return queryStudentIdList(name, false);
	}

	//根据名称查询学生Id列表
	public List<String> queryStudentIdList(String name, boolean queryPhone) {
		Collection<String> studentIdList = this.getStudentMap().keySet();
		return queryStudentIdList(studentIdList, name, queryPhone);
	}
	
	//根据名称查询学生Id列表
	public List<String> queryStudentIdList(Collection<String> classIdList, String name) {
		List<String> studentIdList = this.getStudentAccountIds(classIdList);
		return queryStudentIdList(studentIdList, name, false);
	}

	//根据名称查询学生Id列表
	public List<String> queryStudentIdList(Collection<String> studentList, String name, boolean queryPhone) {
		List<String> idlist = new ArrayList<>();
		if (StringUtils.isEmpty(name)) {
			idlist.addAll(studentList);
		} else if (studentList != null) {
			if (queryPhone && StringUtils.isNumber(name)) {
				for (String str: studentList) {
					String nn = this.getStudentPhone(str);
					if (!StringUtils.isEmpty(nn) && nn.contains(name)) {
						idlist.add(str);
					}
				}
			} else {
				String[] strs = name.split(" ");
				for (String str: studentList) {
					String nn = this.getStudentName(str);
					if (nn != null && containString(nn, strs)) {
						idlist.add(str);
					}
				}
			}
		}
		return idlist;
	}

	public <E> List<JSONObject> getStudentList(Collection<E> studentIds) {
		List<JSONObject> list = new ArrayList<>();
		if (studentIds != null && this.getStudentMap() != null && this.studentMap != null) {
			for (E id: studentIds) {
				JSONObject obj = this.studentMap.get(id.toString());
				if (obj != null) {
					list.add(obj);
				}
			}
		}
		return list;
	}

	public <E> List<JSONObject> getTeacherList(Collection<E> teacherIds) {
		List<JSONObject> list = new ArrayList<>();
		if (teacherIds != null && this.getTeacherMap() != null && this.teacherMap != null) {
			for (E id: teacherIds) {
				JSONObject obj = this.teacherMap.get(id.toString());
				if (obj != null) {
					list.add(obj);
				}
			}
		}
		return list;
	}

	//获取班级列表
	public <E> List<JSONObject> getClassList(Collection<E> classIds) {
		List<JSONObject> classList = new ArrayList<>();
		if (classIds != null && getClassMap() != null && this.classMap != null) {
			for (E classId: classIds) {
				JSONObject cls = this.classMap.get(classId.toString());
				if (cls != null)
					classList.add(cls);
			}
		}
		return classList;
	}

	//获取班级列表
	public <E> List<JSONObject> getSortClassList(Collection<E> classIds) {
		List<JSONObject> classList = new ArrayList<>();
		if (classIds != null) {
			for (E classId: classIds) {
				JSONObject obj = new JSONObject();
				obj.put("classId", classId);
				obj.put("className", this.getClassName(classId.toString()));
				classList.add(obj);
			}
			Collections.sort(classList, SortComparator.classSort);
		}

		return classList;
	}

	public <E> List<JSONObject> getLessonList(Collection<E> lessonIds) {
		List<JSONObject> list = new ArrayList<>();
		if (lessonIds != null && getLessonMap() != null && this.lessonMap != null) {
			for (E lessonId: lessonIds) {
				JSONObject cls = this.lessonMap.get(lessonId.toString());
				if (cls != null)
					list.add(cls);
			}
		}
		return list;
	}

	//获取班主任的班级id列表
	public List<String> getDeanClassIdList(JSONObject param) {
		String accountId = param.getString("accountId");
		String role = param.getString("role");
		List<String> classIds = new ArrayList<>();
		if (!isParentOrStudent(role)) {
			JSONObject obj = this.getTeacherObject(accountId);
			if (obj != null)
				classIds = DataUtil.getDeanClassIdList(obj);
		}
		return classIds;
	}

	//获取用户关联的班级id列表
	public List<String> getUserClassIdList(JSONObject param) {
		String accountId = param.getString("accountId");
		String role = param.getString("role");
		List<String> classIds = new ArrayList<>();
		if (isParentOrStudent(role)) {
			String userId = param.getString("userId");
			String studentId = accountId;
			if (this.getParentMap().containsKey(userId)) {
				studentId = this.getParentStudentId(userId);
			}
			classIds = Arrays.asList(this.getStudentClassId(studentId));
		} else {
			JSONObject obj = this.getTeacherObject(accountId);
			if (obj != null)
				classIds = DataUtil.getUserClassIdList(obj);
		}
		return classIds;
	}

	//获取用户关联的机构id列表
	public List<String> getUserOrgIdList(JSONObject param) {
		List<String> orgIds = new ArrayList<>();
		String accountId = param.getString("accountId");
		String role = param.getString("role");
		if (!isParentOrStudent(role)) {
			JSONObject obj = this.getTeacherObject(accountId);
			if (obj != null)
				orgIds = DataUtil.getUserOrgIdList(obj);
		}
		return orgIds;
	}

	//获取教师科目列表
	public String getTeacherLessonName(String teacherId) {
		JSONObject oo = getTeacherObject(teacherId);
		if (oo != null) {
			JSONArray courlist = oo.getJSONArray("courseIds");
			if (courlist != null) {
				List<JSONObject> sublist = courlist.toJavaList(JSONObject.class);
				Collections.sort(sublist, lessonSort);
				List<String> list = DataUtil.getFieldList(sublist, "lessonId");
				List<String> names = new ArrayList<>();
				for (String subId: list) {
					String name = this.getLessonName(subId);
					if (!StringUtils.isEmpty(name))
						names.add(name);
				}
				return DataUtil.listToString(names);
			}
		}
		return "";
	}

	//根据年级level获取年级组信息
	public List<JSONObject> getGradeOrgList(Integer gradeLevel) {
		List<Integer> list = new ArrayList<>();
		list.add(gradeLevel);
		return this.getGradeOrgList(list);
	}

	//根据年级level获取年级组信息
	public List<JSONObject> getGradeOrgList(Collection<Integer> gradeLevels) {
		List<JSONObject> ret = new ArrayList<>();
		if (gradeLevels.isEmpty()) return ret;
		
		Collection<JSONObject> list = this.getOrgList();
		for (JSONObject obj: list) {
			if (obj.getIntValue("orgType") != 2) continue;//年级组
			
			JSONArray arr = obj.getJSONArray("scopeTypes");
			if (arr == null || arr.isEmpty()) continue;
			for (Integer level: gradeLevels) {
				if (arr.contains(level)) {
					ret.add(obj);
					break;
				}
			}
		}
		return ret;
	}

	//根据使用年级获取班级id列表
	public <E> List<String> getGradeClassIdList(String usedGrade) {
		return getGradeClassIdList(Arrays.asList(usedGrade));
	}

	//根据使用年级获取班级id列表
	public <E> List<String> getGradeClassIdList(Collection<E> usedGrades) {
		List<String> classIds = new ArrayList<>();
		if (classIds != null && this.initUsedGradeMap() != null) {
			for (E usedGrade: usedGrades) {
				List<String> gradeIds = this.usedGradeMap.get(usedGrade.toString());
				for (String gradeId: gradeIds) {
					JSONObject grade = this.gradeMap.get(gradeId);
					if (grade != null) {
						JSONArray idList = grade.getJSONArray("classIds");
						if (idList != null) {
							classIds.addAll(idList.toJavaList(String.class));
						}
					}
				}
			}
		}
		return classIds;
	}

	//根据使用年级获取班级列表
	public <E> List<JSONObject> getGradeClassList(Collection<E> usedGrades) {
		return getClassList(this.getGradeClassIdList(usedGrades));
	}

	public <E> List<String> getGradeDeanIdList(String usedGrade) {
		List<String> usedGrades = new ArrayList<>();
		usedGrades.add(usedGrade);
		return this.getGradeDeanIdList(usedGrades);
	}

	//根据使用年级获取班主任列表
	public <E> List<String> getGradeDeanIdList(Collection<E> usedGrades) {
		List<String> classIds = this.getGradeClassIdList(usedGrades);
		return getDeanIdList(classIds);
	}

	//根据使用年级获取学生数
	public int getGradeStudentSize(String usedGrade) {
		int size = 0;
		List<String> classIds = this.getGradeClassIdList(usedGrade);
		for (String classId: classIds) {
			int cc = this.getStudentIdsSize(classId);
			size += cc;
		}
		return size;
	}

	//根据使用年级获取学生列表
	public List<String> getGradeStudentAccountIds(String usedGrade) {
		return getGradeStudentAccountIds(Arrays.asList(usedGrade));
	}
	
	public <E> List<String> getGradeStudentAccountIds(Collection<E> usedGrades) {
		List<String> classIds = this.getGradeClassIdList(usedGrades);
		return getStudentAccountIds(classIds);
	}

	//学生Id、班级Id映射表
	public <E> Map<String, String> getStudentClassMap(Collection<E> classIds) {
		List<JSONObject> classrooms = this.getClassList(classIds);
		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < classrooms.size(); i++) {
			JSONObject classroom = classrooms.get(i);
			List<String> arr = this.getStudentAccountIds(classroom);
			if (arr != null) {
				for (String ss: arr) {
					map.put(ss, classroom.getString("id"));
				}
			}
		}
		return map;
	}

	//获取班主任列表
	public List<String> getDeanIdList() {
		return getDeanIdList(this.getClassMap().keySet());
	}
	
	public List<String> getDeanIdList(Collection<String> classIds) {
		Set<String> idlist = new HashSet<>();
		for (String classId: classIds) {
			String deanAccountId = this.getDeanAccountId(classId);
			if (!StringUtils.isEmpty(deanAccountId))
				idlist.add(deanAccountId);
		}
		return new ArrayList<>(idlist);
	}

	//获取指定班级关联老师Id列表
	public <E> List<String> getClassTeacherIdList(String classId) {
		Set<String> ret = new HashSet<>();
		String deanId = this.getDeanAccountId(classId);
		if (!StringUtils.isEmpty(deanId)) { // 添加班主任
			ret.add(deanId);
		}
		List<String> tmp = this.getLessonTeacherIdList(classId, null);
		ret.addAll(tmp);
		return new ArrayList<>(ret);
	}

	//获取指定班级关联老师Id列表
	public <E> List<String> getClassTeacherIdList(Map<String, Set<String>> classSubjectMap) {
		Set<String> ret = new HashSet<>();
		for (String classId: classSubjectMap.keySet()) {
			String deanId = this.getDeanAccountId(classId);
			if (!StringUtils.isEmpty(deanId)) { // 添加班主任
				ret.add(deanId);
			}
			Set<String> set = classSubjectMap.get(classId);
			List<String> tmp = this.getLessonTeacherIdList(classId, set);
			ret.addAll(tmp);
		}
		return new ArrayList<>(ret);
	}

	//获取指定班级任课老师Id列表
	public <E> List<String> getLessonTeacherIdList(String classId, Collection<String> subjectIds) {
		List<JSONObject> list = this.getLessonTeacherListInternal(classId, subjectIds);
		return DataUtil.getFieldList(list, "accountId");
	}

	//获取指定年级任课老师列表
	public <E> List<JSONObject> getLessonTeacherList(String usedGrade, List<String> subjectIds) {
		List<String> classIds = this.getGradeClassIdList(Arrays.asList(usedGrade));
		return getLessonTeacherList(classIds, subjectIds);
	}

	//获取指定班级任课老师列表
	public <E> List<JSONObject> getLessonTeacherList(Collection<E> classIds, List<String> subjectIds) {
		List<JSONObject> ret = new ArrayList<>();
		if (classIds == null) {
			classIds = (Collection<E>) this.getClassMap().keySet();
		}
		for (E classId: classIds) {
			List<JSONObject> list = this.getLessonTeacherListInternal(classId.toString(), subjectIds);
			ret.addAll(list);
		}
		return ret;
	}

	//班级id+科目id、老师id映射表
	public <E> Map<String, String> getLessonTeacherMap(Collection<E> classIds) {
		Map<String, String> map = new HashMap<>();
		List<JSONObject> list = this.getLessonTeacherList(classIds, null);
		for (JSONObject obj : list) {
			String classId = obj.getString("classId");
			String subjectId = obj.getString("lessonId");
			String teacherId = obj.getString("accountId");
			map.put(classId + subjectId, teacherId);
		}
		return map;
	}

	//获取指定班级班主任id
	public String getDeanAccountId(String classId) {
		JSONObject cls = this.getClassObject(classId);
		if (cls == null) return "";
		String deanAccountId = cls.getString("deanAccountId");
		if (!StringUtils.isEmpty(deanAccountId) && !"0".equals(deanAccountId))
			return deanAccountId;
		return "";
	}

	//获取指定班级班主任名称
	public String getDeanAccountName(String classId) {
		String deanAccountId = this.getDeanAccountId(classId);
		if (!StringUtils.isEmpty(deanAccountId))
			return this.getTeacherName(deanAccountId);
		return "";
	}

	//获取指定班级使用年级
	public String getUsedGrade(String classId) {
		JSONObject cls = this.getClassObject(classId);
		if (cls == null) return "";
		String gradeId = cls.getString("gradeId");
		if (!StringUtils.isEmpty(gradeId))
			return this.getGradeYearMap().get(gradeId);
		return "";
	}

	//获取指定班级年级level
	public Integer getGradeLevel(String classId) {
		JSONObject cls = this.getClassObject(classId);
		if (cls == null) return 0;
		String gradeId = cls.getString("gradeId");
		if (!StringUtils.isEmpty(gradeId))
			return this.getGradeLevelMap().get(gradeId);
		return 0;
	}

	//获取指定班级家长userId列表
	public List<String> getParentUserIds(String classId) {
		return getParentUserIds(Arrays.asList(classId));
	}

	//获取指定班级家长userId列表
	public <E> List<String> getParentUserIds(Collection<E> classIds) {
		Set<String> ret = new HashSet<>();
		for (E classId: classIds) {
			JSONObject cls = this.getClassObject(classId.toString());
			if (cls != null && this.initStudentParentMap() != null) {
				List<String> list = this.getStudentAccountIds(cls);
				for (String studentId: list) {
					List<String> plist = this.studentParentMap.get(studentId);
					if (plist != null)
						ret.addAll(plist);
				}
			}
		}
		
		return new ArrayList<>(ret);
	}

	//获取指定班级学生数
	public int getStudentIdsSize(String classId) {
		JSONObject cls = this.getClassObject(classId);
		if (cls != null)
			return this.getStudentIdsSize(cls);
		return 0;
	}

	//获取指定班级学生数
	public int getStudentIdsSize(JSONObject cls) {
		JSONArray idList = cls.getJSONArray("studentAccountIds");
		return idList == null ? 0 : idList.size();
	}

	//获取指定班级学生Id列表
	public List<String> getStudentAccountIds(String classId) {
		JSONObject cls = this.getClassObject(classId);
		if (cls != null)
			return this.getStudentAccountIds(cls);
		
		return new ArrayList<>();
	}

	//获取指定班级学生Id列表
	public <E> List<String> getStudentAccountIds(Collection<E> classIds) {
		List<String> stuAccounts = new ArrayList<>();
		if (classIds == null) {
			classIds = (Collection<E>) this.getClassMap().keySet();
		}
		for (E classId: classIds) {
			List<String> arr = this.getStudentAccountIds(classId.toString());
			if (arr != null) {
				stuAccounts.addAll(arr);
			}
		}
		return stuAccounts;
	}

	//获取指定班级学生Id列表
	public List<String> getStudentAccountIds(JSONObject cls) {
		JSONArray idList = cls.getJSONArray("studentAccountIds");
		List<String> ret = new ArrayList<>();
		if (idList != null) {
			ret = idList.toJavaList(String.class);
		}
		return ret;
	}

	//获取指定机构成员Id列表
	public List<String> getMemberAccountIds(int type) {
		Set<String> ret = new HashSet<>();
		List<String> orgList = this.getOrgList(type);
		for (String orgId: orgList) {
			List<String> tmp = this.getMemberAccountIds(orgId);
			ret.addAll(tmp);
		}
		return new ArrayList<>(ret);
	}

	//获取指定机构成员Id列表
	public List<String> getMemberAccountIds(String orgId) {
		JSONObject org = this.getOrgObject(orgId);
		return this.getMemberAccountIds(org);
	}

	//获取指定机构成员Id列表
	public List<String> getMemberAccountIds(JSONObject org) {
		List<String> ret = new ArrayList<>();
		if (org != null) {
			JSONArray idList = org.getJSONArray("memberAccountIds");
			if (idList != null) {
				ret = idList.toJavaList(String.class);
			}
		}
		return ret;
	}

	//获取指定机构组长Id列表
	public List<String> getHeaderAccountIds(int type) {
		Set<String> ret = new HashSet<>();
		List<String> orgList = this.getOrgList(type);
		for (String orgId: orgList) {
			List<String> tmp = this.getHeaderAccountIds(orgId);
			ret.addAll(tmp);
		}
		return new ArrayList<>(ret);
	}

	//获取指定机构组长Id列表
	public List<String> getHeaderAccountIds(String orgId) {
		JSONObject org = this.getOrgObject(orgId);
		return getHeaderAccountIds(org);
	}

	//获取指定机构组长Id列表
	public List<String> getHeaderAccountIds(JSONObject org) {
		List<String> ret = new ArrayList<>();
		if (org != null) {
			JSONArray idList = org.getJSONArray("headerAccountIds");
			if (idList != null) {
				ret = idList.toJavaList(String.class);
			}
		}
		return ret;
	}

    public static String getParentRoleName(Integer prole) {
    	if (prole == null) return ROLENAME_PARENT_PARENT;
    	switch(prole) {
    	case PROLE_FATHER:
    		return ROLENAME_PARENT_FATHER;
    	case PROLE_MOTHER:
    		return ROLENAME_PARENT_MOTHER;
    	case PROLE_GRANDFATHER:
    		return ROLENAME_PARENT_GRANDFATHER;
    	case PROLE_GRANDMOTHER:
    		return ROLENAME_PARENT_GRANDMOTHER;
    	case PROLE_GRANDPA:
    		return ROLENAME_PARENT_GRANDPA;
    	case PROLE_GRANDMA:
    		return ROLENAME_PARENT_GRANDMA;
    	}
		return ROLENAME_PARENT_PARENT;
	}

	private <E> List<JSONObject> getLessonTeacherListInternal(String classId, Collection<String> subjectIds) {
		JSONObject classroom = this.getClassObject(classId);
		List<JSONObject> ret = new ArrayList<>();
		JSONArray alList = classroom.getJSONArray("accountLessons");
		if (alList == null || alList.isEmpty()) return ret;
		for (int i = 0; i < alList.size(); i++) {
			JSONObject oo = alList.getJSONObject(i);
			String subjectId = oo.getString("lessonId");
			String teacherId = oo.getString("accountId");
			if (subjectId != null && teacherId != null
					&& (subjectIds == null || subjectIds.contains(subjectId))) {
				JSONObject obj = new JSONObject();
				obj.putAll(oo);
				obj.put("classId", classId);
				ret.add(obj);
			}
		}
		return ret;
	}

	private Map<String, List<String>> initStudentNameMap() {
		if (this.studentNameMap == null && this.getStudentMap() != null && this.studentMap != null) {
			this.studentNameMap = new HashMap<>();
			for (String key : studentMap.keySet()) {
				String name = this.getStudentName(key);
				List<String> ids = this.studentNameMap.get(name);
				if (ids == null) {
					ids = new ArrayList<>();
					this.studentNameMap.put(name, ids);
				}
				ids.add(key);
			}
		}
		return studentNameMap;
	}

	private Map<String, List<String>> initUsedGradeMap() {
		if (this.usedGradeMap == null && this.getGradeMap() != null && this.gradeMap != null) {
			this.usedGradeMap = new HashMap<>();
			for (JSONObject obj: this.gradeMap.values()) {
				String id = obj.getString("id");
				String used = obj.getString("usedGrade");
				List<String> ids = this.usedGradeMap.get(used);
				if (ids == null) {
					ids = new ArrayList<>();
					this.usedGradeMap.put(used, ids);
				}
				ids.add(id);
			}
		}
		return usedGradeMap == null ? new HashMap<>() : this.usedGradeMap;
	}

	private Map<String, String> initUserNameMap() {
		if (this.userNameMap == null && this.getTeacherMap() != null && this.teacherMap != null) {
			this.userNameMap = DataUtil.getKeyValueMap(this.getTeacherMap().values(), "userId", "name");
		}
		return userNameMap;
	}

	private Map<String, List<String>> initStudentParentMap() {
		if (this.studentParentMap == null && this.getParentMap() != null && this.parentMap != null) {
			this.studentParentMap = new HashMap<>();
			for (JSONObject obj: this.parentMap.values()) {
				String studentId = obj.getString("studentId");
				List<String> ids = this.studentParentMap.get(studentId);
				if (ids == null) {
					ids = new ArrayList<>();
					this.studentParentMap.put(studentId, ids);
				}
				ids.add(obj.getString("id"));
			}
		}
		return studentParentMap;
	}

	private Map<String, List<String>> initTeacherNameMap() {
		if (this.teacherNameMap == null && this.getTeacherMap() != null && this.teacherMap != null) {
			this.teacherNameMap = new HashMap<>();
			for (String key : teacherMap.keySet()) {
				String name = this.getTeacherName(key);
				List<String> ids = this.teacherNameMap.get(name);
				if (ids == null) {
					ids = new ArrayList<>();
					this.teacherNameMap.put(name, ids);
				}
				ids.add(key);
			}
		}
		return teacherNameMap;
	}

	private JSONObject getClassObject(String classId) {
		if (classId != null && this.getClassMap() != null && this.classMap != null) {
			return classMap.get(classId);
		}
		return null;
	}

	private JSONObject getStudentObject(String studentId) {
		if (studentId != null && this.getStudentMap() != null && this.studentMap != null) {
			return studentMap.get(studentId);
		}
		return null;
	}

	private JSONObject getParentObject(String parentId) {
		if (parentId != null && this.getParentMap() != null && this.parentMap != null) {
			return parentMap.get(parentId);
		}
		return null;
	}

	private JSONObject getTeacherObject(String teacherId) {
		if (teacherId != null && getTeacherMap() != null && this.teacherMap != null) {
			return teacherMap.get(teacherId);
		}
		return null;
	}

	private JSONObject getOrgObject(String orgId) {
		if (orgId != null && getOrgMap() != null && this.orgMap != null) {
			return orgMap.get(orgId);
		}
		return null;
	}
	
	private boolean containString(String nn, String[] keys) {
		if (StringUtils.isEmpty(nn))
			return false;
		
		for (String ss: keys) {
			if (ss.length() > 0 && !nn.contains(ss)) {
				return false;
			}
		}
		return true;
	}

	private static Map<Integer, String> orgTypeNameMap;
	
	static {
		orgTypeNameMap = new HashMap<>();
		orgTypeNameMap.put(1, "教研组");
		orgTypeNameMap.put(2, "年级组");
		orgTypeNameMap.put(3, "备课组");
		orgTypeNameMap.put(4, "管理组");
		orgTypeNameMap.put(5, "其他");
		orgTypeNameMap.put(6, "科室组");
	}
	
	public final static String ROLENAME_PARENT_FATHER = "爸爸";
	public final static String ROLENAME_PARENT_MOTHER = "妈妈";
	public final static String ROLENAME_PARENT_GRANDFATHER = "爷爷";
	public final static String ROLENAME_PARENT_GRANDMOTHER = "奶奶";
	public final static String ROLENAME_PARENT_GRANDPA = "外公";
	public final static String ROLENAME_PARENT_GRANDMA = "外婆";
	public final static String ROLENAME_PARENT_PARENT = "家长";
	public final static String ROLENAME_TEACHER = "老师";

    public final static int PROLE_FATHER = 1;
    public final static int PROLE_MOTHER = 2;
    public final static int PROLE_GRANDFATHER = 3;
    public final static int PROLE_GRANDMOTHER = 4;
    public final static int PROLE_GRANDPA = 5;
    public final static int PROLE_GRANDMA = 6;
    public final static int PROLE_PARENT = 7;

	public final static String ROLETYPE_STUDENT = "Student";
	public final static String ROLETYPE_PARENT = "Parent";
    
    public static boolean isParent(String role) {
		return ROLETYPE_PARENT.equals(role);
	}

    public static boolean isParentOrStudent(String role) {
		return ROLETYPE_PARENT.equals(role) || ROLETYPE_STUDENT.equals(role);
	}
    private final static Comparator<JSONObject> lessonSort = new SortComparator("lessonId");
}
