package com.mapmory.services.user.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mapmory.services.call.domain.UserJM;

@Mapper
public interface UserDaoJM {
	
	public UserJM findByUserId(String userId);
    public void updateOnlineStatus(@Param("userId") String userId, @Param("isOnline") boolean isOnline);

}
