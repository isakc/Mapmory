/**
 * 
 */

let clickedMarkerImage = new kakao.maps.MarkerImage(markerImages[7], new kakao.maps.Size(40, 45) );
let clickedMarker = null; // 클릭된 마커를 추적할 변수
let beforeMarkerType = null;

function clickMarkerFromCard(index, contentList) {
    // 마커 배열에서 해당 record을 가진 마커 찾기
    const marker = markers.find(m => {
    	if (m.locationData.markerType === 3) {
        	return m.locationData.latitude === contentList[index].latitude &&
               	m.locationData.longitude === contentList[index].longitude &&
               	m.locationData.addressName === contentList[index].addressName;
   		}else {
        	return m.locationData.latitude === contentList[index].latitude && 
        	       m.locationData.longitude === contentList[index].longitude &&
                   m.locationData.recordNo === contentList[index].recordNo;
    	}
	});
    
    if (marker) {
        clickContentMarker(marker, index, contentList);
    }
}

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
            	clickContentMarker(marker, index, contentList);
			}
        });// 마커에 클릭이벤트를 등록
    });

    kakao.maps.event.addListener(map, 'click', function() {
        hideResultDivs();
        
		resultDivsBtn.removeClass('on');
		if(recordList.length!=0 || placeList.length != 0){
			resultDivsBtn.addClass('on');	
		}
		
    });// 맵을 클릭한 경우 해제 시키기
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

function clickContentMarker(marker, index, contentList) {
    selectLatitude = contentList[index].latitude;
    selectLongitude = contentList[index].longitude;
    map.setCenter(new kakao.maps.LatLng(selectLatitude, selectLongitude));
    
    if (clickedMarker) {
        clickedMarker.setImage(new kakao.maps.MarkerImage(markerImages[beforeMarkerType], new kakao.maps.Size(40, 45))); // 이전 클릭된 마커를 원래 이미지로 되돌림
    }
    
    beforeMarkerType = contentList[index].markerType;
    
    marker.setZIndex(5);
    marker.setImage(clickedMarkerImage); // 새로 클릭된 마커를 클릭된 이미지로 변경
    clickedMarker = marker; // 현재 클릭된 마커를 업데이트

    $(".infoItem").removeClass("on");
	description.addClass("on");
	deleteDescription();
	
	if(contentList[0].markerType === 3){
		description.append(detailPlaceElement(index) );
	}else{
		description.append(simpleRecordElement(index) );
	}
	
	showResultDivs();
    
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
	/*result.addClass('on');*/
	$(".swiper-container").addClass('on');
}// showResult: 결과 목록 보여줄때=> simpleRecord에서 뒤로갈때

function hideResultDivs(){
	resultDivs.css('display', 'none');
}//맵에서 땅 누를때

function showResultDivs(){
	resultDivs.css('display', 'block');
	resultDivsBtn.removeClass('on');
}// 위로 버튼 누를때

function showDescription(){
	clearPolylines();
	clearStartEndMarkers();
	$(".infoItem").removeClass('on');
	description.addClass('on');
	showMarkers();
}// 경로찾기에서 X 버튼 눌렀을때