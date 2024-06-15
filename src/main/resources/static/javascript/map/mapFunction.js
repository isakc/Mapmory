/**
 * 
 */
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

function setMarkers(coordinates) {
	const markerImages = [API_KEYS.cdnUrl + API_KEYS.privateMarker,
						  API_KEYS.cdnUrl + API_KEYS.pulicMarker,
						 API_KEYS.cdnUrl + API_KEYS.followMarker,
						  API_KEYS.cdnUrl + API_KEYS.recommendMarker,
						 API_KEYS.cdnUrl + API_KEYS.currentMarker,
						 API_KEYS.cdnUrl + API_KEYS.startMarker,
						 API_KEYS.cdnUrl + API_KEYS.endMarker
	];

	coordinates.forEach(location => {
		let markerPosition = new kakao.maps.LatLng(location.latitude, location.longitude);
		let markerOptions = {
			position: markerPosition,
			clickable: true
		};

		if (location.markerType) {
			let icon = new kakao.maps.MarkerImage( markerImages[location.markerType], new kakao.maps.Size(31, 35), {
				offset: new kakao.maps.Point(16, 34)
			});
			markerOptions.image = icon;
			markerOptions.zIndex = 4;
		}

		let marker = new kakao.maps.Marker(markerOptions);

		marker.setMap(map);
		markers.push(marker);
		
		kakao.maps.event.addListener(marker, 'mouseout', function() {
			
			map.setCursor(null);
		});

		kakao.maps.event.addListener(marker, 'mouseover', function() {
			
			map.setCursor('pointer');
		});

		kakao.maps.event.addListener(marker, 'click', function() {
			selectLatitude = location.latitude;
			selectLongitude = location.longitude;
			
			$.ajax({
				url: "/map/rest/getDetailRecord", // 요청을 보낼 URL을 입력
				contentType: 'application/json', // Content-Type을 JSON으로 설정
				method: 'POST',
				data: JSON.stringify({ recordNo: location.recordNo }),
				success: function(response) {
					console.log(response);

					if (response.length != 0) {
						deleteHTML();

						let html = `
	    		                <h2>Record Information</h2>
	    		                <p><strong>Record Title:</strong> ${response.recordTitle}</p>
	    		                <p><strong>Checkpoint Address:</strong> ${response.checkpointAddress}</p>
	    		                <p><strong>Media Name:</strong> ${response.mediaName}</p>
	    		                <h3>Image Tags</h3>
	    		                <ul>
	    		                	${response.imageName.map(tag => `<li><img src="https://kr.object.ncloudstorage.com/mapmory-object/productImage/test.png" alt="${tag.imageTagText}" /></li>`).join('')}
	    		            	</ul>
	    		                <h3>Hashtags</h3>
	    		                <ul>
	    		                    ${response.hashtag.map(tag => `<li>${tag.imageTagText}</li>`).join('')}
	    		                </ul>
	    		                <p><strong>Category Name:</strong> ${response.categoryName}</p>
	    		                <p><strong>Category Imoji:</strong> <img src="@{/category/image/${response.categoryImoji} }" alt="Category Imoji" /></p>
	    		                <p><strong>Record Text:</strong> ${response.recordText}</p>
	    		                <p><strong>Record Add Date:</strong> ${response.recordAddDate}</p>
	    		            `;

						const resultDiv = document.getElementById('result');
						resultDiv.innerHTML = html;
					} else {
						alert("해당하는 기록이 없습니다!!");
					}//if~else

				}, // success
				error: function(error) {
					console.log('ajax 에러 발생:', error);
				} // error
			}); //기록 검색 ajax
		}); // 마커에 클릭이벤트를 등록

	});
} // setMarkers

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
	deleteHTML();
}

function deleteHTML() {

	const pathListDiv = document.getElementById('result');
	pathListDiv.innerHTML = '';

	const pathDetailsDiv = document.getElementById('description');
	pathDetailsDiv.innerHTML = '';
}