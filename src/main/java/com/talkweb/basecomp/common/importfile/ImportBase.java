package com.talkweb.basecomp.common.importfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.common.data.DataCache;
import com.talkweb.basecomp.common.util.JSONUtil;

public abstract class ImportBase {

	@Autowired
	private DataCache dataCache;

	static final Logger logger = LoggerFactory.getLogger(ImportBase.class);

	public JSONObject uploadExcel(MultipartFile file, String paramStr) {
		try {
			JSONObject param = JSONObject.parseObject(paramStr);

			// 获取源文件后缀名
			String fileName = file.getOriginalFilename();
			String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
			if (!prefix.equalsIgnoreCase("xls") && !prefix.equalsIgnoreCase("xlsx")) {
				return JSONUtil.getResponse(-1, "文件不是excel格式！");
			} else {
				ExcelData excelData = getExcelData(param);
				JSONObject obj = excelData.checkTitle(file.getInputStream(), file.getOriginalFilename());
				if (obj == null) {
					obj = excelData.checkData(param, dataCache);
					if (obj == null) {
						obj = importInternal(excelData);
					}
				}
				return obj;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return JSONUtil.getResponse(-1, "导入异常。");
		}
	}

	protected abstract ExcelData getExcelData(JSONObject param);
	
	protected abstract JSONObject importInternal(ExcelData edata);
}