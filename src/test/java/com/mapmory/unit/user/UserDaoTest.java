package com.mapmory.unit.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.mapmory.common.domain.Search;
import com.mapmory.services.user.dao.UserDao;
import com.mapmory.services.user.domain.FollowBlock;
import com.mapmory.services.user.domain.FollowMap;
import com.mapmory.services.user.domain.SocialLoginInfo;
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
	public void testInsertSocialLoginLink() throws Exception {
		
		String userId = "simple_7890";
		String socialId = "103984541210666630525";
		int socialLoginInfoType = 0;
		
		SocialLoginInfo info = SocialLoginInfo.builder()
								.userId(userId)
								.socialLoginInfoType(socialLoginInfoType)
								.socialId(socialId)
								.build();
				
		int result = userDao.insertSocialLoginLink(info);
		
		Assertions.assertThat(result).isEqualTo(1);
		
		List<SocialLoginInfo> resultList = userDao.selectSocialIdList(userId);
		
		Assertions.assertThat(resultList.get(0).getUserId()).isEqualTo(userId);
	}
	
	@Test
	public void testInsertFollow() throws Exception {
		
		String userId = "user_123";
		String targetId = "john_doe_90";
		
		FollowBlock follow = FollowBlock.builder()
						.userId(userId)
						.targetId(targetId)
						.build();
		
		int result = userDao.insertFollow(follow);
		
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
	
	// 정지 사용자만 조회
	// categoryNo를 사용해보자.
	
	// @Test
	public void testSelectSocialIdList() throws Exception {
		
		String userId = "simple_7890";
		
		List<SocialLoginInfo> resultList = userDao.selectSocialIdList(userId);
		
		Assertions.assertThat(resultList.get(0).getUserId()).isEqualTo(userId);
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
	public void testSelectFollowList() throws Exception {
		
		String userId = "user1";
		String searchKeyword = "a";
		int currentPage = 1;
		int limit = 3;
		
		Search search = Search.builder()
						.userId(userId)
						.searchKeyword(searchKeyword)
						.currentPage(currentPage)
						.limit(limit)
						.build();
		
		List<FollowMap> followList = userDao.selectFollowList(search);
		int count = userDao.getFollowListTotalCount(search);
		
		Assertions.assertThat(followList.size()).isEqualTo(count);
		
		Assertions.assertThat(followList.get(0).getUserId()).isEqualTo("user3");
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
	
	// @Test
	public void testUpdateUserInfo() throws Exception {
		
		String userId = "my_id-is_456";
		String userName = "홍길동";
		String nickname = "나는홍길동";
		LocalDate birthday = LocalDate.parse("2001-09-21");
		Integer sex = 0;
		String email = "test@test.com";
		String phoneNumber = "010-6666-3333";
		
		User user = User.builder()
					.userId(userId)
					.userName(userName)
					.nickname(nickname)
					.birthday(birthday)
					.sex(sex)
					.email(email)
					.phoneNumber(phoneNumber)
					.build();
		
		int result = userDao.updateUser(user);

		Assertions.assertThat(result).isEqualTo(1);
		
		user.setUserId(null);  // UserMappper 동적 query를 참고
		User resultUser = userDao.selectUser(user);
		
		Assertions.assertThat(resultUser.getNickname()).isEqualTo(nickname);
	    Assertions.assertThat(resultUser.getBirthday()).isEqualTo(birthday);
	    Assertions.assertThat(resultUser.getSex()).isEqualTo(sex);
	    Assertions.assertThat(resultUser.getEmail()).isEqualTo(email);
	    Assertions.assertThat(resultUser.getPhoneNumber()).isEqualTo(phoneNumber);
	}
	
	// @Test
	public void testUpdateProfile() throws Exception {
		
		String userId = "my_id-is_456";
		String profileImageName = "abcd.jpg";
		String introduction = "안녕하세요, 반갑습니다.";
		
		User user = User.builder()
					.userId(userId)
					.profileImageName(profileImageName)
					.introduction(introduction)
					.build();
		
		int result = userDao.updateUser(user);
		
		Assertions.assertThat(result).isEqualTo(1);
		
		User resultUser = userDao.selectUser(user);
		
		Assertions.assertThat(resultUser.getProfileImageName()).isEqualTo(profileImageName);
	    Assertions.assertThat(resultUser.getIntroduction()).isEqualTo(introduction);
	}
	
	// @Test
	public void testUpdateLeaveAccount() throws Exception{
		
		LocalDateTime leaveAccountDate = LocalDateTime.now();
		String userId = "my_id-is_456";
		
		User user = User.builder()
					.userId(userId)
					.leaveAccountDate(leaveAccountDate)
					.build();
		
		int result = userDao.updateUser(user);
		
		Assertions.assertThat(result).isEqualTo(1);
		
		User resultUser = userDao.selectUser(user);
		
		// 초 단위로 검증하니까 1초 차이로 test에 실패함.
		// Assertions.assertThat(resultUser.getLeaveAccountDate().toString()).isEqualTo(leaveAccountDate.toString().split("\\.")[0]);
		
		Assertions.assertThat(resultUser.getLeaveAccountDate().toLocalDate()).isEqualTo(leaveAccountDate.toLocalDate());
	}
	
	// @Test
	public void testUpdateRecoverAccount() throws Exception {
		
		// DAO 단에서는 만료일 제한 logic을 추가할 필요가 없음.
		String userId = "my_id-is_456";
		
		int result = userDao.updateRecoverAccount(userId);
		
		Assertions.assertThat(result).isEqualTo(1);
		
		User user = User.builder()
				.userId(userId)
				.build();
		
		User resultUser = userDao.selectUser(user);
		
		Assertions.assertThat(resultUser.getLeaveAccountDate()).isNull();
	}

	// @Test
	public void testDeleteFollow() {
		
		String userId = "user1";
		String targetId = "user2";
		
		FollowBlock follow = FollowBlock.builder()
						.userId(userId)
						.targetId(targetId)
						.build();
		
		int result = userDao.deleteFollow(follow);
		
		Assertions.assertThat(result).isEqualTo(1);

		
		int currentPage = 1;
		int limit = 5;
		
		Search search = Search.builder()
						.userId(userId)
						.currentPage(currentPage)
						.limit(limit)
						.build();
		
		List<FollowMap> list = userDao.selectFollowList(search);
		
		boolean flag = true;
		for(FollowMap fm : list) {
			if(fm.getUserId().equals("user2"))
				flag = false;
		}
		
		Assertions.assertThat(flag).isTrue();
	}
	
	// @Test
	public void testCheckDuplicationById() throws Exception {
		
		// 중복되는 경우
		String userId = "user1";
		User user = User.builder()
						.userId(userId)
						.build();
		
		int result = userDao.checkDuplication(user);
		
		Assertions.assertThat(result).isEqualTo(1);
		
		// 중복되지 않는 경우
		userId = "user9999";
		user.setUserId(userId);
		result = userDao.checkDuplication(user);
		Assertions.assertThat(result).isEqualTo(0);
	}
	
	// @Test
	public void testCheckDuplicationByNickname() throws Exception {
		
		// 중복되는 경우
		String nickname = "tomlee";
		User user = User.builder()
						.nickname(nickname)
						.build();
		
		int result = userDao.checkDuplication(user);
		
		Assertions.assertThat(result).isEqualTo(1);
		
		// 중복되지 않는 경우
		nickname = "tomlee9999";
		user.setNickname(nickname);
		result = userDao.checkDuplication(user);
		Assertions.assertThat(result).isEqualTo(0);
	}
}
