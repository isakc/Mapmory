package com.mapmory.common.util;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.mapmory.common.domain.SessionData;

/**
 * Redis에 대한 CRUD를 이 utility class로 쉽게 처리하세요!
 * @author 김대민
 *
 * @param <T>  :: 본인이 CRUD하고자 할 객체 type을 넣어주시면 됩니다. 오직 session만 발급받으실 분이라면 SessionData를 넣고 쓰시면 됩니다.
 */
@Component
public class RedisUtil<T> {

	@Autowired
	RedisTemplate<String, T> redisTemplate;
	
	private final static long SESSION_EXPIRE_TIME = 30L;
	
	public RedisUtil() {
		System.out.println("redisUtil"); 
		
	}
	
	/**
	 * redis에게 데이터를 넣는다. (3O분만 유지됨)
	 * @param keyName  : 지정할 key 이름
	 * @param t  : 주입할 domain 객체
	 * @return  : transaction 등에서 문제가 발생하여 값을 넣지 못하면 false, 성공하면 true
	 */
	@Deprecated
	public boolean insert(String keyName, T t) {

		redisTemplate.opsForValue().set(keyName, t, SESSION_EXPIRE_TIME, TimeUnit.MINUTES);
		// boolean successed = redisTemplate.expire(keyName, 10, TimeUnit.SECONDS);
		/*
		boolean successed = redisTemplate.expire(keyName, 1, TimeUnit.SECONDS);
		
		if( !successed) {
			System.out.println("Transaction 오류");
			return false;
		} else
			return true;
			*/
		return true;
	}
	
	/**
	 * redis에게 데이터를 넣는다.
	 * @param keyName  : 지정할 key 이름
	 * @param t  : 주입할 domain 객체
	 * @param expireTime  : 만료 시간(분)
	 * @return  : transaction 등에서 문제가 발생하여 값을 넣지 못하면 false, 성공하면 true
	 */
	public boolean insert(String keyName, T t, long expireTime) {

		redisTemplate.opsForValue().set(keyName, t);
		boolean successed = redisTemplate.expire(keyName, expireTime, TimeUnit.MINUTES);
		
		if( !successed) {
			System.out.println("Transaction 오류"); 
			return false;
		} else
			return true;
	}
	
	/**
	 * redis로부터 값을 가져온다.
	 * @param keyName  : 지정할 key 이름
	 * @param clazz  : auto binding을 원하는 domain class type
	 * @return  : key에 해당하는 value를 가져온다.
	 */
	public T select(String keyName, Class<T> clazz) {
		
		return redisTemplate.opsForValue().get(keyName);
	}
	
	/**
	 * redis 내에 저장된 data를 수정한다.
	 * @param <T>
	 * @param keyName  : 지정할 key 이름
	 * @param t  : 변경할 domain 객체
	 * @return  : transaction 등에서 문제가 발생하여 값을 넣지 못하면 false, 성공하면 true
	 */
	public boolean update(String keyName, T t) {
		
		return insert(keyName, t, SESSION_EXPIRE_TIME);
	}
	
	/**
	 * redis 내에 저장된 data를 수정한다.
	 * @param keyName  : 지정할 key 이름
	 * @param t  : 변경할 domain 객체
	 * @param expireTime  : 변경할 만료 시간(분)
	 * @return  : transaction 등에서 문제가 발생하여 값을 넣지 못하면 false, 성공하면 true
	 */
	public boolean update(String keyName, T t, long expireTime) {
		
		return insert(keyName, t, expireTime);
	}
	
	/**
	 * redis 내에 저장된 data를 제거한다.
	 * @param keyName  : 지정할 key 이름
	 */
	public void delete(String keyName) {
		
		redisTemplate.delete(keyName);
	}
	
	/**
	 * HttpSession의 getSession() 기능과 동일한 역할을 합니다!
	 * @param request
	 * @return
	 */
	public SessionData getSession(HttpServletRequest request) {
		
		String sessionId = null;
		
		Cookie[] cookies = request.getCookies();
		if(cookies == null) {
			System.out.println("asdf");
		}
		for(Cookie cookie : cookies ) {
			
			if(cookie.getName().equals("JSESSIONID")) {
				
				sessionId = cookie.getValue();
				return (SessionData) redisTemplate.opsForValue().get(sessionId);
				
			}
		}
		
		return null;
	}
	
	
	public boolean updateSession(HttpServletRequest request, HttpServletResponse response) {

		Cookie cookie = CookieUtil.findCookie("JSESSIONID", request);
		if(cookie == null)
			return true;
		
		String key = cookie.getValue(); 
		SessionData sessionData = (SessionData) redisTemplate.opsForValue().get(key);
		
		boolean successed;
		if(sessionData.getIsKeepLogin() == 1) {
			cookie.setPath("/");
			cookie.setMaxAge(60*60*24*90);
			successed = redisTemplate.expire(key, 60*24*90, TimeUnit.MINUTES);
		} else {
			cookie.setPath("/");
			cookie.setMaxAge(60*30);
			successed = redisTemplate.expire(key, 30, TimeUnit.MINUTES);
		}
			
		response.addCookie(cookie);
		
		if( !successed) {
			System.out.println("Transaction 오류");
			return false;
		} else
			return true;
	}
	
	
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	//// utility ////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	

}
