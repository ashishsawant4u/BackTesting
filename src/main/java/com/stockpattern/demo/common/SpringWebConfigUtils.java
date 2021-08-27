package com.stockpattern.demo.common;

import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SpringWebConfigUtils 
{
	public static String STOCKNOTE_SESSION_KEY = "stocknoteSessionKey";
	public static String STOCKNOTE_SESSION_KEY_LAST_FETCH_TIME = "stocknoteSessionKeyLastFetchTime";
	
	public static HttpSession getHttpSession() 
	{
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = attr.getRequest().getSession();
		return session;
	}
}
