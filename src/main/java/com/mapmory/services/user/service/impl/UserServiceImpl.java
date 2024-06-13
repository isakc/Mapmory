package com.mapmory.services.user.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mapmory.common.domain.Search;
import com.mapmory.common.util.ContentFilterUtil;
import com.mapmory.common.util.ImageFileUtil;
import com.mapmory.common.util.ObjectStorageUtil;
import com.mapmory.exception.user.MaxCapacityExceededException;
import com.mapmory.services.user.dao.UserDao;
import com.mapmory.services.user.domain.FollowBlock;
import com.mapmory.services.user.domain.FollowMap;
import com.mapmory.services.user.domain.Login;
import com.mapmory.services.user.domain.LoginDailyLog;
import com.mapmory.services.user.domain.LoginMonthlyLog;
import com.mapmory.services.user.domain.LoginSearch;
import com.mapmory.services.user.domain.SocialLoginInfo;
import com.mapmory.services.user.domain.SuspensionDetail;
import com.mapmory.services.user.domain.SuspensionLog;
import com.mapmory.services.user.domain.SuspensionLogList;
import com.mapmory.services.user.domain.TermsAndConditions;
import com.mapmory.services.user.domain.User;
import com.mapmory.services.user.domain.UserSearch;
import com.mapmory.services.user.service.UserService;

import kr.co.shineware.nlp.komoran.exception.FileFormatException;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;

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
	
	@Autowired
	private PasswordEncoder passwordEncoder; 
	
	
	@Value("${coolsms.apikey}")
	private String coolsmsApiKey;
	
	@Value("${coolsms.apisecret}")
	private String coolsmsSecret;
	
	@Value("${coolsms.fromnumber}")
	private String phoneNum;
	
	@Value("${kakao.client}")
	private String kakaoCilent;
	
	////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////
	
	@Override
	public boolean addUser(String userId, String userPassword, String userName, String nickname, LocalDate birthday, int sex, String email, String phoneNumber) throws Exception {
		// TODO Auto-generated method stub
		
		if ( contentFilterUtil.checkBadWord(userId) ) 
			throw new Exception("아이디에 비속어가 포함되어 있습니다.");
		
		if ( contentFilterUtil.checkBadWord(nickname) ) 
			throw new Exception("닉네임에 비속어가 포함되어 있습니다.");
			
		userPassword = passwordEncoder.encode(userPassword);
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
	public int getFollowListTotalCount(String userId, String searchKeyword, int currentPage, int limit) {
		
		Search search = Search.builder()
				.userId(userId)
				.searchKeyword(searchKeyword)
				.currentPage(currentPage)
				.limit(limit)
				.build();
		
		return userDao.getFollowListTotalCount(search);
	}
	
	@Override
	public List<FollowMap> getFollowerList(String userId, String searchKeyword, int currentPage, int limit) {
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
	public int getFollowerListTotalCount(String userId, String searchKeyword, int currentPage, int limit) {
		
		Search search = Search.builder()
				.userId(userId)
				.searchKeyword(searchKeyword)
				.currentPage(currentPage)
				.limit(limit)
				.build();
		
		return userDao.getFollowListTotalCount(search);
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
				
				// System.out.println("filePath : " + filePath);
				TermsAndConditions temp = getDetailTermsAndConditions(filePath.toString());
				// System.out.println("tac : " + temp);
				
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
	public TermsAndConditions getDetailTermsAndConditions(String filePath) throws Exception {
		// TODO Auto-generated method stub

		// String filePath = tacDirectoryPath + "/" + fileName;
		
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
	public String getPassword(String userId) {
		
		return userDao.selectPassword(userId);
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
		
		String changedProfileImageName = ImageFileUtil.getImageUUIDFileName(profileImageName);
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
	public boolean updatePassword(String userId, String rawPassword) {
		// TODO Auto-generated method stub
		
		String hashedPassword = passwordEncoder.encode(rawPassword);
		System.out.println("hashedPassword : " + hashedPassword);
		
		Login login = Login.builder()
				.userId(userId)
				.userPassword(hashedPassword)
				.build();
		
		int result = userDao.updatePassword(login);
		
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
	
	@Override
	public boolean checkFollow(String myUserId, String targetUserId) {
		
		FollowBlock fb = FollowBlock.builder()
					.userId(myUserId)
					.targetId(targetUserId)
					.build();
		
		int result = userDao.isFollow(fb);
		return intToBool(result);
	}
	
	/**
	 * 오직 테스트 전용이다. 기존 test data의 비밀번호를 전부 암호화한다.
	 * 현재 user1과 user2만 갈아엎도록 설정되어 있다. 
	 * 만약 test data(최대 100개까지 지원) 모두를 갈아엎고 싶다면 userServiceImpl에 직접 찾아와서 주석 처리된 부분을 해제하고 기존 코드를 주석 처리하라.
	 */
	public void setupForTest() {

		/*
		UserSearch search = UserSearch.builder()
							.searchCondition(-1)
							.role(0)
							.currentPage(1)
							.pageSize(100)
							.limit(100)
							.build();
		List<User> list = userDao.selectUserList(search);
		
		for(User user : list) {
			
			String userId = user.getUserId();
			String userPassword = getPassword(userId);
			
			updatePassword(userId, userPassword);
		}
		*/
		
		String userId="user1";
		String userPassword="password1";
		updatePassword(userId, userPassword);
		userId="user2";
		userPassword="password2";
		updatePassword(userId, userPassword);
		userId="admin";
		userPassword="admin";
		updatePassword(userId, userPassword);
	}
	
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	//// jaemin ////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	
	@Override
    public String getKakaoAccessToken (String authorize_code) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //    POST 요청을 위해 기본값이 false인 setDoOutput을 true로

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //    POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id="+kakaoCilent );  //본인이 발급받은 key
            sb.append("&redirect_uri=http://localhost:8000/user/kakaoLogin&response_type=code");     // 본인이 설정해 놓은 경로
            sb.append("&code=" + authorize_code);
            System.out.println("authorize_code : " + authorize_code);
            bw.write(sb.toString());
            bw.flush();

            //    결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //    요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //    Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return access_Token;
    }
	
	@Override
    public String getKakaoUserInfo (String access_Token) throws Exception {

        //    요청하는 클라이언트마다 가진 정보가 다를 수 있기에 HashMap타입으로 선언
        HashMap<String, Object> kakaoInfo = new HashMap<String, Object>();
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        String kakaoId = null;
        
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

            kakaoId = element.getAsJsonObject().get("id").getAsString();
            System.out.println("kakaoId : " + kakaoId);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return kakaoId; // 예외 발생 시 null 반환
    }
	
	public String PhoneNumberCheck(String to) throws Exception {
		String smsProvider = "https://api.coolsms.co.kr";
		DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(coolsmsApiKey, coolsmsSecret, smsProvider);

		Random rand = new Random();
		String numStr = "";
		for(int i=0; i<4; i++) {
			String ran = Integer.toString(rand.nextInt(10));
			numStr += ran;
		}

		Message message = new Message();
		message.setFrom(phoneNum);    	// 발신전화번호. 테스트시에는 발신,수신 둘다 본인 번호로 하면 됨
		message.setTo(to);    				// 수신전화번호 (ajax로 view 화면에서 받아온 값으로 넘김)
		message.setText("인증번호는 : [" + numStr + "]");

		SingleMessageSendingRequest request = new SingleMessageSendingRequest(message);
		SingleMessageSentResponse response = messageService.sendOne(request); // 메시지 전송

		return numStr;
	}

	private boolean intToBool(int result) {
		
		if(result == 1)
			return true;
		else
			return false;
	}
}
