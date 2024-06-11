package com.mapmory.common.util;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil<T> {

	@Autowired
	RedisTemplate<String, T> redisTemplate;
	
	private final static long EXPIRE_TIME = 30L;
	
	private final static String KEY_PATTERN = "users:";
	
	/**
	 * redis에게 데이터를 넣는다.
	 * @param <T>
	 * @param keyName  : 지정할 key 이름
	 * @param t  : 주입할 domain 객체
	 * @return  : transaction 등에서 문제가 발생하여 값을 넣지 못하면 false, 성공하면 true
	 */
	
	public boolean insert(String keyName, T t) {

		redisTemplate.opsForValue().set(keyName, t);
		boolean successed = redisTemplate.expire(keyName, EXPIRE_TIME, TimeUnit.MINUTES);
		
		if( !successed) {
			System.out.println("Transaction 오류");
			return false;
		} else
			return true;
	}
	
	/**
	 * redis에게 데이터를 넣는다.
	 * @param <T>
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
	 * @param <T>  : return type
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
		
		return insert(keyName, t, EXPIRE_TIME);
	}
	
	/**
	 * redis 내에 저장된 data를 수정한다.
	 * @param <T>
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
}
