package com.mapmory.services.user.service.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.mapmory.common.domain.Search;
import com.mapmory.common.util.ContentFilterUtil;
import com.mapmory.common.util.ImageFileUtil;
import com.mapmory.common.util.ObjectStorageUtil;
import com.mapmory.exception.user.MaxCapacityExceededException;
import com.mapmory.services.user.dao.UserDao;
import com.mapmory.services.user.domain.FollowBlock;
import com.mapmory.services.user.domain.FollowMap;
import com.mapmory.services.user.domain.LoginDailyLog;
import com.mapmory.services.user.domain.LoginLog;
import com.mapmory.services.user.domain.LoginSearch;
import com.mapmory.services.user.domain.SocialLoginInfo;
import com.mapmory.services.user.domain.SuspensionDetail;
import com.mapmory.services.user.domain.SuspensionLog;
import com.mapmory.services.user.domain.SuspensionLogList;
import com.mapmory.services.user.domain.TermsAndConditions;
import com.mapmory.services.user.domain.User;
import com.mapmory.services.user.service.UserService;

import kr.co.shineware.nlp.komoran.exception.FileFormatException;

@Service("userServiceImpl")
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	@Qualifier("userDao")
	private UserDao userDao;

	@Value("${directory.path.tac}")
	private String tacDirectoryPath;
	
	@Value("${object.profile.folderName}")
	private String profileFolderName;
	
	
	@Autowired
	private ObjectStorageUtil objectStorageUtil;
	
	@Autowired
	private ContentFilterUtil contentFilterUtil;
	
	private Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder();
	
	@Override
	public boolean addUser(String userId, String userPassword, String userName, String nickname, LocalDate birthday, int sex, String email, String phoneNumber) throws Exception {
		// TODO Auto-generated method stub
		
		if ( contentFilterUtil.checkBadWord(userId) ) 
			throw new Exception("아이디에 비속어가 포함되어 있습니다.");
		
		if ( contentFilterUtil.checkBadWord(nickname) ) 
			throw new Exception("닉네임에 비속어가 포함되어 있습니다.");
			
		userPassword = argon2PasswordEncoder.encode(userPassword);
		System.out.println("암호화된 비밀번호 : " + userPassword);
		
		User user = User.builder()
						.userId(userId)
						.userPassword(userPassword)
						.userName(userName)
						.nickname(nickname)
						.birthday(birthday)
						.sex(sex)
						.email(email)
						.phoneNumber(phoneNumber)
						.build();
	
		int result = userDao.insertUser(user);
		
		return intToBool(result);
	}

	@Override
	public boolean addSuspendUser(String userId, String reason) throws Exception {
		// TODO Auto-generated method stub
		
		int searchCondition = 1;
		
		Search search = Search.builder()
							.userId(userId)
							.searchCondition(searchCondition)
							.build();
		
		// @SuppressWarnings("rawtypes")
		// int count = ((List) userDao.selectSuspensionList(search).get("suspensionDetailList")).size();
		// int count = userDao.selectSuspensionList(search).get(0).getSuspensionDetailList().size();
		SuspensionLogList tempResult = getSuspensionLogListActually(userId);
		int count = 0;
		if(tempResult == null)
			count = 0;
		else
			count = tempResult.getSuspensionDetailList().size();
			
		LocalDateTime startSuspensionDate = LocalDateTime.now(); 
		switch(count) {
		
			case 0:
				startSuspensionDate.plusDays(1);
				break;
			case 1:
				startSuspensionDate.plusDays(7);
				break;
			case 2:
				startSuspensionDate.plusDays(14);
				break;
			case 3:
				startSuspensionDate.plusYears(9999L);
				break;
			default :
				throw new MaxCapacityExceededException("현재 해당 사용자의 정지 횟수가 정책 최대 개수보다 더 많이 존재합니다.");
		}
		
		SuspensionDetail detail = new SuspensionDetail(startSuspensionDate, reason);
		
		SuspensionLog log = SuspensionLog.builder()
							.userId(userId)
							.suspensionDetail(detail)
							.build();
		
		int result = userDao.insertSuspendLog(log);
		
		return intToBool(result);
	}

	@Override
	public boolean addSocialLoginLink(String userId, String socialId) {
		// TODO Auto-generated method stub
		
		/* 예외 처리 사항
		 * 1. 동일한 업체의 소셜 계정을 overriding하는 경우 -> 기존 것을 제거 후 새 것으로 교체
		 */
		
		int socialIdLength = socialId.length();
		
		Integer type = null;
		if(socialIdLength == 10)
			type = 2;
		else if (socialIdLength == 21)
			type = 0;
		else
			type = 1;
		
		SocialLoginInfo socialLoginInfo = SocialLoginInfo.builder()
								.userId(userId)
								.socialLoginInfoType(type)
								.socialId(socialId)
								.build();
		
		int result = userDao.insertSocialLoginLink(socialLoginInfo);

		return intToBool(result);
	}
	
	@Override
	public boolean addLeaveAccount(String userId) {
		// TODO Auto-generated method stub
		
		User user = User.builder()
					.userId(userId)
					.leaveAccountDate(LocalDateTime.now())
					.build();
		
		int result = userDao.updateUser(user);
		
		return intToBool(result);
	}
	
	@Override
	public boolean addLoginLog(String userId) {
		// TODO Auto-generated method stub
		
		
		
		return false;
	}
	
	@Override
	public User getDetailUser(String userId) {
		// TODO Auto-generated method stub
		User user = User.builder()
					.userId(userId)
					.build();
		
		return userDao.selectUser(user);
	}
	
	
	
	@Override
	public String getId(String userName, String email) {
		// TODO Auto-generated method stub
		
		User user = User.builder()
					.userName(userName)
					.email(email)
					.build();
		User resultUser =  userDao.selectUser(user);
		return resultUser.getUserId();
	}

	@Deprecated
	@Override
	public String getSocialId(SocialLoginInfo socialLoginInfo) {
		// TODO Auto-generated method stub
		
		List<SocialLoginInfo> resultList = userDao.selectSocialIdList(socialLoginInfo.getUserId());
		
		for(SocialLoginInfo info : resultList) {
			
			if(info.getSocialLoginInfoType() == socialLoginInfo.getSocialLoginInfoType())
				return info.getSocialId();
		}
		
		return null;
	}

	@Override
	public Map<String, Object> getUserList(Search search) {
		// TODO Auto-generated method stub
		
		List<User> userList = userDao.selectUserList(search);
		int count = userDao.getUserListTotalCount(search);
		
		Map<String, Object> result = new HashMap<>();
		result.put("userList", userList);
		result.put("count", count);
		
		return result;
	}

	@Override
	public List<FollowMap> getFollowList(String userId, String searchKeyword, int currentPage, int limit) {
		// TODO Auto-generated method stub
		
		Search search = Search.builder()
						.userId(userId)
						.searchKeyword(searchKeyword)
						.currentPage(currentPage)
						.limit(limit)
						.build();
		
		return userDao.selectFollowList(search);
	}

	

	@Override
	public List<SuspensionLogList> getSuspensionLogList(String userId, Integer currentPage, Integer limit) {
		// TODO Auto-generated method stub
		
		Search search = Search.builder()
						.userId(userId)
						.searchCondition(0)
						.limit(limit)
						.currentPage(currentPage)
						.build();
		
		return userDao.selectSuspensionList(search);
	}
	
	@Override
	public SuspensionLogList getSuspensionLogListActually(String userId) {
		// TODO Auto-generated method stub
		
		Search search = Search.builder()
						.userId(userId)
						.searchCondition(1)
						.build();
		List<SuspensionLogList> result = userDao.selectSuspensionList(search);

		if(result.size() == 0)
			return null;
		else
			return result.get(0);
	}
	
	
	@Override
	public List<TermsAndConditions> getTermsAndConditionsList() throws Exception {
		// TODO Auto-generated method stub

		List<TermsAndConditions> result = new ArrayList<>();
		
		try (Stream<Path> paths = Files.walk(Paths.get(tacDirectoryPath))){
			
			List<Path> files = paths.filter(Files::isRegularFile).collect(Collectors.toList());
			
			for (Path filePath : files) {
				
				TermsAndConditions temp = getDetailTermsAndConditions(filePath.toString());
				
				if(temp == null)
					throw new NullPointerException("TermsAndConditions 객체를 받지 못했습니다...");
				
				result.add(temp);
			}
			
			return result;
		} catch(Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public TermsAndConditions getDetailTermsAndConditions(String fileName) throws Exception {
		// TODO Auto-generated method stub

		String filePath = tacDirectoryPath + "/" + fileName;
		
		try {
		
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			
			if(br.lines() == null) 
				throw new FileNotFoundException("file을 찾지 못했습니다...");
			
			String title = null;
			Boolean required = null;
			StringBuilder contents = new StringBuilder();
			
			String nextLine = null;
			
			while( (nextLine = br.readLine()) != null  ) {
	
				if(nextLine.isEmpty())
					continue;
				
				if(title == null) {
					
					title = nextLine.trim();
					continue;
					
				} else if(required == null) {
					
					if(nextLine.trim().equalsIgnoreCase("필수"))
						required = true;
					else if(nextLine.trim().equalsIgnoreCase("선택"))
						required = false;
					else
						throw new FileFormatException("'필수' 또는 '선택'만 기입해주세요.");
					
					continue;
					
				} else {
					contents.append(nextLine).append("\n");
				}
			}

			return new TermsAndConditions(title, required, contents);
			
		} catch(Exception e) {
			throw new Exception();
		}
	}
	
	@Override
	public List<LoginDailyLog> getUserLoginDailyList(LoginSearch search) {
		// TODO Auto-generated method stub
		
		return userDao.selectUserLoginDailyList(search);
	}
	
	@Override
	public List<LoginMonthlyLog> getUserLoginMonthlyList(LoginSearch search) {
		// TODO Auto-generated method stub
		
		return userDao.selectUserLoginMonthlyList(search);
	}
	
	@Override
	public boolean updateUserInfo(String userId, String userName, String nickname, LocalDate birthday, Integer sex, String email, String phoneNumber) throws Exception {
		// TODO Auto-generated method stub
		
		if ( contentFilterUtil.checkBadWord(nickname) ) 
			throw new Exception("닉네임에 비속어가 포함되어 있습니다.");
		
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
		
		return intToBool(result);
	}

	@Override
	public boolean updateProfile(MultipartFile file, String userId, String profileImageName, String introduction) throws Exception {
		// TODO Auto-generated method stub
		
		if( contentFilterUtil.checkBadImage(file) )
			throw new Exception("해당 사진은 유해성 정책에 위반되는 사진입니다.");
		
		if( contentFilterUtil.checkBadWord(introduction))
			throw new Exception("자기소개란에 욕설이 포함될 수 없습니다.");
		
		User user = getDetailUser(userId);
		
		// 기존애 사용하던 커스텀 프로필이 있는 경우, object storage에서 해당 내용을 제거한 후 진행
		System.out.println("profileName : " + user.getProfileImageName());
		if( !user.getProfileImageName().equals("default_image.jpg")) {
			
			objectStorageUtil.deleteFile(user.getProfileImageName(), profileFolderName);
		}
		
		String changedProfileImageName = ImageFileUtil.getProductImageUUIDFileName(profileImageName);
		System.out.println("changedProfileName : " + changedProfileImageName );
		objectStorageUtil.uploadFileToS3(file, changedProfileImageName, profileFolderName);
		
		
		User tempUser = User.builder()
						.userId(userId)
						.profileImageName(changedProfileImageName)
						.introduction(introduction)
						.build();
		
		int result = userDao.updateUser(tempUser);
		
		return intToBool(result);
	}

	@Override
	public boolean updatePassword(String userId, String userPassword) {
		// TODO Auto-generated method stub
		
		User user = User.builder()
						.userId(userId)
						.userPassword(userPassword)
						.build();
		
		int result = userDao.updateUser(user);
		
		return intToBool(result);
	}

	@Override
	public int updateRecoverAccount(String userId) {
		// TODO Auto-generated method stub
		
		User user = User.builder()
					.userId(userId)
					.build();
		
		LocalDate leaveAccountDate = userDao.selectUser(user).getLeaveAccountDate().toLocalDate();
		
		// 변경 일자가 오늘로부터 1개월이 지난 경우면 진행 불가능.
		if( leaveAccountDate.isBefore( LocalDate.now().minusMonths(1) ) ) {
			return 2;
		} else {
			int result = userDao.updateRecoverAccount(userId);
			
			if (result == 1)
				return 1;
			else
				return 0;
		}
	}

	@Override
	public boolean updateHideProfile(String userId) {
		// TODO Auto-generated method stub
		
		int result = userDao.updateHideProfile(userId);
		return intToBool(result);
	}
	

	@Override
	public boolean updateSecondaryAuth(String userId) {
		// TODO Auto-generated method stub
		
		int result = userDao.updateSecondaryAuth(userId);
		return intToBool(result);
	}	

	@Override
	public boolean deleteFollow(String userId, String targetId) {
		// TODO Auto-generated method stub
		
		FollowBlock follow = FollowBlock.builder()
						.userId(userId)
						.targetId(targetId)
						.build();
		
		int result = userDao.deleteFollow(follow);
		
		return intToBool(result);
	}
	

	@Override
	public boolean deleteSuspendUser(int logNo) {
		// TODO Auto-generated method stub
		
		int result = userDao.deleteSuspendUser(logNo);
		return intToBool(result);
	}
	
	@Override
	public boolean checkSecondaryAuth(String userId) {
		// TODO Auto-generated method stub
		
		User user = User.builder()
						.userId(userId)
						.build();
						
		Byte result = userDao.selectUser(user).getSetSecondaryAuth();
		
		return intToBool(result);
	}

	@Override
	public boolean checkDuplicationById(String userId) {
		// TODO Auto-generated method stub
		
		User user = User.builder()
						.userId(userId)
						.build();
		
		int result = userDao.checkDuplication(user);
		
		return intToBool(result);
	}
	
	@Override
	public boolean checkDuplicationByNickname(String nickname) {
		// TODO Auto-generated method stub
		
		User user = User.builder()
						.nickname(nickname)
						.build();
		
		int result = userDao.checkDuplication(user);
		
		return intToBool(result);
	}
	
	@Override
	public boolean checkSocialId(String userId, String socialId) {
		// TODO Auto-generated method stub
		
		List<SocialLoginInfo> resultList = userDao.selectSocialIdList(userId);
		
		for (SocialLoginInfo info : resultList) {
			
			if(info.getSocialId().equals(socialId))
				return true;
		}
		return false;
	}
	
	@Override
	public boolean checkPasswordChangeDeadlineExceeded(String userId) {
		// TODO Auto-generated method stub
		
		User user = User.builder()
						.userId(userId)
						.build();
		
		LocalDate updatedPasswordDate = userDao.selectUser(user).getUpdatePasswordDate().toLocalDate();

		if( updatedPasswordDate.isBefore( LocalDate.now().minusMonths(3) ) ) {
			return false;
		} else {
			return true;
		}
	}	

	@Override
	public boolean checkHideProfile(String userId) {
		// TODO Auto-generated method stub
		
		User user = User.builder()
						.userId(userId)
						.build();
		
		Byte result = userDao.selectUser(user).getHideProfile();
		
		return intToBool(result);
	}
	


	private boolean intToBool(int result) {
		
		if(result == 1)
			return true;
		else
			return false;
	}
}
