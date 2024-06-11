/**
 * 
 */
function getCurrentDate() {

	let date = new Date();

	return date.getFullYear().toString() + "-"
		+ String(date.getMonth() + 1).padStart(2, '0') + "-"
		+ String(date.getDate()).padStart(2, '0') + "T"
		+ String(date.getHours()).padStart(2, '0') + ":"
		+ String(date.getMinutes()).padStart(2, '0') + ":"
		+ String(date.getSeconds()).padStart(2, '0');
}//getCurrentDate: 현재 날짜 구하기

function convertUnixTimestamptoLocalDateTime(unixTimestamp, period) {
	const milliseconds = unixTimestamp * 1000; // 밀리초 단위로 변환

	// Date 객체 생성
	const date = new Date(milliseconds);
	let addMonth = period ? 2 : 1

	const year = date.getFullYear();
	const month = String(date.getMonth() + addMonth).padStart(2, '0'); // 월은 0부터 시작하므로 +1
	const day = String(date.getDate()).padStart(2, '0');
	const hours = String(date.getHours()).padStart(2, '0');
	const minutes = String(date.getMinutes()).padStart(2, '0');
	const seconds = String(date.getSeconds()).padStart(2, '0');

	return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
}//convertUnixTimestamptoLocalDateTime: Unix 타임스탬프를 LocalDatetime으로 변환

function getPaymentMethod(paymentMethod) {
	const paymentMethods = {
		1: 'card',
		2: 'kakaopay',
		3: 'payco',
		4: 'tosspay'
	};

	return paymentMethods[paymentMethod];
}//getSubscriptionPaymentMethod: 결제수단 확인

function getLastFourDigits(cardNumber) {
	return cardNumber.slice(-4); // 마지막 4자리 반환
}//getLastFourDigits: 카드번호 마지막 4자리 가져오기

function getSubscriptionPG(selectedValue) {
	let pg;
	
	if (selectedValue == 1) {
		pg = 'html5_inicis';
	} else if (selectedValue == 2) {
		pg = 'kakaopay.TCSUBSCRIP';
	} else {
		pg = 'payco.AUTOPAY';
	}
	
	return pg;
}//getSubscriptionPG 구독 일 경우 pg사 가져오기