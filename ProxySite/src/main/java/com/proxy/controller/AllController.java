package com.proxy.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

import com.proxy.utils.StringUtil;



@Controller

public class AllController {
	
	@RequestMapping(value="/**/*")
	
	public ResponseEntity<byte[]> handle(HttpServletRequest request){
		
		String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		
		if(request.getMethod().equals("GET"))
		{
			Map<String, String[]> paramMap=request.getParameterMap();
			String remoteUrl = StringUtil.makeRemoteUrl("https://www.daoway.cn"+path, paramMap);
			
			System.out.println(remoteUrl);
			
			return remoteGet(request, remoteUrl);
		}
		else
		{
			String remoteUrl="https://www.daoway.cn"+path;
			return remotePost(request, remoteUrl);
		}
	            		
	}
	
	@RequestMapping(value="/*")
	
	public ResponseEntity<byte[]> rootHandle(HttpServletRequest request){
		
		String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		
		if(request.getMethod().equals("GET"))
		{
			Map<String, String[]> paramMap=request.getParameterMap();
			String remoteUrl = StringUtil.makeRemoteUrl("https://www.daoway.cn"+path, paramMap);
			
			System.out.println(remoteUrl);
			
			return remoteGet(request, remoteUrl);
		}
		else
		{
			String remoteUrl="https://www.daoway.cn"+path;
			return remotePost(request, remoteUrl);
		}
	            		
	}
	

//	@RequestMapping(value="/css/{extension:.*}", produces="text/css; charset=utf-8")
//	@ResponseBody
//	public String cssHandle(HttpServletRequest request, @PathVariable String extension){
//
//		Map<String, String[]> paramMap=request.getParameterMap();
//		String remoteUrl = StringUtil.makeRemoteUrl("http://hs.blizzard.cn/css/"+extension, paramMap);
//		
//		System.out.println(remoteUrl);
//		return remoteGet(request, remoteUrl);	
//	
//	}
	
	
	
	private ResponseEntity<byte[]> remoteGet(HttpServletRequest request, String remoteUrl)
	{
		Enumeration<String> headerNames = request.getHeaderNames();
		
		HttpUriRequest remoteRequest = RequestBuilder.get().setUri(remoteUrl).build();
		
		while(headerNames.hasMoreElements())
		{
			String name=headerNames.nextElement();
			String value=request.getHeader(name);
				
			System.out.println(name+"->"+value);
			if(StringUtil.captalize(name).equals("Host"))
			{
			}
			else
				remoteRequest.setHeader(StringUtil.captalize(name), value);
		}
		
		System.out.println("-----------------------------");
		
		Header[] headers = remoteRequest.getAllHeaders();
		for(Header h: headers)
		{
			System.out.println(h.getName()+":"+h.getValue());
		}

		// Create a custom response handler
		ResponseHandler<ResponseEntity<byte[]>> responseHandler = response -> {
			int status = response.getStatusLine().getStatusCode();
			
			HttpHeaders hs=new HttpHeaders();
			
			Header[] resHeaders = response.getAllHeaders();
			for(Header h: resHeaders)
			{
				System.out.println(h.getName()+":"+h.getValue());
				
				if(h.getName().equals("Transfer-Encoding"))
					continue;
				
				hs.add(h.getName(), h.getValue());
			}			
			
			if (status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity();
								
				byte[] responseBody;
				if(entity==null)
					responseBody=null;
				else
					responseBody=EntityUtils.toByteArray(entity);
							
				return new ResponseEntity<byte[]>(responseBody, hs, HttpStatus.valueOf(status));
			} else {
								
				HttpEntity entity = response.getEntity();
				System.out.println(entity);
			
				return new ResponseEntity<byte[]>(null, hs, HttpStatus.valueOf(status));
			}
		};

		CloseableHttpClient httpClient = HttpClients.createDefault();
		ResponseEntity<byte[]> responseEntity;
		try {
			responseEntity = httpClient.execute(remoteRequest, responseHandler);
						
			return responseEntity;	
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return null;
	}
	
	private ResponseEntity<byte[]> remotePost(HttpServletRequest request, String remoteUrl)
	{
		// create http post request
		
		//装配post请求参数
        List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>(); 
        
        for(Entry<String, String[]> entry: request.getParameterMap().entrySet())
		{
        	String name=entry.getKey();
        	String value="";
        	if(entry.getValue().length>=1)
			{
				value=entry.getValue()[0];
			}
        	
        	System.out.println(name+":"+value);
        	
        	list.add(new BasicNameValuePair(name, value));  //请求参数			
		}
        
        UrlEncodedFormEntity postEntity=null;
		try {
			postEntity = new UrlEncodedFormEntity(list,"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		
		HttpUriRequest remoteRequest = RequestBuilder.post().setUri(remoteUrl).setEntity(postEntity).build();
		
		// set remote headers
		Enumeration<String> headerNames = request.getHeaderNames();
		
		while(headerNames.hasMoreElements())
		{
			String name=headerNames.nextElement();
			String value=request.getHeader(name);
				
			System.out.println(name+"->"+value);
			if(name.equals("host") || name.equals("content-length"))
			{
			}
			else
				remoteRequest.setHeader(name, value);
		}
		
		System.out.println("-----------------------------");
		
		Header[] headers = remoteRequest.getAllHeaders();
		for(Header h: headers)
		{
			System.out.println(h.getName()+":"+h.getValue());
		}
		
		// Create a custom response handler
		ResponseHandler<ResponseEntity<byte[]>> responseHandler = response -> {
			int status = response.getStatusLine().getStatusCode();
			
			HttpHeaders hs=new HttpHeaders();
			
			Header[] resHeaders = response.getAllHeaders();
			for(Header h: resHeaders)
			{
				System.out.println(h.getName()+":"+h.getValue());
				
				if(h.getName().equals("Transfer-Encoding"))
					continue;
				
				hs.add(h.getName(), h.getValue());
			}			
			
			if (status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity();
								
				byte[] responseBody;
				if(entity==null)
					responseBody=null;
				else
					responseBody=EntityUtils.toByteArray(entity);
							
				return new ResponseEntity<byte[]>(responseBody, hs, HttpStatus.valueOf(status));
			} else {
								
				HttpEntity entity = response.getEntity();
				System.out.println(entity);
			
				return new ResponseEntity<byte[]>(null, hs, HttpStatus.valueOf(status));
			}
		};
		

		CloseableHttpClient httpClient = HttpClients.createDefault();
		ResponseEntity<byte[]> responseEntity;
		try {
			responseEntity = httpClient.execute(remoteRequest, responseHandler);
						
			return responseEntity;	
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return null;
	}
}








