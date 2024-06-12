// validation.js

document.getElementById('signupForm').addEventListener('submit', function (event) {
    // 아이디 유효성 검사
    const userId = document.getElementById('userId').value;
    const userIdRegex = /^[a-z0-9_-]{5,20}$/;
    if (!userIdRegex.test(userId)) {
        alert('아이디는 5~20자의 영문 소문자, 숫자와 특수기호(_),(-)만 사용 가능합니다.');
        event.preventDefault();
        return;
    }

    // 비밀번호 유효성 검사
    const password = document.getElementById('password').value;
    const passwordRegex = /^[A-Za-z\d@$!%*?&]{8,16}$/;
    if (!passwordRegex.test(password)) {
        alert('비밀번호는 8~16자의 영문 대/소문자, 숫자, 특수문자만 사용 가능합니다.');
        event.preventDefault();
        return;
    }

    // 이름 유효성 검사
    const name = document.getElementById('name').value;
    const nameRegex = /^[^\s][A-Za-z0-9\s]{0,9}$/;
    if (!nameRegex.test(name)) {
        alert('이름은 최소 1자 최대 10자의 영어, 숫자, 띄어쓰기만 사용 가능합니다.');
        event.preventDefault();
        return;
    }

    // 닉네임 유효성 검사
    const nickname = document.getElementById('nickname').value;
    const nicknameRegex = /^[가-힣a-zA-Z]{2,18}$/;
    if (!nicknameRegex.test(nickname)) {
        alert('닉네임은 최소 2자 최대 18자의 한글 및 영문 사용만 가능합니다.');
        event.preventDefault();
        return;
    }

    // 생년월일 유효성 검사 (Date input type이 기본적으로 유효성을 체크함)
    const birthday = document.getElementById('birthday').value;
    if (!birthday) {
        alert('생년월일을 입력해 주세요.');
        event.preventDefault();
        return;
    }

    // 성별 유효성 검사
    const sex = document.getElementById('sex').value;
    if (!['0', '1', '2'].includes(sex)) {
        alert('성별을 올바르게 선택해 주세요.');
        event.preventDefault();
        return;
    }

    // 이메일 유효성 검사 (Email input type이 기본적으로 유효성을 체크함)
    const email = document.getElementById('email').value;
    if (!email) {
        alert('이메일을 입력해 주세요.');
        event.preventDefault();
        return;
    }

    // 휴대전화번호 유효성 검사
    const phoneNumber = document.getElementById('phoneNumber').value;
    const phoneNumberRegex = /^010-\d{4}-\d{4}$/;
    if (!phoneNumberRegex.test(phoneNumber)) {
        alert('휴대전화번호는 010-1234-1234 형식으로 입력해 주세요.');
        event.preventDefault();
        return;
    }
});
