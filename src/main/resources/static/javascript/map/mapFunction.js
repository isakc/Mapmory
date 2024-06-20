/**
 * 
 */

const markerImages =
[
	API_KEYS.cdnUrl + API_KEYS.privateMarker,
	API_KEYS.cdnUrl + API_KEYS.pulicMarker,
	API_KEYS.cdnUrl + API_KEYS.followMarker,
	API_KEYS.cdnUrl + API_KEYS.recommendMarker,
	API_KEYS.cdnUrl + API_KEYS.currentMarker,
	API_KEYS.cdnUrl + API_KEYS.startMarker,
	API_KEYS.cdnUrl + API_KEYS.endMarker,
	API_KEYS.cdnUrl + API_KEYS.selectMarker
];

let clickedMarkerImage = new kakao.maps.MarkerImage(markerImages[7], new kakao.maps.Size(40, 45) );
let clickedMarker = null; // 클릭된 마커를 추적할 변수
let beforeMarkerType = null;
		
function drawLine(arrPoint, mode) {
	let polyline = new kakao.maps.Polyline({
		path: arrPoint, // 선을 구성하는 좌표배열
		strokeWeight: 7, // 선의 두께
		strokeColor: mode === 0 ? 'black' : 'blue', // 0 = 걸을 때, 1 = 그 외 선 색깔
		strokeOpacity: 1, // 선의 불투명도, 1에서 0 사이의 값이며 0에 가까울수록 투명
		strokeStyle: mode === 0 ? 'dash' : 'solid' // 0 = 걸을 때 점선, 1 = solide 선의 스타일
	});

	polyline.setMap(map);
	polylines.push(polyline); // 배열에 폴리라인 객체를 추가
} // drawLine: 라인을 그리는 함수

function clickMarkerFromCard(index, contentList) {
    // 마커 배열에서 해당 record을 가진 마커 찾기
    const marker = markers.find(m => 
        m.locationData.latitude === contentList[index].latitude &&
        m.locationData.longitude === contentList[index].longitude &&
        m.locationData.markerType === 3 ? 'm.locationData.addressName === contentList[index].addressName' : m.locationData.recordNo === contentList[index].recordNo
    );
    
    console.log(marker);
    
    if (marker) {
        clickContentMarker(marker, index, contentList);
    }
}

function setMarkers(contentList) {
    contentList.forEach( (content, index) => {
        let marker = createMarkerImage(content);
        
        marker.setMap(map);
        marker.originalImage = markerImages[content.markerType]; // 마커에 원래 이미지를 저장
        marker.clickedImage = markerImages[7];
        content.markerType != 4 ? markers.push(marker) : '';

        // 마커 객체에 location 정보 저장
        marker.locationData = content;

        kakao.maps.event.addListener(marker, 'click', function() {
			if(content.markerType === 0 || content.markerType === 1 || content.markerType === 2 || content.markerType === 3){
            	clickContentMarker(marker, index, contentList);
			}
        });// 마커에 클릭이벤트를 등록
    });

    kakao.maps.event.addListener(map, 'click', function() {
        hideResult();
        deleteDescription();
        
        if (clickedMarker) {
			clickedMarker.setImage(new kakao.maps.MarkerImage(markerImages[beforeMarkerType], new kakao.maps.Size(40, 45))); // 이전 클릭된 마커를 원래 이미지로 되돌림
		}
    });
}
	
function createMarkerImage(location) {
	let markerPosition = new kakao.maps.LatLng(location.latitude, location.longitude);
	let markerOptions = {
		position: markerPosition,
		clickable: true
	};

	if (location.markerType) {
		let icon = new kakao.maps.MarkerImage(markerImages[location.markerType], new kakao.maps.Size(35, 39), {
			offset: new kakao.maps.Point(16, 34)
		});
		markerOptions.image = icon;
		 markerOptions.zIndex = location.markerType === 4 ? 5 : 4
	}

	let marker = new kakao.maps.Marker(markerOptions);

	return marker;
}

function clickContentMarker(marker, index, contentList) {
    selectLatitude = contentList[index].latitude;
    selectLongitude = contentList[index].longitude;
    map.setCenter(new kakao.maps.LatLng(selectLatitude, selectLongitude));

    if (clickedMarker) {
        clickedMarker.setImage(new kakao.maps.MarkerImage(markerImages[beforeMarkerType], new kakao.maps.Size(40, 45))); // 이전 클릭된 마커를 원래 이미지로 되돌림
    }

    beforeMarkerType = contentList[index].markerType;

    marker.setImage(clickedMarkerImage); // 새로 클릭된 마커를 클릭된 이미지로 변경
    clickedMarker = marker; // 현재 클릭된 마커를 업데이트

    hideResult();
	deleteDescription();

	if(contentList[0].markerType === 3){
		$("#description").append(detailPlaceElement(index) );
	}else{
		$("#description").append(simpleRecordElement(index) );
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

function hideMarkers() {
	/* 		markers.forEach(marker => {
				marker.setMap(null);
			});  */

	tempMarkers = [...markers];
	clearMarkers();
}

function showMarkers() {
	markers.forEach(marker => {
		marker.setMap(null);
	});

	clearMarkers();

	markers = [...tempMarkers];
	tempMarkers = [];

	markers.forEach(marker => {
		marker.setMap(map);
		bounds.extend(marker.getPosition());
	});

	map.setBounds(bounds);
	clearPolylines();
	deleteResult();
}

function deleteResult() {

	const resultDiv = document.getElementById('result');
	resultDiv.innerHTML = '';

	const descriptionDiv = document.getElementById('description');
	descriptionDiv.innerHTML = '';
}

function deleteDescription() {
	const descriptionDiv = document.getElementById('description');
	descriptionDiv.innerHTML = '';
}

function hideDescription(){
	
}

function hideResult() {

	const resultDiv = document.getElementById('result');
	resultDiv.style.display = 'none';
}

function showResult() {
	if (clickedMarker) {
		clickedMarker.setImage(new kakao.maps.MarkerImage(markerImages[beforeMarkerType], new kakao.maps.Size(40, 45))); // 이전 클릭된 마커를 원래 이미지로 되돌림
	}
	
	const pathDetailsDiv = document.getElementById('description');
	pathDetailsDiv.innerHTML = '';
	
	const resultDiv = document.getElementById('result');
	resultDiv.style.display = 'block';
}
