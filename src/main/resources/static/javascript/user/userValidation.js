$(function() {
	
	console.log('onLoad');
	
	$('#userId').on('input', function() {
	  const userId = document.getElementById('userId').value;
	  const userIdRegex = /^[a-z0-9_-]{5,20}$/;
	
	  if ((userId.length < 5) || !userIdRegex.test(userId)) {
	    $('#userId').removeClass('is-valid').addClass('is-invalid');
	    $('#userIdMsg').removeClass('valid-feedback').addClass('invalid-feedback').text('아이디는 5~20자의 영문 소문자, 숫자와 특수기호(_),(-)만 사용 가능합니다.');
	    $('#userIdChecked').text('false');
	    event.preventDefault();
	    return;
	  } else {
	    let result;
	    $.ajax({
	      url : "/user/rest/checkBadWord",
	      method : "post",
	      dataType : "json",
	      headers : {
	        "accept" : "application/json",
	        "content-type" : "application/json"
	      },
	      data : JSON.stringify({
	        value : $('#userId').val()
	      }),
	      async : false,
	      success : function(responseBody, httpStatus) {
	        result = responseBody;
	      },
	      error : function(jqXHR, textStatus, errorThrown) {
	        alert('오류 발생 : ' + jqXHR.responseText);
	        return;
	      }
	    });
	
	    console.log("비속어 통과?:" + result);
	    if(!result) {
	      $('#userId').removeClass('is-valid').addClass('is-invalid');
	      $('#userIdMsg').removeClass('valid-feedback').addClass('invalid-feedback').text('욕설은 사용할 수 없습니다.');
	      return;
	    }
	
	    $.ajax({
	      url : "/user/rest/checkDuplication",
	      method : "post",
	      dataType : "json",
	      headers : {
	        "accept" : "application/json",
	        "content-type" : "application/json"
	      },
	      data : JSON.stringify({
	        type : 0,
	        value : $('#userId').val()
	      }),
	      async : false,
	      success : function(responseBody, httpStatus) {
	        result = responseBody;
	      },
	      error : function(jqXHR, textStatus, errorThrown) {
	        alert('오류 발생 : ' + jqXHR.responseText);
	      }
	    });
	
	    console.log("중복 통과?:" + result);
	    if (result === true) {
	      $('#userId').removeClass('is-invalid').addClass('is-valid');
	      $('#userIdMsg').removeClass('invalid-feedback').addClass('valid-feedback').text('사용 가능한 아이디입니다.');
	      $('#userIdChecked').text('true');
	    } else {
	      $('#userId').removeClass('is-valid').addClass('is-invalid');
	      $('#userIdMsg').removeClass('valid-feedback').addClass('invalid-feedback').text('중복되는 아이디입니다.');
	      $('#userIdChecked').text('false');
	    }
	    return;
	  }
	});
	
	
	$('#userPassword').on('input', function() {
		
		const password = document.getElementById('userPassword').value;
	   //  const passwordRegex = /^[A-Za-z\d!@#$%^&*-_=+]{8,16}$/;
	    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+=-])[A-Za-z\d!@#$%^&*()_+=-]{8,16}$/;
	    
	    console.log(passwordRegex.test(password))
	    if (!passwordRegex.test(password)) {
			
	        // $('#passwordMsg').text('비밀번호는 8~16자의 영문 대/소문자, 숫자, 특수문자(!@#$%^&*_+-=)만 사용 가능합니다.').css('color', 'red').show();
	        $('#userPassword').removeClass('is-valid').addClass('is-invalid');
	        $('#passwordMsg').removeClass('valid-feedback').addClass('invalid-feedback').text('비밀번호는 8~16자의 영문 대/소문자, 숫자, 특수문자(!@#$%^&*_+-=)만 사용 가능합니다.');
	        $('passwordChecked').text('false');
	        event.preventDefault();
	        return;
	    } else {
			$('#userPassword').removeClass('is-invalid').addClass('is-valid');
			$('#passwordMsg').removeClass('invalid-feedback').addClass('valid-feedback').text('사용 가능한 비밀번호입니다.');
			$('#passwordChecked').text('true');
	        event.preventDefault();
	        return;
		}
	});

	$('#userName').on('input', function() {
		
		const name = document.getElementById('userName').value;
	    // const nameRegex = /^[A-Za-z가-힣\s]+$/;
	    const nameRegex = /^[가-힣a-zA-Z]{2,18}$/;
	    
	    if ( !nameRegex.test(name)) {

			$('#userName').removeClass('is-valid').addClass('is-invalid');
	        $('#userNameMsg').removeClass('valid-feedback').addClass('invalid-feedback').text('이름은 최소 2자 최대 18자의 한글 및 영문 사용만 가능합니다.');
	        $('#userNameChecked').text('false');
	        event.preventDefault();
	        return;
	    } else {
			$('#userName').removeClass('is-invalid').addClass('is-valid');
			$('#userNameMsg').removeClass('invalid-feedback').addClass('valid-feedback').text('사용 가능한 이름입니다.');
			$('#userNameChecked').text('true');
	        event.preventDefault();
	        return;
		}
	});
	
	$('#nickname').on('input', function() {
		
		const nickname = document.getElementById('nickname').value;
	    const nicknameRegex = /^[A-Za-z가-힣\d][A-Za-z가-힣\d]{0,9}$/;
	    if (!nicknameRegex.test(nickname)) {
			
			$('#nickname').removeClass('is-valid').addClass('is-invalid');
	        $('#nicknameMsg').removeClass('valid-feedback').addClass('invalid-feedback').text('닉네임은 최소 1자 최대 10자의 한글, 영어, 숫자만 사용 가능합니다.');
	        $('#nicknameChecked').text('false');
	        event.preventDefault();
	        return;
	    } else {
			
			let result;
			
			$.ajax({
					
				url : "/user/rest/checkBadWord",
				method : "post",
				dataType : "json",
				headers : {
					"accept" : "application/json",
					"content-type" : "application/json"
				},
				async : false,
				data : JSON.stringify({
					value : $('#nickname').val()
				}),
				success : function(responseBody, httpStatus) {
					
					result = responseBody;
					
					
				},
				error : function(jqXHR, textStatus, errorThrown) {
					
					alert('오류 발생 : ' + jqXHR.responseText);
				}
			});
			
			if (result === false) {
				
				$('#nickname').removeClass('is-valid').addClass('is-invalid');
	      		$('#nicknameMsg').removeClass('valid-feedback').addClass('invalid-feedback').text('욕설은 사용할 수 없습니다.');
				return;
			}
			
			$.ajax({
					
				url : "/user/rest/checkDuplication",
				method : "post",
				dataType : "json",
				headers : {
					"accept" : "application/json",
					"content-type" : "application/json"
				},
				data : JSON.stringify({
					type : 1,
					value : $('#nickname').val()
				}),
				async : false,
				success : function(responseBody, httpStatus) {

					result = responseBody;
				},
				error : function(jqXHR, textStatus, errorThrown) {
					
					alert('오류 발생 : ' + jqXHR.responseText);
				}
			});
			
							
			if (result === true) {
				
	      		$('#nickname').removeClass('is-invalid').addClass('is-valid');
				$('#nicknameMsg').removeClass('invalid-feedback').addClass('valid-feedback').text('사용 가능한 닉네임입니다.');
				$('#nicknameChecked').text('true');
				
			} else {
				
				$('#nickname').removeClass('is-valid').addClass('is-invalid');
				$('#nicknameMsg').removeClass('valid-feedback').addClass('invalid-feedback').text('중복되는 닉네임입니다.');
				$('#nicknameChecked').text('false');
			}
		}
		
	});
	
	$('#email').on('input', function() {
		if (event.keyCode === 32) {
	    	event.preventDefault(); // 기본 동작 취소
    	}
  	});
});

