/**
 * 
 */

async function getAddressName(geocoder, coord) {
	return new Promise((resolve, reject) => {
		geocoder.coord2Address(coord.getLng(), coord.getLat(), function(result, status) {
			if (status === kakao.maps.services.Status.OK) {
				resolve(result[0].address.address_name);
			} else {
				reject('Failed to get address');
			}
		});
	});
}
        
const drawRoute = (type) => {
	const urlParameter = type == 1 ? "getPedestrianRoute" : "getCarRoute";

	getCurrentLocation().done(function(location) {
		let drawInfoArr = []; // 선을 그릴 위도,경도 모음

		const requestData = {
			startX: location.coords.longitude,
			startY: location.coords.latitude,
			endX: selectLongitude,
			endY: selectLatitude,
			reqCoordType: "WGS84GEO",
			resCoordType: "WGS84GEO",
			startName: "출발",
			endName: "도착"
		};

		$.ajax({
			method: "POST",
			contentType: 'application/json; charset=utf-8',
			url: "/map/rest/" + urlParameter,
			data: JSON.stringify(requestData),

			success: async function(response) {
				console.log(response);
				
				const geocoder = new kakao.maps.services.Geocoder();
            	const startEndCoord = [
                	new kakao.maps.LatLng(location.coords.latitude, location.coords.longitude), 
                	new kakao.maps.LatLng(selectLatitude, selectLongitude)
            	];
                const [startAddress, endAddress] = await Promise.all(startEndCoord.map(coord => getAddressName(geocoder, coord)));
                
                response.startEndAddressName = [startAddress, endAddress];
				
				clearPolylines();
				hideMarkers();
				clearStartEndMarkers();
				setMarkers([{ latitude: requestData.startY, longitude: requestData.startX, markerType:5 },
							{ latitude: requestData.endY,   longitude: requestData.endX,   markerType:6 } ]);
							
				bounds = new kakao.maps.LatLngBounds(); // 중심좌표 변경
				
				for (let i = 0; i < response.lat.length; i++) {
					const latlng = new kakao.maps.LatLng(response.lat[i], response.lon[i]);
					bounds.extend(latlng);
					drawInfoArr.push(latlng);
				}
				
				$(".infoItem").removeClass("on");
				routeDescriptionList.addClass("on");
				
				deleteRouteDescriptionList();
				routeDescriptionList.append(routeListElement(response));
				drawLine(drawInfoArr, 1);
				map.setBounds(bounds);
			}, // success
			error: function() {
				alert("길찾기 실패!!")
				console.error("에러!!");
			} // error
		}); // ajax
	}).fail(function(error) {
		alert("길찾기 실패!!")
		console.log('Error getting location:', error);
	});
}; // 보행자, 자동차

const drawTransitRoute = () => {
	paths = [];

	getCurrentLocation().done(function(location) {
		bounds = new kakao.maps.LatLngBounds(); // 중심좌표 변경
		
		const requestData = {
			startX: location.coords.longitude,
			startY: location.coords.latitude,
			endX: selectLongitude,
			endY: selectLatitude,
			lang: "0",
			count: "10",
			format: "json"
		}; // 대중교통

		$.ajax({
			method: "POST",
			contentType: "application/json; charset=utf-8",
			Accept: "application/json; charset=utf-8",
			url: "/map/rest/getTransitRouteList", // 대중교통
			data: JSON.stringify(requestData),

			success: async function(response) {
				console.log(response);

				if (response.length != 0) {
					const geocoder = new kakao.maps.services.Geocoder();
            		const startEndCoord = [
                		new kakao.maps.LatLng(location.coords.latitude, location.coords.longitude), 
                		new kakao.maps.LatLng(selectLatitude, selectLongitude)
            		];
                	const [startAddress, endAddress] = await Promise.all(startEndCoord.map(coord => getAddressName(geocoder, coord)));
                
					hideMarkers();
					
					response.forEach((path, index) => {
						const routeList = path.routeList;

						paths.push({
							index: index + 1,
							totalFare: path.totalFare,
							totalTime: path.totalTime,
							totalDistance: path.totalDistance,
							totalWalkTime: path.totalWalkTime,
							transferCount: path.transferCount,
							pathType: path.pathType,
							startEndAddressName: [startAddress, endAddress],

							routes: routeList.map(route => ({
								mode: route.mode,
								routeName: route.route,
								startName: route.startName,
								startLat: route.startLat,
								startLon: route.startLon,
								endName: route.endName,
								endLat: route.endLat,
								endLon: route.endLon,
								lineStringLat: route.lineStringLat,
								lineStringLon: route.lineStringLon
							}))// routes
						});//path.push
					});//foreach
					
					$(".infoItem").removeClass("on");
					routeDescriptionList.addClass("on");
					
					deleteRouteDescriptionList();
					routeDescriptionList.append(transitRouteListElement(paths) );
					showPathDetails(0, requestData); // 0번째 경로로 그리기
				}//if
				else {
					alert("해당 경로찾기가 없습니다!!");
				}//else
			} // success
			, error(){
				alert("경로찾기를 찾아오는데 실패했습니다!!");
			}
		}); // ajax 대중교통
	}).fail(function(error) {
		alert("경로찾기를 찾아오는데 실패했습니다!!");
		console.log('경로 찾기 실패', error);
	});
}; // 대중교통 경로찾기

function showPathDetails(index, requestData) {
	let drawInfoArr = []; // 선을 그릴 위도,경도 모음
	clearPolylines();
	clearStartEndMarkers();
	
	setMarkers([
		{ latitude: requestData.startY, longitude: requestData.startX,  markerType:5 },
		{ latitude: requestData.endY, longitude: requestData.endX,  markerType:6 }
		]);
	
	for (let i = 0; i < paths[index].routes.length; i++) {
		drawInfoArr = [];

		for (let j = 0; j < paths[index].routes[i].lineStringLat.length; j++) {
			const latlng = new kakao.maps.LatLng(paths[index].routes[i].lineStringLat[j], paths[index].routes[i].lineStringLon[j]);
			bounds.extend(latlng);
			drawInfoArr.push(latlng);
		} // 이게 안에 있는 세부정보들

		const route= [{ latitude: paths[index].routes[i].startLat, longitude: paths[index].routes[i].startLon }];
		
		if(paths[index].routes[i].mode  === 'WALK'){
			route[0].markerType = 8;
		}else if(paths[index].routes[i].mode  === 'SUBWAY'){
			route[0].markerType = 9;
		}else if(paths[index].routes[i].mode  === 'BUS'){
			route[0].markerType = 10;
		}else if(paths[index].routes[i].mode === 'EXPRESSBUS'){
			route[0].markerType = 11;
		}else if(paths[index].routes[i].mode === 'AIRPLANE'){
			route[0].markerType = 12;
		}
		
		setMarkers(route);

		if (paths[index].routes[i].mode == 'WALK') {
			drawLine(drawInfoArr, 0);
		} else {
			drawLine(drawInfoArr, 1);
		}

		map.setBounds(bounds);
		
	} // 이게 라우트 1,2,3,4,5
	
	return transitRouteDescriptionElement(paths[index]);
} // showPathDetails

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