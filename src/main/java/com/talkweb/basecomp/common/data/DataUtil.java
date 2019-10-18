package com.talkweb.basecomp.common.data;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class DataUtil {
	public static String getUUID() {
		return UUID.randomUUID().toString();
	}

	public static float castFloatTowPoint(float param) {
		BigDecimal b = new BigDecimal(param);
		float rs = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		if (!Float.isNaN(rs)) {
			return rs;
		} else {
			return 0;
		}
	}

	public static String formatNumber(double val) {
		return new DecimalFormat("#.##").format(val);
	}

	public static String formatNumber(double val, int digit) {
		if (digit == 1)
			return new DecimalFormat("#.#").format(val);
		if (digit == 2)
			return new DecimalFormat("#.##").format(val);
		if (digit == 3)
			return new DecimalFormat("#.###").format(val);
		return new DecimalFormat("#").format(val);
	}

	public static List<String> stringToList(String str) {
		return stringToList(str, ",");
	}

	public static List<String> stringToList(String str, String sep) {
		List<String> ret = new ArrayList<>();
		if (StringUtils.isEmpty(str))
			return ret;

		for (String sub : str.split(sep)) {
			if (!ret.contains(sub))
				ret.add(sub);
		}
		return ret;
	}

	public static List<Long> stringToLongList(String str) {
		if (StringUtils.isEmpty(str))
			return null;

		List<Long> ret = new ArrayList<>();
		for (String s : str.split(",")) {
			Long l = Long.parseLong(s);
			if (!ret.contains(l))
				ret.add(l);
		}
		return ret;
	}

	public static <E> String listToString(Collection<E> list) {
		return listToString(list, ",");
	}

	public static <E> String listToString(Collection<E> list, String sep) {
		String str = "";
		if (list != null) {
			for (Object obj : list) {
				if (obj != null) {
					if (str.length() > 0)
						str += sep;
					str += obj.toString();
				}
			}
		}
		return str;
	}

	public static Map<String, String> getIdNameMap(Collection<JSONObject> list) {
		return getKeyValueMap(list, "id", "name");
	}

	public static Map<String, String> getKeyValueMap(Collection<JSONObject> list, String key, String value) {
		Map<String, String> map = new HashMap<>();
		if (list != null && !list.isEmpty()) {
			for (JSONObject account : list) {
				String id = account.getString(key);
				String name = account.getString(value);
				if (!StringUtils.isEmpty(id))
					map.put(id, name);
			}
		}
		return map;
	}

	public static List<String> getIdList(Collection<JSONObject> list) {
		return getFieldList(list, "id");
	}

	public static List<String> getFieldList(Collection<JSONObject> list, String key) {
		List<String> ret = new ArrayList<>();
		if (list != null && !list.isEmpty()) {
			for (JSONObject obj : list) {
				String val = obj.getString(key);
				if (!StringUtils.isEmpty(val) && !ret.contains(val))
					ret.add(val);
			}
		}
		return ret;
	}

	public static String getAllValues(Collection<JSONObject> list, String key) {
		List<String> res = getFieldList(list, key);
		return listToString(res);
	}
	
	public static Map<String, JSONObject> getIdMap(Collection<JSONObject> list) {
		return getKeyMap(list, "id");
	}

	public static Map<String, JSONObject> getKeyMap(Collection<JSONObject> list, String key) {
		Map<String, JSONObject> map = new HashMap<>();
		if (list != null && !list.isEmpty()) {
			for (JSONObject obj : list) {
				String id = obj.getString(key);
				map.put(id, obj);
			}
		}
		return map;
	}

	public static <E> List<JSONObject> getTextValueList(Collection<JSONObject> list) {
		return getTextValueList(null, list, false);
	}
	
	public static <E> List<JSONObject> getTextValueList(JSONObject params, Collection<JSONObject> list, boolean needSort) {
		List<JSONObject> data = new ArrayList<>();
		List<String> all = new ArrayList<>();
		for (JSONObject obj : list) {
			JSONObject item = new JSONObject();
			item.put("text", obj.get("name"));
			item.put("value", obj.get("id"));
			data.add(item);
			all.add(obj.getString("id"));
		}
		if (needSort)
			Collections.sort(data, SortComparator.textSort);
		boolean isAll = params == null ? false : params.getBooleanValue("isAll");
		if (isAll && all.size() > 0) {
			JSONObject item = new JSONObject();
			item.put("text", "全部");
			item.put("value", DataUtil.listToString(all));
			data.add(0, item);
		}
		return data;
	}

	public static List<JSONObject> getClassType() {
		List<JSONObject> data = new ArrayList<>();
		JSONObject obj = new JSONObject();
		obj.put("name", "文科");
		obj.put("id", 1);
		data.add(obj);
		obj = new JSONObject();
		obj.put("name", "理科");
		obj.put("id", 2);
		data.add(obj);
		obj = new JSONObject();
		obj.put("name", "综合");
		obj.put("id", 0);
		data.add(obj);
		return data;
	}

	public static String getStudentAccountId(JSONObject params) {
		if (!StringUtils.isEmpty(params.getString("studentAccountId")))
			return params.getString("studentAccountId");
		if (params.containsKey("curUser"))
			return params.getJSONObject("curUser").getString("accountId");
		return params.getString("accountId");
	}

	public static String getStudentAccountName(JSONObject params) {
		if (!StringUtils.isEmpty(params.getString("studentAccountName")))
			return params.getString("studentAccountName");
		if (params.containsKey("curUser"))
			return params.getJSONObject("curUser").getString("accountName");
		return params.getString("accountName");
	}

	public static List<String> getDeanClassIdList(JSONObject param) {
		Set<String> classIds = new HashSet<>();
		JSONArray deanOfClassId = param.getJSONArray("deanOfClassIds");
		if (deanOfClassId != null) {
			for (Object cls : deanOfClassId) {
				classIds.add(cls.toString());
			}
		}
		return new ArrayList<>(classIds);
	}

	public static List<String> getUserClassIdList(JSONObject param) {
		Set<String> classIds = new HashSet<>();
		JSONArray deanOfClassId = param.getJSONArray("deanOfClassIds");
		if (deanOfClassId != null) {
			for (Object cls : deanOfClassId) {
				classIds.add(cls.toString());
			}
		}
		JSONArray courlist = param.getJSONArray("courseIds");
		if (courlist != null) {
			for (int i = 0; i < courlist.size(); i++) {
				JSONObject obj = courlist.getJSONObject(i);
				String classId = obj.getString("classId");
				classIds.add(classId);
			}
		}
		return new ArrayList<>(classIds);
	}

	public static List<String> getUserOrgIdList(JSONObject param) {
		Set<String> orgIdList = new HashSet<>();
		JSONArray deanOfOrgIds = param.getJSONArray("deanOfOrgIds");
		if (deanOfOrgIds != null) {
			for (Object id : deanOfOrgIds) {
				orgIdList.add(id.toString());
			}
		}
		JSONArray orgIds = param.getJSONArray("orgIds");
		if (orgIds != null) {
			for (Object id : orgIds) {
				orgIdList.add(id.toString());
			}
		}
		return new ArrayList<>(orgIdList);
	}

	public static String getAllStageType(JSONObject param) {
		JSONArray stageList = param.getJSONArray("stageTypes"); // 学习阶段，幼小初高
		List<String> list = new ArrayList<>();
		if (stageList != null) {
			for (Object stage : stageList) {
				int tmp = Integer.parseInt(stage.toString());
				if (tmp >= 2) { // 从小学开始计算
					list.add(String.valueOf(tmp - 1));
				}
			}
		}
		return listToString(list);
	}

	public static String formatTerm(String xnxq) {
		StringBuilder result = new StringBuilder();
		if (!xnxq.matches("[0-9]+")) {
			throw new IllegalArgumentException("参数必须为数字格式的字符串");
		}

		String term = xnxq.substring(4, 5);
		if (!(term.equals("1") || term.equals("2"))) {
			throw new IllegalArgumentException("参数的最后一位只能为1或者2");
		}

		String year = xnxq.substring(0, 4);
		int iYear = Integer.valueOf(year);
		int toYear = iYear + 1;
		if ("1".equals(term)) {
			result.append(year).append("-").append(toYear).append("学年第一学期");
		} else {
			result.append(year).append("-").append(toYear).append("学年第二学期");
		}
		return result.toString();
	}

	public static Map<String, JSONObject> convertToMap(List<JSONObject> data, String keyProperty) {
		Map<String, JSONObject> result = new HashMap<>();

		if (keyProperty == null || "".equals(keyProperty))
			return result;

		if (data != null && data.size() > 0) {
			for (JSONObject item : data) {
				String key = item.get(keyProperty) == null ? "" : item.get(keyProperty).toString();
				result.put(key, item);
			}
		}

		return result;
	}

	public static List<JSONObject> grepJsonKeyBySingleVal(String keyName, String keyValue, List<JSONObject> sorList) {
		List<JSONObject> rs = new ArrayList<JSONObject>();
		for (JSONObject o : sorList) {
			if (o.containsKey(keyName) && o.getString(keyName) != null) {
				String oVal = o.getString(keyName);
				if (oVal.equalsIgnoreCase(keyValue)) {
					rs.add(o);
				}
			}
		}

		return rs;
	}

	public static List<JSONObject> grepJsonKeyByVal(String[] keyName, String[] keyValue, List<JSONObject> sorList) {

		if (keyName.length < keyValue.length) {
			return sorList;
		}
		List<JSONObject> rs = new ArrayList<JSONObject>();
		for (JSONObject o : sorList) {
			boolean grs = true;
			for (int i = 0; i < keyName.length; i++) {
				if (o.containsKey(keyName[i]) && o.getString(keyName[i]) != null) {
					String oVal = o.getString(keyName[i]);
					if (!oVal.equalsIgnoreCase(keyValue[i])) {
						grs = false;
						break;
					}
				} else {
					grs = false;
					break;
				}
			}
			if (grs) {
				rs.add(o);
			}
		}
		return rs;
	}

	public static String getTermInfo(JSONObject param) {
		String xnxq = param.getString("selectedSemester");
		if (!StringUtils.isEmpty(xnxq))
			return xnxq;
		xnxq = param.getString("xnxq");
		if (!StringUtils.isEmpty(xnxq))
			return xnxq;
		xnxq = param.getString("termInfo");
		if (!StringUtils.isEmpty(xnxq))
			return xnxq;
		xnxq = param.getString("termInfoId");
		if (!StringUtils.isEmpty(xnxq))
			return xnxq;
		xnxq = param.getString("curTermInfoId");
		if (!StringUtils.isEmpty(xnxq))
			return xnxq;
		return null;
	}

	public static Long getSchoolId(JSONObject param) {
		Long xxdm = param.getLong("schoolId");
		if (xxdm != null && xxdm != 0)
			return xxdm;
		xxdm = param.getLong("xxdm");
		if (xxdm != null && xxdm != 0)
			return xxdm;
		return null;
	}

	public static String getGradeId(JSONObject param) {
		String str = param.getString("gradeId");
		if (!StringUtils.isEmpty(str))
			return str;
		str = param.getString("nj");
		if (!StringUtils.isEmpty(str))
			return str;
		return null;
	}

	public static String getClassId(JSONObject param) {
		String str = param.getString("classId");
		if (!StringUtils.isEmpty(str))
			return str;
		str = param.getString("bhs");
		if (!StringUtils.isEmpty(str))
			return str;
		str = param.getString("bh");
		if (!StringUtils.isEmpty(str))
			return str;
		str = param.getString("bhStr");
		if (!StringUtils.isEmpty(str))
			return str;
		return null;
	}

	public static String getMaxUsedGrade(JSONObject input, List<String> njList) {
		String xnxq = input.getString("xnxq"); // 学年学期
		String xn = xnxq.substring(0, xnxq.length() - 1); // 学年
		int idx = 0;
		int minRxnd = Integer.parseInt(DataUtil.getGradeStartYear(njList.get(0), xn));
		for (int i = 0, len = njList.size(); i < len; i++) {
			int rxnd = Integer.parseInt(DataUtil.getGradeStartYear(njList.get(i), xn));
			if (minRxnd > rxnd) {
				minRxnd = rxnd;
				idx = i;
			}
		}
		return njList.get(idx);
	}
	
	public static int convertLevel(String levelOrYear, String xn) {
		return Integer.valueOf(xn) - Integer.valueOf(levelOrYear) + 10;
	}

	public static int convertLevel(int levelOrYear, String xn) {
		return Integer.valueOf(xn) - levelOrYear + 10;
	}

	public static String getGradeStartYear(String usedGrade, String xn) {
		int grade = Integer.valueOf(usedGrade);
		int level = convertLevel(grade, xn);
		return getGradeStartYear(level, xn);
	}

	public static String getGradeStartYear(int level, String xn) {
		// 年级代码
		long year = convertLevel(level, xn);
		// 培养层次
		Integer pycc = getPYCCByNJCode(level);
		Integer qsnj = null;
		if (null != pycc) {
			// 起始年级
			qsnj = getQSNJByPYCC(pycc);
		}
		String rxnd = null;
		// 入学年度=学年-起始年级
		if (null != qsnj) {
			rxnd = String.valueOf(year + qsnj);
		} else {
			rxnd = "";
		}

		return rxnd;
	}

	public static Integer getPYCCByNJCode(long njCode) {
		if (njCode <= 15) {
			return 1;
		} else if (njCode <= 18) {
			return 2;
		} else if (njCode <= 21) {
			return 3;
		} else {
			return null;
		}
	}

	public static float[][] trendLineCoordinate(List<Float> x, List<Float> y) {
		// 趋势线的起点和终点坐标存放变量
		float startEnd[][] = new float[2][2];

		if (x == null || y == null)
			return startEnd;

		if (x.size() == 0 || y.size() == 0)
			return startEnd;

		if (x.size() != y.size())
			return startEnd;

		float averX = 0, averY = 0;// x的平均值，y的平均值
		float sumXY = 0, sumXX = 0;// xy的乘积之和，xx的乘积之和
		float n = x.size();// 元素个数

		float b = 0;// 斜率
		float a = 0;// 偏移

		for (int i = 0; i < n; i++) {
			averX = averX + x.get(i) / n;
			averY = averY + y.get(i) / n;

			sumXY = sumXY + x.get(i) * y.get(i);
			sumXX = sumXX + x.get(i) * x.get(i);
		}

		b = (sumXY - n * averX * averY) / (sumXX - n * averX * averX);
		a = averY - b * averX;

		float startX = 0, endX = 0, startY = 0, endY = 0;// 起点和终点的[x,y]坐标

		startX = x.get(0);
		startY = a + b * startX;

		endX = x.get((int) n - 1);
		endY = a + b * endX;

		// 把起点终点的坐标存放到数组中
		startEnd[0][0] = startX;
		startEnd[0][1] = startY;
		startEnd[1][0] = endX;
		startEnd[1][1] = endY;

		return startEnd;
	}
	private static Integer getQSNJByPYCC(int pycc) {
		Integer qsnj = null;
		switch (pycc) {
		case 1:
			qsnj = 0;
			break;
		case 2:
			qsnj = 6;
			break;
		case 3:
			qsnj = 9;
			break;
		default:
			break;
		}
		return qsnj;
	}

}
