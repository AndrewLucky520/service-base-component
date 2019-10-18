package com.talkweb.basecomp.common.importfile;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.common.data.DataCache;
import com.talkweb.basecomp.common.data.DataUtil;
import com.talkweb.basecomp.common.data.SchoolData;
import com.talkweb.basecomp.common.util.ExcelUtil;
import com.talkweb.basecomp.common.util.JSONUtil;

/**
 * 导入数据处理器
 * @author xixi1979
 *
 */
public abstract class ExcelData implements Serializable {
	private static final long serialVersionUID = 1L;
	protected List<String> titleNames = null;
	protected List<String> titleCodes = null;
	protected List<Integer> titleNeeds = null;
	protected Map<String, Integer> rowMap = new HashMap<>();
	protected JSONObject inputParam;
	protected List<List<String>> rowList;
	protected List<String> headerList;
	protected List<String[]> valueList;
	protected boolean repeatCheck = true;

	protected Map<String, String> classNameMap;
	protected int headerIndex;
	protected int rowIndex;
	protected int cellIndex;
	protected int importCount;
	protected String errMsg;
	protected String cellValue;

	public ExcelData() {
	}
	
	public ExcelData(JSONObject inputParam) {
		this.inputParam = inputParam;
	}
	
	public JSONObject checkTitle(InputStream in, String fileName) throws IOException {
		this.initExcelData(in, fileName);
		if (this.rowList.isEmpty() || headerList.isEmpty()) {
			return JSONUtil.getResponse(-1, "文件内容错误或不符合规范！");
		}
		
		for (String str: this.headerList) {
    		int headerIndex = getHeaderIndex(str);
    		if (headerIndex == -1) {
				return JSONUtil.getResponse(-1, "文件格式正确, [" + str + "]字段不匹配！");
    		}
		}

		for (int i = 0; i < this.titleNeeds.size(); i++) {
			if (this.titleNeeds.get(i) == 1 && !this.headerList.contains(titleNames.get(i))) {
				return JSONUtil.getResponse(-1, "文件格式正确, [" + titleNames.get(i) + "]字段不匹配！");
			}
		}
		
		return null;
	}

	public JSONObject checkData(JSONObject result, DataCache dataCache) {
    	JSONObject obj = new JSONObject();
		try {
			List<String> err = checkData(dataCache);
	    	if (err.size() > 0) {
				obj = JSONUtil.getResponse(-1, "导入失败，" + DataUtil.listToString(err));
				return obj;
	    	}
	    	return null;
		} catch(Exception ex) {
			ex.printStackTrace();
			obj = JSONUtil.getResponse(-1, "导入失败: " + ex.getMessage());
		}
		return obj;
	}

	public abstract List<JSONObject> getImportResult(JSONObject param);
	
	protected List<String> checkData(DataCache dataCache) {
		checkTitleNames();//重构title
		
		SchoolData data = dataCache.getSchoolData(this.inputParam);
		List<String> allErrList = new ArrayList<>();
		valueList = new ArrayList<>();
    	for (int i = 0; i < this.rowList.size(); i++) {
    		String[] values = new String[this.titleNames.size()];
			valueList.add(values);
			
    		int emptyCount = 0;
			List<String> errList = new ArrayList<>();
			
    		for (int j = 0; j < this.titleNames.size(); j++) {
	    		String val = getFieldValue(i, j, data);
	    		if (val == null) {
	    			emptyCount++;
	    			errList.add(this.errMsg);
	    		} else if (!val.isEmpty()) {
	    			values[j] = val;
	    		}
    		}
    		
    		if (emptyCount > 0 && emptyCount < this.titleNames.size()) {//允许存在空行
    			allErrList.addAll(errList);
    		}
    	}
    	return allErrList;
    }

	protected void checkTitleNames() {
	}
	
    private String getFieldValue(int rowIndex, int headerIndex, SchoolData data) {
    	this.headerIndex = headerIndex;
    	this.rowIndex = rowIndex;
    	return getFieldValue(null, null, data);
    }
    
