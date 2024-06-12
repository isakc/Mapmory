package com.mapmory.services.user.dao;

import org.apache.ibatis.annotations.Mapper;

import com.mapmory.services.user.domain.User;

@Mapper
public interface UserDaoJM {
	
	public User findByUserId(String userId);

}
