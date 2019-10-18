package com.talkweb.basecomp.common.util;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * @author xixi
 *
 */
public class HttpClientUtil {
    
	public static JSONObject postRequest(String url, JSONObject param) throws Exception {
		return postRequest(url, null, param);
	}
	
	public static JSONObject postRequest(String url, String token, JSONObject param) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;  
		JSONObject ret = null;
		try {  
			HttpUriRequest httppost = getRequest(url, token, param);
		    response = httpClient.execute(httppost);
		    
		    int status = response.getStatusLine().getStatusCode();
		    if (status == 200) {
			    String str = EntityUtils.toString(response.getEntity());
			    ret = JSONObject.parseObject(str);
		    } else {
		    	throw new IOException("请求错误: " + response.getStatusLine());
		    }
        } finally {
			closeResponse(response);
			closeClient(httpClient);
        }
		return ret;
	}

	private static HttpUriRequest getRequest(String url, String token, JSONObject param) {
		if (param == null) {
			HttpGet httppost = new HttpGet(url);
			return httppost;
		} else {
			HttpPost httppost = new HttpPost(url);
			if (token != null)
				httppost.addHeader("Access-Token", token);
			if (param != null) {
				StringEntity input = new StringEntity(param.toString(), "utf-8"); 		// 解决中文乱码问题
				input.setContentEncoding("UTF-8");    
				input.setContentType("application/json");
			    httppost.setEntity(input);
			}
			return httppost;
		}
	}
	
	private static void closeResponse(CloseableHttpResponse closeableHttpResponse) {
		if (closeableHttpResponse != null) {
            try {
                closeableHttpResponse.close();
            } catch (Exception e) {
            }
        }
	}
	
	private static void closeClient(CloseableHttpClient client) {
		if (client != null) {
            try {
            	client.close();
            } catch (Exception e) {
            }
        }
	}
	
}
