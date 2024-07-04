
function handleButtonClick() {
    document.getElementById('buttonGroup').style.display = 'none';
    document.getElementById('loadingContainer').style.display = 'block';
}

$(document).ready(function(){ 
    if ($.datepicker) {              
	    $.datepicker.setDefaults({
	    closeText: "닫기",
	    currentText: "오늘",
	    prevText: '이전 달',
	    nextText: '다음 달',
	    monthNames: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
	    monthNamesShort: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
	    dayNames: ['일', '월', '화', '수', '목', '금', '토'],
	    dayNamesShort: ['일', '월', '화', '수', '목', '금', '토'],
	    dayNamesMin: ['일', '월', '화', '수', '목', '금', '토'],
	    weekHeader: "주",
	    yearSuffix: '년'
	    });
    }
 });
 
function zoomImage(imageContainer) {
    var zoomedImgContainer = document.querySelector('.zoomed-image');
    var zoomedImg = zoomedImgContainer.querySelector('img');

    // 이미지 소스 설정
    var originalImgSrc = imageContainer.querySelector('img').src;
    zoomedImg.src = originalImgSrc;

    // 확대된 이미지 보이기
    zoomedImgContainer.style.display = 'block';

    // body 스크롤 방지
    document.body.style.overflow = 'hidden';
}

function closeZoomedImage() {
    var zoomedImgContainer = document.querySelector('.zoomed-image');

    // 확대된 이미지 숨기기
    zoomedImgContainer.style.display = 'none';

    // body 스크롤 다시 허용
    document.body.style.overflow = 'auto';
}

function zoomVideo(element) {
    const zoomedContainer = $('#zoomed-video-container');
    const zoomedVideoSource = $('#zoomed-video-source');
    const videoSrc = $(element).find('source').attr('src');

    zoomedVideoSource.attr('src', videoSrc);
    const zoomedVideo = document.getElementById('zoomed-video');
    zoomedVideo.load();
    zoomedVideo.play();
    // body 스크롤 방지
    document.body.style.overflow = 'hidden';
    zoomedContainer.css('display', 'flex');
}

function closeZoom() {
    const zoomedContainer = $('#zoomed-video-container');
    const zoomedVideo = document.getElementById('zoomed-video');

    zoomedVideo.pause();

    // body 스크롤 다시 허용
    document.body.style.overflow = 'auto';
    zoomedContainer.css('display', 'none');
}

function updateLabel() {
    const checkbox = document.getElementById('sharedDateType');
    const label = document.querySelector('label[for="sharedDateType"]');

    if (checkbox.checked) {
        label.textContent = '공유됨';
    } else {
        label.textContent = '비공유';
    }
}
function autoResize(textarea) {
    textarea.style.height = 'auto'; // Reset textarea height
    textarea.style.height = textarea.scrollHeight + 'px'; // Set new height based on content
}

//입력된 해시태그를 처리하는 함수
function processHashtags() {
    // 입력된 해시태그 텍스트 가져오기
    var hashtagText = document.getElementById('hashtagText').value.trim();

    // 첫 번째 문자가 #이 아닌 경우 처리
    if (!hashtagText.startsWith('#')) {
        document.getElementById('hashtagText').value = '#' + hashtagText;
        showModal('해시태그 오류', "해시태그는 '#'로 시작해야 합니다.");
        return false;
    }

    // 해시태그를 # 기준으로 분리하여 배열로 만듭니다
    var hashtags = hashtagText.split('#').filter(Boolean); // Boolean을 사용하여 빈 문자열을 제거합니다
	let hashtagCount=20;
    // 최대 입력 가능한 해시태그 개수는 20개입니다
    if (hashtags.length > hashtagCount) {
        hashtags = hashtags.slice(0, hashtagCount); // 최대 20개까지만 유지
        document.getElementById('hashtagText').value = '#' + hashtags.join('#');
        showModal('해시태그 오류', "최대 20개의 해시태그까지 입력할 수 있습니다.");
        return false;
    }

	let hashtagNumber=20;
    // 각 해시태그의 길이가 20자 이하인지 검사합니다
    for (var i = 0; i < hashtags.length; i++) {
        var hashtag = hashtags[i];
        if (hashtag.length > hashtagNumber) {
            hashtags[i] = hashtag.substring(0, hashtagNumber); // 20자까지만 유지
            document.getElementById('hashtagText').value = '#' + hashtags.join('#');
            showModal('해시태그 오류', "각 해시태그는 최대 20자까지 입력할 수 있습니다.");
            return false;
        }
    }

    // 해시태그를 배열 형태로 처리하거나 필요한 로직을 추가할 수 있습니다
    //console.log("처리된 해시태그 배열:", hashtags);

    // 여기서 추가적으로 필요한 로직을 수행하거나 서버에 전송할 수 있습니다

    return true; // 유효성 검사를 모두 통과했을 경우 true 반환
}
