package com.mapmory.services.user.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mapmory.common.domain.Page;
import com.mapmory.common.domain.Search;
import com.mapmory.services.user.domain.User;

@Mapper
public interface UserDao {

	public int insertUser(User user);
	
	public User selectUser(String userId);
	
	// 0: updateUserInfo, 1: updateProfile :: 悼利 query 积己捞 格钎
	public int updateUser(User user, int type);
	
	public List<User> selectUserList(Search search);
	
	public int getUserListTotalCount(Search search);
	
	public int updateLeaveAccount(String userId);
	
	public int updateRecoverAccount(String userId);
	
	public String selectId(String userName, String email);
}
