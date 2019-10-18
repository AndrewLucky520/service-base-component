package com.talkweb.basecomp.common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * HttpGET获取token
 * @author zhanghuihui
 *
 */
public class HttpClientToken {
	protected final static Log log = LogFactory.getLog(HttpClientToken.class);
	/** 发送http请求 @author zhanghuihui **/
	public static JSONObject callHttpRemoteInterface(String url,String access_token) {
			 CloseableHttpClient httpclient= HttpClientBuilder.create().setDefaultRequestConfig(null).build();
			 String reponseResult="";
			 HttpGet httpget = new HttpGet(url);
			 if(access_token!=null){
				 httpget.addHeader("Access-Token", access_token);
			 }
			try {
				 CloseableHttpResponse response= httpclient.execute(httpget);
				 log.info("wechat http response:"+response.toString());
				 HttpEntity responseEntity = response.getEntity();
				 reponseResult=EntityUtils.toString(responseEntity);	
				 log.info("wechat http reponseResult:"+reponseResult.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return  JSON.parseObject(reponseResult);
	}
	/** 发送http请求 @author zhanghuihui **/
	public static JSONObject callHttpRemoteInterface(String url,String clientId,String secretId) {
			 CloseableHttpClient httpclient= HttpClientBuilder.create().setDefaultRequestConfig(null).build();
			 String reponseResult="";
			 HttpGet httpget = new HttpGet(url);
			 if(clientId!=null && secretId!=null){
				 httpget.addHeader("Client-Id", clientId);
				 httpget.addHeader("Client-Secret",secretId);
			 }
			
			try {
				 CloseableHttpResponse response= httpclient.execute(httpget);
				 log.info("wechat http response:"+response.toString());
				 HttpEntity responseEntity = response.getEntity();
				 reponseResult=EntityUtils.toString(responseEntity);	
				 log.info("wechat http reponseResult:"+reponseResult.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return  JSON.parseObject(reponseResult);
	}
	/** 发送http请求 @author zhanghuihui **/
	public static JSONObject callHttpRemoteInterfacePost(String url,String access_token, JSONObject jsonData) {
		CloseableHttpClient client = null;
		
		try {
			client = new SSLClient();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		String reponseResult="";
		HttpPost post=new HttpPost(url);
		 if(access_token!=null){
			 post.addHeader("Access-Token", access_token);
		 }
		
		StringEntity entity = new StringEntity(jsonData.toString(),"utf-8");//解决中文乱码问题    
        entity.setContentEncoding("UTF-8");    
        entity.setContentType("application/json");    
        post.setEntity(entity); 
		try {
			 CloseableHttpResponse response = client.execute(post);
			 HttpEntity responseEntity = response.getEntity();
			 reponseResult=EntityUtils.toString(responseEntity);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
		return  JSON.parseObject(reponseResult);
	}
	/** 发送http请求 @author zhanghuihui **/
	public static JSONObject callHttpRemoteInterfacePost(String url,String clientId,String secretId, JSONObject jsonData) {
		CloseableHttpClient client = null;
		
		try {
			client = new SSLClient();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		String reponseResult="";
		HttpPost post=new HttpPost(url);
		 if(clientId!=null && secretId!=null){
			 post.addHeader("Client-Id", clientId);
			 post.addHeader("Client-Secret",secretId);
		 }
		
		StringEntity entity = new StringEntity(jsonData.toString(),"utf-8");//解决中文乱码问题    
        entity.setContentEncoding("UTF-8");    
        entity.setContentType("application/json");    
        post.setEntity(entity); 
		try {
			 CloseableHttpResponse response = client.execute(post);
			 HttpEntity responseEntity = response.getEntity();
			 reponseResult=EntityUtils.toString(responseEntity);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
		return  JSON.parseObject(reponseResult);
	}
	
	/** 发送http请求 form提交 @author zhanghuihui **/
	public static JSONObject callHttpRemoteInterfacePostForm(String url,String clientId,String secretId, JSONObject jsonData) {
		CloseableHttpClient client = null;
		@SuppressWarnings("unchecked")
		Map<String,Object> param = JSONObject.toJavaObject(jsonData, Map.class);
		try {
			client = new SSLClient();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		String reponseResult="";
		HttpPost post=new HttpPost(url);
		if(clientId!=null && secretId!=null){
			post.addHeader("Client-Id", clientId);
			post.addHeader("Client-Secret",secretId);
		}
		
		//装填参数
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        if(param!=null){
            for (Entry<String, Object> entry : param.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
            }
        }
        log.info("callHttpRemoteInterfacePostForm params : " + params);
        post.setHeader("Content-type", "application/x-www-form-urlencoded");
        
		try {
			//设置参数到请求对象中
	        post.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
	        CloseableHttpResponse response = client.execute(post);
	        HttpEntity responseEntity = response.getEntity();
	        reponseResult=EntityUtils.toString(responseEntity);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
		return  JSON.parseObject(reponseResult);
	}
}
