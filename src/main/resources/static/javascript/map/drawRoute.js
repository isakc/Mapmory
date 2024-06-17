/**
 * 
 */

const drawRoute = (type) => {
	const urlParameter = type == 1 ? "getPedestrianRoute" : "getCarRoute";
	deleteHTML();
	hideMarkers();
	clearPolylines();

	getCurrentLocation().done(function(location) {

		let tDescription; // 설명
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

			success: function(response) {
				console.log(response);

				setMarkers([{ latitude: requestData.startY, longitude: requestData.startX, markerType:5 }]);
				setMarkers([{ latitude: requestData.endY, longitude: requestData.endX,  markerType:6 }]);

				const tDistance = "<span>총 거리 : " + ((response.totalDistance) / 1000).toFixed(1) + "km,</span>";
				const tTime = "<span>총 시간 : " + ((response.totalTime) / 60).toFixed(0) + "분</span>";

				$("#result").html(tDistance + tTime);

				bounds = new kakao.maps.LatLngBounds(); // 중심좌표 변경

				for (let i = 0; i < response.lat.length; i++) {
					const latlng = new kakao.maps.LatLng(response.lat[i], response.lon[i]);
					bounds.extend(latlng);
					drawInfoArr.push(latlng);
				}

				let description = $('#description');
				description.empty();
				response.description.forEach(function(desc) {
					let p = $('<p></p>').text(desc);
					description.append(p);
				});

				drawLine(drawInfoArr, 1);
				map.setBounds(bounds);
			}, // success

			error: function(request, status, error) {
				console.error("에러!!");
			} // error
		}); // ajax
	}).fail(function(error) {
		console.log('Error getting location:', error);
	});
}; // 보행자, 자동차

const drawTransitRoute = () => {

	paths = [];
	deleteHTML();
	clearPolylines();

	getCurrentLocation().done(function(location) {

		let tDescription; // 설명
		let drawInfoArr = []; // 선을 그릴 위도,경도 모음
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

			success: function(response) {
				console.log(response);

				if (response.length != 0) {
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

					const pathListDiv = document.getElementById('result');

					paths.forEach((path, index) => {
						const pathDiv = document.createElement('div');
						pathDiv.textContent = `Path ${index + 1}: Total Fare: ${path.totalFare}, Total Time: ${path.totalTime}`;
						pathDiv.style.cursor = 'pointer';
						pathDiv.addEventListener('click', () => showPathDetails(index));
						pathListDiv.appendChild(pathDiv);
					});

					function showPathDetails(index) {
						clearPolylines();
						clearMarkers();

						for (let i = 0; i < paths[index].routes.length; i++) {
							drawInfoArr = [];

							for (let j = 0; j < paths[index].routes[i].lineStringLat.length; j++) {
								const lat = paths[index].routes[i].lineStringLat[j];
								const lon = paths[index].routes[i].lineStringLon[j];
								const latlng = new kakao.maps.LatLng(lat, lon);
								bounds.extend(latlng);
								drawInfoArr.push(latlng);
							} // 이게 안에 있는 세부정보들

							setMarkers([{ latitude: paths[index].routes[i].startLat, longitude: paths[index].routes[i].startLon }]);

							if (paths[index].routes[i].mode == 'WALK') {
								drawLine(drawInfoArr, 0);
							} else {
								drawLine(drawInfoArr, 1);
							}

							map.setBounds(bounds);
						} // 이게 라우트 1,2,3,4,5

						const path = paths[index];
						const pathDetailsDiv = document.getElementById('description');
						pathDetailsDiv.innerHTML = `
	                        <h2>Path ${path.index}</h2>
	                        <p>Total Fare: ${path.totalFare}</p>
	                        <p>Total Time: ${path.totalTime}</p>
	                        <p>Total Distance: ${path.totalDistance}</p>
	                        <p>Total Walk Time: ${path.totalWalkTime}</p>
	                        <p>Transfer Count: ${path.transferCount}</p>
	                        <p>Path Type: ${path.pathType}</p>
	                        <h3>Routes:</h3>
	                        ${path.routes.map(route => `
	                            <div>
	                                <p>Mode: ${route.mode}</p>
	                                <p>Route: ${route.routeName}</p>
	                                <p>Start: ${route.startName}</p>
	                                <p>End: ${route.endName}</p>
	                            </div>
	                        `).join('')}
	                    `;
					} // showPathDetails

					showPathDetails(0); // 0번째 경로로 그리기

					setMarkers([
						{ latitude: requestData.startY, longitude: requestData.startX,  markerType:5 },
						{ latitude: requestData.endY, longitude: requestData.endX,  markerType:6 }
					]);

				}//if
				else {
					alert("해당 경로찾기가 없습니다!!");
				}



			} // success
		}); // ajax 대중교통
	}).fail(function(error) {
		console.log('위치를 불러오는데 실패', error);
	});
}; // 대중교통 경로찾기