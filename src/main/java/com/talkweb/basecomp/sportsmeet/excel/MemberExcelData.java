package com.talkweb.basecomp.sportsmeet.excel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.common.data.SchoolData;
import com.talkweb.basecomp.common.importfile.ExcelData;

public class MemberExcelData extends ExcelData {
	private static final long serialVersionUID = 1L;
	protected Map<String, String> eventsNameMap = new HashMap<>();
	protected Map<String,String> memberMap = new HashMap<>();

	public Map<String, String> getEventsNameMap() {
		return eventsNameMap;
	}

	public void setEventsNameMap(Map<String, String> eventsNameMap) {
		this.eventsNameMap = eventsNameMap;
	}

	public Map<String, String> getMemberMap() {
		return memberMap;
	}

	public void setMemberMap(Map<String, String> memberMap) {
		this.memberMap = memberMap;
	}

	@Override
	public List<JSONObject> getImportResult(JSONObject param) {
		this.importCount = 0;
		List<JSONObject> scoreList = new ArrayList<>();
		for (int i = 0; i < this.rowList.size(); i++) {
    		String[] values = this.valueList.get(i);
    		if (values == null || values.length < 3)
    			continue;

    		if (StringUtils.isEmpty(values[2]))
    			continue;
			if (StringUtils.isEmpty(values[0]))
				continue;
    		JSONObject row = new JSONObject();
    		row.putAll(param);
			row.put("studentId", values[2]);
			row.put("eventsId", values[0]);
			if (values.length > 3) {
				row.put("record", values[3]);
			}
			scoreList.add(row);
    		this.importCount++;
		}
		return scoreList;
	}

	@Override
    protected String getFieldValue(SchoolData data) {
		if (this.headerIndex == 1 || this.headerIndex == 2) {
			return super.getFieldValue(data);
		}
    	String val = cellValue;
		if (this.headerIndex == 0) {
	    	if (eventsNameMap.containsKey(val)) {
	    		val = eventsNameMap.get(val);
	    	} else {
				errMsg = "项目[" + val + "]不存在";
				return null;
	    	}
	    	String[] values = this.valueList.get(rowIndex);
			if (values != null && StringUtils.isNotBlank(values[2])) {
		    	String key = val + ":" + values[2];
		    	if (this.rowMap.containsKey(key) || memberMap.containsKey(key)) {
					errMsg = "项目[" + cellValue + "]记录[" + data.getStudentName(values[2]) + "]重复";
					return null;
		    	}
				this.rowMap.put(key, this.rowIndex);
			}
		}
		return val;
    }
}
