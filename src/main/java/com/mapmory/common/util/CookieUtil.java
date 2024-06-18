package com.mapmory.common.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

	
	public static Cookie findCookie(String cookieKeyName, HttpServletRequest request) {

    	Cookie[] cookies = request.getCookies();
    	if(cookies == null)
    		return null;
    	
    	for(Cookie cookie : cookies) {
    		
    		if(cookie.getName().equals(cookieKeyName)) {
    			
    			return cookie;
    			
    		}	
    	}
    	
    	return null;
    }
	
    public static Cookie createCookie(String codeKeyName, String codeKey, int maxAge, String path) {
		
		Cookie cookie = new Cookie(codeKeyName, codeKey);
		cookie.setPath(path);
		// cookie.setDomain("mapmory.co.kr");
		// cookie.setSecure(true);
		cookie.setHttpOnly(false);
		cookie.setMaxAge(maxAge);
		
		return cookie;
	}
}
