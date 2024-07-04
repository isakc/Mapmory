/**
 * 
 */

let clickedMarkerImage = new kakao.maps.MarkerImage(markerImages[7], new kakao.maps.Size(40, 45) );
let clickedMarker = null; // 클릭된 마커를 추적할 변수
let beforeMarkerType = null;

function setMarkers(contentList) {
    contentList.forEach( (content, index) => {
        let marker = createMarkerImage(content);
        
        marker.setMap(map);
        marker.originalImage = markerImages[content.markerType]; // 마커에 원래 이미지를 저장
        marker.locationData = content;// 마커 객체에 location 정보 저장
		
        if(content.markerType === 5 || content.markerType === 6 || content.markerType === 8 || 
        content.markerType === 9 ||content.markerType === 10 || content.markerType === 11 || content.markerType === 12){
			startEndMarkers.push(marker);
		}else if(content.markerType == 4) {
			currentMarker = marker;
		}else{
			markers.push(marker);
		}

        kakao.maps.event.addListener(marker, 'click', function() {
			if(content.markerType === 0 || content.markerType === 1 || content.markerType === 2 || content.markerType === 3){
				
				if(description.hasClass('on')){
					clickContentMarker(index, contentList);
				}
				showResultDivs();
				content.markerType === 3 ? navigateToMarkerOnSelect(index, contentList, 'recommend') : navigateToMarkerOnSelect(index, contentList);
			}
        });// 마커에 클릭이벤트를 등록
        
    });
}
	
function createMarkerImage(location) {
	let markerPosition = new kakao.maps.LatLng(location.latitude, location.longitude);
	let icon = new kakao.maps.MarkerImage(markerImages[location.markerType], new kakao.maps.Size(40, 45) );//마커 이미지

	let markerOptions = {
		position: markerPosition,
		clickable: true,
		image: icon,
		zIndex: 4
	};
	
	let marker = new kakao.maps.Marker(markerOptions);

	return marker;
}// 이미지 있는 마커 생성

function navigateToMarkerOnSelect(index, contentList, recommend) {
    selectLatitude = contentList[index].latitude;
    selectLongitude = contentList[index].longitude;
    map.setCenter(new kakao.maps.LatLng(selectLatitude, selectLongitude));

    if (clickedMarker) {
        clickedMarker.setImage(new kakao.maps.MarkerImage(markerImages[beforeMarkerType], new kakao.maps.Size(40, 45))); // 이전 클릭된 마커를 원래 이미지로 되돌림
        clickedMarker.setZIndex(4); // ZIndex 수정
    }

    beforeMarkerType = contentList[index].markerType;

    markers[index].setZIndex(5);
    markers[index].setImage(clickedMarkerImage); // 새로 클릭된 마커를 클릭된 이미지로 변경

    clickedMarker = markers[index]; // 현재 클릭된 마커를 업데이트

    // 슬라이드로 이동하기 전에 visibleIndex를 계산
    let visibleIndex = 0;
    
    if(recommend){
		$("#swiper-wrapper .placeListItem").each(function(i) {
        if ($(this).is(":visible")) {
            if (i === index) {
                swiper.slideTo(visibleIndex);
                return false; // 루프 종료
            }
            visibleIndex++;
        }
    });
	}else{ $("#swiper-wrapper .resultListItem").each(function(i) {
        if ($(this).is(":visible")) {
            if (i === index) {
                swiper.slideTo(visibleIndex);
                return false; // 루프 종료
            }
            visibleIndex++;
        }
    });
	}
    
}

function clickContentMarker(index, contentList) {
	navigateToMarkerOnSelect(index, contentList);
	
	$(".mapButton").removeClass('on'); // 기록에 들어갈 때 리스트로 보기 버튼 감추기
    $(".infoItem").removeClass("on");//infoItem에 있는 on 지우기
	description.addClass("on");//description에 on
	deleteDescription(); // description 안에 있는 이전 내용 지우기
	
	if(contentList[0].markerType === 3){
		description.append(detailPlaceElement(index) );
	}else{
		description.append(simpleRecordElement(index) );
	}
} // 마커나 리스트에서 클릭했을 경우

function clearPolylines() {
	for (let i = 0; i < polylines.length; i++) {
		polylines[i].setMap(null);
	}

	polylines = []; // 배열을 초기화하여 폴리라인 객체를 삭제
} // clearPolylines: 모든 폴리라인 제거

function clearMarkers() {
	for (let i = 0; i < markers.length; i++) {
		markers[i].setMap(null);
	}
	
	markers = []; // 배열을 초기화하여 마커 객체를 삭제
} // clearMarkers: 모든 마커를 지도에서 제거하는 함수

function clearStartEndMarkers() {
	for (let i = 0; i < startEndMarkers.length; i++) {
		startEndMarkers[i].setMap(null);
	}
	
	startEndMarkers = []; // 배열을 초기화하여 마커 객체를 삭제
} // clearStartEndMarkers

function hideMarkers() {
	markers.forEach(marker => {
		marker.setMap(null);
	});
} // hideMarkers

function showMarkers() {
	markers.forEach(marker => {
		marker.setMap(map);
		bounds.extend(marker.getPosition());
	});
	
	map.setBounds(bounds);
} // showMarkers

function deleteResult() {
	result.html('');
} // deleteResult

function deleteSwiperWrapper() {
	swiperWrapper.html('');
} // deleteSwiperWrapper

function deleteDescription() {
	description.html('');
} // deleteDescription

function deleteRouteDescriptionList() {
	routeDescriptionList.html('');
} // deleteDescription

function showResult() {
	$(".infoItem").removeClass('on');
	
	if(listBtn.hasClass('list')){
		result.addClass('on');
	}else{
		$(".swiper-container").addClass('on');
	}//만약 어디에서 본지 확인후 다시 on 붙이기
	
	listBtn.addClass('on'); //리스트로 볼지, 넘기기로 볼지 선택하는 버튼 다시 보이기	
}// showResult: 결과 목록 보여줄때 => simpleRecord에서 뒤로갈때

function hideResultDivs(){
	resultDivs.css('display', 'none');
}//맵에서 땅 누를때

function showResultDivs(){
	resultDivs.css('display', 'block'); //resultDiv 다시 보이게 하고
	resultDivsBtn.removeClass('on'); // 위로 버튼 없애기
	
	if(result.hasClass('on') || $(".swiper-container").hasClass('on')){
		listBtn.addClass('on');
	}//만약 리스트보기에서 내려갔을 경우 리스트로 볼지, 넘기기로 볼지 선택하는 버튼 다시 보이기		
}// 위로 버튼 누를때

function showDescription(){
	clearPolylines();
	clearStartEndMarkers();
	$(".infoItem").removeClass('on');
	description.addClass('on');
	showMarkers();
}// 경로찾기에서 X 버튼 눌렀을때