
function handleButtonClick() {
    document.getElementById('buttonGroup').style.display = 'none';
    document.getElementById('loadingContainer').style.display = 'block';
}

$(document).ready(function(){               
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
