package com.mapmory.unit.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mapmory.common.domain.SessionData;
import com.mapmory.common.util.RedisUtil;

@SpringBootTest
// @Transactional
public class RedisUtilTest {

	@Autowired
	private RedisUtil<SessionData> redisUtil;
	
	// @Test
	public void testInsert() {
		
		// redisUtil.insert("a", "1111");
		
		String key = "test";
		
		SessionData s = SessionData.builder()
				.userId("hong")
				.role(1)
				.build();
		
		redisUtil.insert(key, s);
		SessionData result = redisUtil.select(key, SessionData.class);
		System.out.println(result);
		Assertions.assertThat(result.getUserId()).isEqualTo("hong");
	}
	
	// @Test
	public void testDelete() {
		
		String key = "test";
		redisUtil.delete(key);
		
		SessionData result = redisUtil.select(key, SessionData.class);
		System.out.println(result);
		Assertions.assertThat(result).isNull();
	}
}
