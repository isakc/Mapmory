package com.mapmory.unit.user;

import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.mapmory.common.domain.Search;
import com.mapmory.services.user.dao.UserDao;
import com.mapmory.services.user.domain.User;


@SpringBootTest
@Transactional
public class UserDaoTest {

	@Autowired
	@Qualifier("userDao")
	private UserDao userDao;
	
	//@Test
	public void testInsertUser() throws Exception {
		
		User user = User.builder()
					.userId("hong")
					.userPassword("1234")
					.role((byte) 1)
					.userName("홍길동")
					.nickname("나는 홍길동")
					.birthday(LocalDate.parse("2010-02-22"))
					.email("test@test.com")
					.phoneNumber("010-1234-1234")
					.sex(1)
					.build();
		
		int result = userDao.insertUser(user);
		
		Assertions.assertThat(result).isEqualTo(1);
	}
	
	// @Test
	public void testSelectUserById() throws Exception {
		
		/* test data 생성
		 * INSERT INTO users (user_id, user_password, user_name, nickname, birthday, email, phone_number, sex) VALUES 
			('user_123', 'Password!1', 'Alice Kim', 'alice1', '1990-01-01', 'alice@example.com', '010-1234-5678', 2),
		 */
		
		User user = User.builder()
					.userId("user_123")
					.build();
		
		User userResult = userDao.selectUser(user);
		
		Assertions.assertThat(userResult.getUserName()).isEqualTo("Alice Kim");
	}
	
	//@Test
	public void testSelectUserListOnlyPage() throws Exception {
		
		Search search = Search.builder()
						.currentPage(2)
						.pageSize(5)
						.build();
		
		List<User> listUser = userDao.selectUserList(search);
		int count = userDao.getUserListTotalCount(search);
		
		Assertions.assertThat(count).isEqualTo(listUser.size());
	}
	
	// @Test
	public void testSelectUserListPageWithKeyword() throws Exception {
		
		/// userId 검색
		Search search = Search.builder()
						.currentPage(1)
						.pageSize(5)
						.searchCondition(0)  // 0: user_id, 1: nickname
						.searchKeyword("user")
						.build();
		
		List<User> listUser = userDao.selectUserList(search);
		int count = userDao.getUserListTotalCount(search);
		
		Assertions.assertThat(listUser.get(0).getUserId().contains("user"))
					.isEqualTo(true);
		
		Assertions.assertThat(listUser.size()).isEqualTo(count);
		
		
		/// nickname 검색
		search = Search.builder()
					.currentPage(1)
					.pageSize(5)
					.searchCondition(1)  // 0: user_id, 1: nickname
					.searchKeyword("alice")
					.build();
		
		listUser = userDao.selectUserList(search);
		count = userDao.getUserListTotalCount(search);
		
		System.out.println("listUser : " + listUser);
		
		Assertions.assertThat(listUser.get(0).getNickname().contains("alice"))
					.isEqualTo(true);
		
		Assertions.assertThat(listUser.size()).isEqualTo(count);
	}
	
	// @Test
	public void selectUserByNameAndEmail() throws Exception  {
		
		String userName = "Jane Park";
		String email = "jane.park@example.com";
		
		User user = User.builder()
					.userName(userName)
					.email(email)
					.build();
		
		User resultUser = userDao.selectUser(user);
		
		Assertions.assertThat(userName).isEqualTo(resultUser.getUserName());
		Assertions.assertThat(email).isEqualTo(resultUser.getEmail());
	}
	
	// @Test
	public void testUpdateUserPassword() throws Exception {
		
		String userId = "john_doe_90";
		String userPassword = "qwer1234!";
		
		User user = User.builder()
				.userId(userId)
				.userPassword(userPassword)
				.build();

		int result = userDao.updateUser(user);
		String updatedUserPassword = userDao.selectUser(user).getUserPassword();
		
		Assertions.assertThat(result).isEqualTo(1);
		Assertions.assertThat(updatedUserPassword).isEqualTo(userPassword);
	}
	
	public void testUpdateUserInfo() throws Exception {
		
		String userName = "홍길동";
		String nickname = "나는홍길동";
		LocalDate birthday = LocalDate.parse("2001-09-21");
		
		
	}
	
	public void testUpdateProfile() throws Exception {
		
		String profileImageName = "abcd.jpg";
		String introduction = "안녕하세요, 반갑습니다.";
		
		
	}
	
	public void testLeaveAccount() throws Exception{
		
		
	}
	
	public void testRecoverAccount() throws Exception {
		
	}

}
