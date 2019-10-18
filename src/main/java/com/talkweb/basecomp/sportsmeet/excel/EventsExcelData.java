package com.talkweb.basecomp.sportsmeet.excel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.common.data.SchoolData;
import com.talkweb.basecomp.common.importfile.ExcelData;

public class EventsExcelData extends ExcelData {
	private static final long serialVersionUID = 1L;
	protected Map<String, String> eventsNameMap = new HashMap<>();

	public Map<String, String> getEventsNameMap() {
		return eventsNameMap;
	}

	public void setEventsNameMap(Map<String, String> eventsNameMap) {
		this.eventsNameMap = eventsNameMap;
	}

	@Override
	public List<JSONObject> getImportResult(JSONObject param) {
		this.importCount = 0;
		List<JSONObject> scoreList = new ArrayList<>();
		for (int i = 0; i < this.rowList.size(); i++) {
    		String[] values = this.valueList.get(i);
    		if (values == null || values.length < 2)
    			continue;

    		if (StringUtils.isEmpty(values[1]))
    			continue;
			if (StringUtils.isEmpty(values[0]))
				continue;
    		JSONObject row = new JSONObject();
    		row.putAll(param);
			row.put("eventsName", values[0]);
			row.put("type", values[1]);
			scoreList.add(row);
    		this.importCount++;
		}
		return scoreList;
	}

	@Override
    protected String getFieldValue(SchoolData data) {
    	String val = cellValue;
		if (this.headerIndex == 0) {//项目名称不能重复
			if (this.eventsNameMap.containsKey(val) || this.rowMap.containsKey(val)) {
				errMsg = "项目[" + val + "]重复";
				return null;
			}
			this.rowMap.put(val, this.rowIndex);
		} else if (this.headerIndex == 1) {//项目类型检测
			if ("田赛".equals(val)) {
				val = "1";
			} else if ("径赛".equals(val)) {
				val = "2";
			} else {
				errMsg = "项目类别[" + val + "]不存在";
				return null;
			}
		}
		return val;
    }
}
