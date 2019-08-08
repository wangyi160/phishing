package com.proxy.utils;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;


public class StringUtil {

	public static String captalize(String name)
	{
		if(name==null)
			return name;
		
		if(name.length()==0)
			return name;
		
		StringBuffer sb=new StringBuffer();
		
		boolean shouldCap=true;
		for(int i=0;i<name.length();i++)
		{
			String c=name.substring(i,i+1);
			if(shouldCap)
			{
				sb.append(c.toUpperCase());
				shouldCap=false;
			}
			else
			{
				sb.append(c);
			}
			
			if(c.equals("-"))
				shouldCap=true;
		}
		
		return sb.toString();
	}
	
	public static String makeRemoteUrl(String url, Map<String, String[]> paramMap)
	{
		StringBuffer urlBuffer=new StringBuffer();
		
		for(Entry<String, String[]> entry: paramMap.entrySet())
		{
						
			urlBuffer.append(entry.getKey()+"=");
			if(entry.getValue().length>=1)
			{
				String value=entry.getValue()[0].replace("+", "%2B");
				value=value.replace(" ", "%20");
				urlBuffer.append(value);
			}
			
			urlBuffer.append("&");
		}
		
		if(urlBuffer.length()==0)
			return url;
		
		System.out.println(urlBuffer.toString());
		
		return url+"?"+urlBuffer.toString();
	}
	
	public static Charset detectCharsetImpl(byte[] buffer) throws Exception
	{
	    CharsetDetector detector = new CharsetDetector();
	    detector.setText(buffer);
	    CharsetMatch match = detector.detect();

	    if(match != null) // && match.getConfidence() > threshold)
	    {
	        System.out.println(match.getConfidence());
	    	
	    	try
	        {
	            return Charset.forName(match.getName());
	        }
	        catch(UnsupportedCharsetException e)
	        {
	            System.err.println("Charset detected as " + match.getName() + " but the JVM does not support this, detection skipped");
	        }
	    }
	    
	    return null;
	}
	
}