////////////////////////////// captcha //////////////////////////////
$(function() {
	
	  let captchaKey;	
	  let verify = false;
	  
	  function loadCaptcha() {
	    $.get("/user/rest/nkey?code=0", function(response) {
	      captchaKey = JSON.parse(response).key;
	      var captchaImageUrl = "/user/rest/image?key=" + captchaKey;
	      $("#captchaImageContainer").html($("<img>").attr("src", captchaImageUrl));
	    });
	  }
	
	  $("#verify-button").click(function() {
	    var captchaValue = $("#captcha-input").val();
	
	    $.ajax({
	      url: "/user/rest/verify",
	      method: "GET",
	      data: {
	        key: captchaKey,
	        value: captchaValue
	      },
	      success: function(response) {
	        var result = JSON.parse(response);
	        if (result.result) {
	          alert("캡차 확인 성공!");
	          $('#captchaChecked').text('true');
	          document.getElementById('verify-button').style.visibility = 'hidden';
	        } else {
	          alert("캡차를 다시 확인해주세요.");
	          $('#captchaChecked').text('false');
	          loadCaptcha(); // 새로운 캡차 이미지 로드
	        }
	      },
	      error: function(xhr, status, error) {
	        console.error("캡차 검증 실패:", error);
	        alert("캡차를 다시 확인해주세요.");
	        $('#captchaChecked').text('false');
	        loadCaptcha(); // 새로운 캡차 이미지 로드
	      }
	    });
	  });
	
	  loadCaptcha(); // 페이지 로드 시 캡차 로드
});


