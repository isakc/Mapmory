package com.mapmory.services.user.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mapmory.common.domain.Search;
import com.mapmory.services.user.domain.User;

@Mapper
public interface UserDao {

	public int insertUser(User user);
	
	public User selectUser(User user);
	
	public List<User> selectUserList(Search search);
	
	public int getUserListTotalCount(Search search);
	
	// 0: updateUserInfo, 1: updateProfile :: 동적 query로 처리할 예정
	public int updateUser(User user);
	
	public int updateRecoverAccount(String userId);
	
	
}
