package com.talkweb.basecomp.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelUtil {

	public ExcelUtil(){
		
	}

    public static List<List<String>> importTable(InputStream in) throws IOException {
    	return importTable(in, "xls", 0, -1);
    }
    
    public static List<List<String>> importTable(InputStream in, String fileName) throws IOException {
    	return importTable(in, fileName, 0, -1);
    }
    
    public static List<List<String>> importTable(InputStream in, String fileName, int startIndex, int rowCount) throws IOException {
    	String ext = "xls";
    	if (fileName != null)
    		ext = FilenameUtils.getExtension(fileName);
    	
    	List<List<String>> res = new ArrayList<>();
		Workbook workbook = null;
		if ("xls".equalsIgnoreCase(ext)) {
			workbook = new HSSFWorkbook(in);
		} else if ("xlsx".equalsIgnoreCase(ext)) {
			workbook = new XSSFWorkbook(in);
		}
		if (rowCount <= 0) {
			rowCount = workbook.getNumberOfSheets();
		}
    	try {
	    	for (int i = startIndex; i < rowCount; i++) {
	    		Sheet sheet = workbook.getSheetAt(i);
	    		for (int j = 0; j < sheet.getLastRowNum() + 1; j++) {
	    			Row row = sheet.getRow(j);
	    			if (row == null) continue;
	        		List<String> cells = new ArrayList<String>();
	    			for(int k = 0; k < row.getLastCellNum(); k++) {
	    				Cell cell = row.getCell(k);
	    				String txt = "";
	    				if (cell != null) {
	    					txt = getCellValue(cell);
	    				}
	    				cells.add(txt);
	    			}
	    			res.add(cells);
	    		}
	    	}
    	} finally {
    		workbook.close();
    	}
    	return res;
    }

    @SuppressWarnings("deprecation")
	public static String getCellValue(Cell cell) {
		if (cell == null)
			return "";
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			String str = null;
			String temStr = cell.getRichStringCellValue().getString().trim();
			str = temStr;
			return str;
		case Cell.CELL_TYPE_BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case Cell.CELL_TYPE_FORMULA:
			String temp = null;
			try {
				temp = String.valueOf(cell.getStringCellValue());
			} catch (IllegalStateException e) {
				double d = cell.getNumericCellValue();
				return new DecimalFormat("#.##").format(d);
			}
			return temp;
		case Cell.CELL_TYPE_NUMERIC:
			double d = cell.getNumericCellValue();
			return new DecimalFormat("#.##").format(d);
		}
		return "";
	}

    public static void exportTable(String title, String[] headers, List<String[]> rowData, OutputStream out) throws Exception {
    	XSSFWorkbook workbook = new XSSFWorkbook();
    	XSSFSheet  sheet = workbook.createSheet(title);
    	XSSFCellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        
    	XSSFRow row = sheet.createRow(0);
        int[] cellWidth = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = row.createCell(i);
            //sheet.autoSizeColumn(i,true);
            cell.setCellStyle(style);
            String h = headers[i];
            if (h == null) h = "";
            XSSFRichTextString text = new XSSFRichTextString(h);
            cell.setCellValue(text);
            setColumnWidth(sheet, i, h, cellWidth);
        }
    	int i = 1;
    	for (String[] strs : rowData){
    		 row = sheet.createRow(i++);
    		 for (int j = 0; j < strs.length; j++) {
    			 XSSFCell cell = row.createCell(j);
    			 String val = strs[j];
    			 XSSFRichTextString text = new XSSFRichTextString(val);
    			 cell.setCellValue(text);
                 setColumnWidth(sheet, j, val, cellWidth);
    		 }
    	}
        
    	try {
    		workbook.write(out); 
    	} finally {
    		workbook.close();
    	}
    }

    static void setColumnWidth(XSSFSheet sheet, int index, String val, int[] defaul) {
        if (val != null && val.length() > 0) {
            int len = val.getBytes().length;
            len = (len + val.length())/2 + 5;
            //logger.debug("exportTable value:" + val + "; len:" + len);
            if (defaul[index] < len) {
                defaul[index] = len;
                sheet.setColumnWidth(index, len * 256);
            }
        }
    }

    public static void exportTable2(String title, String[] headers, List<String[]> rowData, OutputStream out) throws Exception {
    	HSSFWorkbook workbook = new HSSFWorkbook();
    	HSSFSheet  sheet = workbook.createSheet(title);
    	HSSFCellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        
        HSSFRow row = sheet.createRow(0);
        int[] cellWidth = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
        	HSSFCell cell = row.createCell(i);
            //sheet.autoSizeColumn(i,true);
            cell.setCellStyle(style);
            String h = headers[i];
            if (h == null) h = "";
            HSSFRichTextString text = new HSSFRichTextString(h);
            cell.setCellValue(text);
            setColumnWidth2(sheet, i, h, cellWidth);
        }
    	int i = 1;
    	for (String[] strs : rowData){
    		 row = sheet.createRow(i++);
    		 for (int j = 0; j < strs.length; j++) {
    			 HSSFCell cell = row.createCell(j);
    			 String val = strs[j];
    			 HSSFRichTextString text = new HSSFRichTextString(val);
    			 cell.setCellValue(text);
                 setColumnWidth2(sheet, j, val, cellWidth);
    		 }
    	}
        
    	try {
    		workbook.write(out); 
    	} finally {
    		workbook.close();
    	}
    }

    static void setColumnWidth2(HSSFSheet sheet, int index, String val, int[] defaul) {
        if (val != null && val.length() > 0) {
            int len = val.getBytes().length;
            len = (len + val.length())/2 + 5;
            //logger.debug("exportTable value:" + val + "; len:" + len);
            if (defaul[index] < len) {
                defaul[index] = len;
                sheet.setColumnWidth(index, len * 256);
            }
        }
    }
}