////////////////////////////// phone 인증 //////////////////////////////
$(function() {
	
	var checkNum; // 인증번호 저장 변수

    // 휴대폰 번호 인증 요청
    $('#sendAuthPhoneNum').click(function() {
		
		var phone = $('#phoneNumber').val(); // 입력된 휴대폰 번호 가져오기
		
		const phoneRegex = /^(?:(010)|(01[1|6|7|8|9]))\d{3,4}(\d{4})$/;
		
		if( !phoneRegex.test(phone)) {
			
			$('#phoneNumberMsg').text("양식에 맞게 작성해주세요. (ex. 01012341234)").removeClass('text-success').addClass('text-danger').show();
			event.preventDefault();
			return;
		} else {
			
			$('#phoneNumberMsg').css('display','none');
			
		}

        // 서버로 POST 요청 보내기
        $.ajax({
            url: "/user/rest/sendPhoneNumberAuthNum", // 요청할 URL
            type: "POST", // POST 요청
            data: { to: phone }, // 전송할 데이터
            dataType: "json", // 응답 데이터 형식은 JSON으로 설정
            success: function(data) {
                if (data) { // 요청이 성공하면
                    checkNum = data; // 전송된 인증번호 저장
                    
                    $('#phoneAuthCode').attr("type", "number");
                    alert('인증번호를 전송했습니다. (만료 시간 : 3분)');
                    
                } else {
                    alert('인증번호 전송에 실패했습니다. 다시 시도해주세요.');
                }
            },
            error: function(jqXHR, textStatus, errorThrown) { // 요청이 실패하면
                console.error("AJAX error: " + textStatus + ' : ' + errorThrown);
                alert("서버 요청 중 오류가 발생했습니다. 다시 시도해주세요.");
            }
        });
    });

    // 인증번호 확인
    $('#phoneAuthCode').on('input',function() {
		
		$('#phoneNumberMsg').show();
		const value = $('input[name="phoneAuthCode"]').val();
		if(value == '')
			return;
		
		$.ajax({
            url: "/user/rest/checkAuthNum", // 요청할 URL
            type: "POST", // POST 요청
            data: JSON.stringify({
				codeKey: getCookie("PHONEAUTHKEY"),
				codeValue: value
			}),
            contentType: "application/json", // 응답 데이터 형식은 JSON으로 설정
            success: function(data) {
				
                if (data==true) { // 요청이 성공하면
                   
					$('#phoneNumberMsg').text("인증번호가 일치합니다.").removeClass('text-danger').addClass('text-success').show();
		            $('#phoneNumber').attr('readonly', true);
		            $('#sendAuthPhoneNum').attr('disabled', true);
		            $('#phoneAuthCode').attr('type', 'hidden');
		            $('#phoneNumberChecked').text('true');
                    
                } else {
					
                    $('#phoneNumberMsg').text("인증번호가 일치하지 않습니다.").removeClass('text-success').addClass('text-danger').show();
                    
                }
            },
            error: function(jqXHR, textStatus, errorThrown) { // 요청이 실패하면
                console.error("AJAX error: " + textStatus + ' : ' + errorThrown);
                alert("서버 요청 중 오류가 발생했습니다. 다시 시도해주세요.");
            }
        });
    });
})

////////////////////////////// email 인증 //////////////////////////////
$(function() {
	
	let codeKey;
	
	$("#sendAuthEmail").click(function () {
				
		
		const email = $("#email").val(); //사용자가 입력한 이메일 값 얻어오기
		console.log(email);
		
		email_regex = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i;
		
		if( !email_regex.test(email) ) {
			
			$('#emailMsg').text("양식에 맞게 작성해주세요. (ex. test@test.com)").removeClass('text-success').addClass('text-danger').show();
			event.preventDefault();
			return;
			
		} else {
			
			$('#emailMsg').css('display','none');
			
		}
		
		$('#emailAuthCode').attr("type", "number");

		//Ajax로 전송
		$.ajax({
			url : '/user/rest/sendEmailAuthNum',
			data : {
				email : email
			},
			type : 'POST',
			dataType : 'json',
			success : function(result) {

				codeKey = result;
				alert("인증 코드가 입력하신 이메일로 전송 되었습니다. (만료 시간 : 3분)");
			}
		}); //End Ajax
	});

	$("#emailAuthCode").on("input", function() {

		$('#emailMsg').show();
		
		const value = $('input[name="emailAuthCode"]').val();
		if(value == '')
			return;
		
		$.ajax({
            url: "/user/rest/checkAuthNum", // 요청할 URL
            type: "POST", // POST 요청
            data: JSON.stringify({
				codeKey: getCookie("EMAILAUTHKEY"),
				codeValue: value
			}),
            contentType: "application/json", // 응답 데이터 형식은 JSON으로 설정
            success: function(data) {
				
                if (data==true) { // 요청이 성공하면
                   
					$("#emailMsg").text('인증번호가 일치합니다.').removeClass('text-danger').addClass('text-success').show();
					$('#sendAuthEmail').attr('disabled', true);
					$('#email').attr('readonly', true);
					$('#emailAuthCode').attr('type', 'hidden');
					$('#emailChecked').text("true");
                    
                } else {
					
                    $("#emailMsg").text('인증번호가 불일치합니다.').removeClass('text-success').addClass('text-danger').show();
                    
                }
            },
            error: function(jqXHR, textStatus, errorThrown) { // 요청이 실패하면
                console.error("AJAX error: " + textStatus + ' : ' + errorThrown);
                alert("서버 요청 중 오류가 발생했습니다. 다시 시도해주세요.");
            }
        });
	});
});

function getCookie(codeKeyName) {
  const cookies = document.cookie.split('; ');
  for (const cookie of cookies) {
    const [key, value] = cookie.split('=');
    if (key === codeKeyName) {
      return value;
    }
  }
  return null;
}

/*
const userId = getCookie('userId');
console.log(userId); // 예: "kim1234"
*/
