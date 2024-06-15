//// kakao
$(document).ready(function() {
	Kakao.init('bde4d0b1dd558fcdac6b04e0ba243c20');
	Kakao.Auth.createLoginButton({
		container: '#kakao-login-btn',
		success: function (authObj) {
		},
		fail: function (err) {
			alert(JSON.stringify(err));
		}
	});
});



//// google




//// naver