    protected String getFieldValue(String rowValue, String headerName, SchoolData data) {
    	errMsg = null;
    	cellIndex = this.getCellIndex(headerName, this.headerIndex);
		if (headerIndex == -1) {
			errMsg = "字段不匹配";
			return null;
		}
		
    	if (rowValue == null) {
			if (cellIndex < 0)
				return "";//可选字段允许为空
			cellValue = this.rowList.get(rowIndex).get(cellIndex);
    	} else {
    		cellValue = rowValue;
    	}
    	
    	try {
			cellValue = cellValue != null ? cellValue.replaceAll(" ", "") : null;
			if (StringUtils.isEmpty(cellValue)) {
				if (this.titleNeeds.get(headerIndex) == 0)
					return "";//允许为空
				errMsg = this.titleNames.get(headerIndex) + "不能为空";
				return null;
			}
			return getFieldValue(data);
    	} finally {
    		if (rowValue != null && cellIndex >= 0) {//更新缓存excel数据
    			List<String> row = this.rowList.get(rowIndex);
				if (row != null) {
					row.set(cellIndex, rowValue);
				}
    		}
    	}
    }

    protected String getFieldValue(SchoolData data) {
    	if (classNameMap == null) {
    		classNameMap = data.getClassNameMap();
    	}
    	String val = cellValue;
    	String[] values = this.valueList.get(rowIndex);
    	switch (this.headerIndex) {
    	case 1:
			//班级
			val = classNameMap.get(cellValue);
			if (val == null) {
				errMsg = "班级[" + cellValue + "]无匹配记录";
				return null;
			}
			break;
    	case 2:
			//姓名
    		String classId = "";
    		if (values != null) {
    			if (StringUtils.isNotBlank(values[1])) {
    				classId = values[1];
    			}
    		}
			List<String> str = data.getStudentIdByName(cellValue, classId);
			if (str == null || str.isEmpty()) {
				errMsg = "姓名[" + cellValue + "]无匹配记录";
				return null;
			}
			if (str.size() > 1) {
				errMsg = "姓名[" + cellValue + "]匹配到多条记录";
				return null;
			}
			val = str.get(0);
			if (repeatCheck) {
	    		if (this.rowMap.containsKey(val)) {
					errMsg = "姓名[" + cellValue + "]重复";
					return null;
	    		}
	    		this.rowMap.put(val, this.rowIndex);
			}
			break;
    	}
		return val;
    }
	
    protected int getCellIndex(String headerName, int index) {
    	if (headerName != null) {
    		headerIndex = getHeaderIndex(headerName);
    	} else {
    		headerName = this.titleNames.get(index);
    	}
    	return this.headerList.indexOf(headerName);
    }

    protected int getHeaderIndex(String headerName) {
		return this.titleNames.indexOf(headerName);
    }

	protected void initExcelData(InputStream in, String fileName) throws IOException {
		this.rowList = ExcelUtil.importTable(in, fileName);

		if (!this.rowList.isEmpty()) {
			this.headerList = new ArrayList<>();
			for (String str: this.rowList.get(0)) {
				this.headerList.add(str.replaceAll(" ", ""));
			}
			this.rowList.remove(0);
		}
	}

	public List<String> getTitleNames() {
		return titleNames;
	}

	public void setTitleNames(List<String> titleNames) {
		this.titleNames = titleNames;
	}

	public List<String> getTitleCodes() {
		return titleCodes;
	}

	public void setTitleCodes(List<String> titleCodes) {
		this.titleCodes = titleCodes;
	}

	public List<Integer> getTitleNeeds() {
		return titleNeeds;
	}

	public void setTitleNeeds(List<Integer> titleNeeds) {
		this.titleNeeds = titleNeeds;
	}

	public JSONObject getInputParam() {
		return inputParam;
	}

	public void setInputParam(JSONObject inputParam) {
		this.inputParam = inputParam;
	}

	public List<List<String>> getRowList() {
		return rowList;
	}

	public void setRowList(List<List<String>> rowList) {
		this.rowList = rowList;
	}

	public List<String> getHeaderList() {
		return headerList;
	}

	public void setHeaderList(List<String> headerList) {
		this.headerList = headerList;
	}

	public List<String[]> getValueList() {
		return valueList;
	}

	public void setValueList(List<String[]> valueList) {
		this.valueList = valueList;
	}

	public Map<String, Integer> getRowMap() {
		return rowMap;
	}

	public void setRowMap(Map<String, Integer> rowMap) {
		this.rowMap = rowMap;
	}

	public boolean isRepeatCheck() {
		return repeatCheck;
	}

	public void setRepeatCheck(boolean repeatCheck) {
		this.repeatCheck = repeatCheck;
	}

	public int getImportCount() {
		return importCount;
	}
	
}
