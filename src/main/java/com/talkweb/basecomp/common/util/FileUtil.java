package com.talkweb.basecomp.common.util;

//import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
//import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.alibaba.fastjson.JSONObject;

import java.io.ByteArrayOutputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
//import java.util.Map;
import java.util.List;

/**
 * 无纸化会议
 * created  by User: lzq
 * Date: 2013/09/05 11:39:
 */
public class FileUtil {

    public static String fixAttachmentName(HttpServletRequest req, String fileName ){
    	if (req == null) return fileName;
        final String agent = req.getHeader("User-Agent");
        try {
            return fixAttachmentName(agent,fileName);
        } catch (UnsupportedEncodingException e) {
            return fileName;
        }
    }
    public static String fixAttachmentName(String userAgent, String filename) throws UnsupportedEncodingException {
        String fixed;
        String new_filename = URLEncoder.encode(filename, "UTF8");
        // 如果没有UA，则默认使用IE的方式进行编码，因为毕竟IE还是占多数的
        fixed =  new_filename ;
        if (userAgent != null) {
            userAgent = userAgent.toLowerCase();
            // IE浏览器，只能采用URLEncoder编码
            if (userAgent.indexOf("msie") != -1) {
                fixed =  new_filename ;
            }
            // Opera浏览器只能采用filename*
            else if (userAgent.indexOf("opera") != -1) {
                fixed ="UTF-8''" + new_filename;
            }
            // Safari浏览器，只能采用ISO编码的中文输出
            else if (userAgent.contains("firefox") || userAgent.contains("chrome") || userAgent.indexOf("safari") != -1) {
                fixed =  new String(filename.getBytes("UTF-8"), "ISO8859-1") ;
            }
            // Chrome浏览器，只能采用MimeUtility编码或ISO编码的中文输出
            else if (userAgent.indexOf("applewebkit") != -1) {
                fixed = new_filename;// = MimeUtility.encodeText(filename, "UTF8", "B");
            }
            // FireFox浏览器，可以使用MimeUtility或filename*或ISO编码的中文输出
            else if (userAgent.indexOf("mozilla") != -1) {
                fixed =  new_filename;
            }
        }
        //logger.debug("fixed attachment,useragent[{}} [{}] ->[{}] ", userAgent, filename, fixed);
        return fixed;
    }

    public static ResponseEntity<byte[]> downloadData(HttpServletRequest req, 
    		String filename, String url) throws Exception {
		return downloadData(req, filename, urlToBytes(url));
    }

    public static ResponseEntity<byte[]> downloadData(HttpServletRequest req, 
    		String filename, byte[] stream) throws Exception {
    	filename = fixAttachmentName(req, filename);
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.valueOf(MimeUtils.getMimeType(filename));
        headers.setContentType(type);//设置MIME类型
        headers.setContentDispositionFormData("attachment", filename);
    	//headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		return new ResponseEntity<byte[]>(stream, headers, HttpStatus.OK);
    }

    public static byte[] urlToBytes(String str) throws Exception {
        ByteArrayOutputStream fOut = new ByteArrayOutputStream();
		URL url = new URL(str);
        IOUtils.copy(url.openStream(), fOut);
        byte[] bt = fOut.toByteArray();
        return bt;
    }
    
    public static ResponseEntity<byte[]> batchDownload(HttpServletRequest req, 
    		List<JSONObject> list) throws Exception {
    	return null;
    }
}
