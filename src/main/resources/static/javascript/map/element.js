/**
 * 
 */

const recordListElement = (index) => {
	return `
		<div class="card" onClick='clickMarkerFromCard( ${JSON.stringify(index)}, recordList )'>
	    <div class="row g-0">
	      <div class="col-9">
	        <div class="card-body p-1">

	            <div class="row">
	                <h5 class="card-title col-9 fw-bold ellipsis fs-6">${recordList[index].recordTitle}</h5>
	                <span class="col-3"> <img src="/user/rest/emoji/${recordList[index].categoryImoji}" alt="Category Imoji"
	                class="recordEmoji" data-categoryNo ="${recordList[index].categoryNo}"/> </span>
	                
	            </div>

	          <p class="card-text">${recordList[index].recordAddDate}</p>
	          <p class="card-text">
	          	${recordList[index].hashtag && recordList[index].hashtag.length > 0 ?
				recordList[index].hashtag.slice(0, 3).map(tag => 
				`<a href="#"><small class="text-primary hashTag">${tag.imageTagText}</small></a>`).join('')
				: ''}
	          </p>

	        </div>
	      </div>

	      <div class="col-3">
	      	<div>
	      		<span class="badge bg-primary">${recordList[index].stringDistance}</span>
	      	</div>
	      	
	      	<div class="recordImageContainer">
	      		${recordList[index].imageName && recordList[index].imageName.length > 0 ? 
	      		recordList[index].imageName.slice(0, 1).map(image =>
	      		`<img src="/user/rest/thumbnail/${image.imageTagText}" class="img-fluid rounded-start" alt="기록 사진"/>`) : 
	      		`<img src="/user/rest/thumbnail/default.jpg" class="img-fluid rounded-start" alt="기본 사진"/>`}
	      	</div>
	      </div><!-- 이미지 부분-->
	      
	    </div>
	  </div> <!-- card -->
	  `;
}// recordListElement: 기록 html

const simpleRecordElement = (index) => {
	return `
	        <div class="card simpleRecord" data-index=${index} >
	            <div class="row g-0">
	                <div class="col-9">
	                    <div class="card-body p-1">
	                        <div class="row">
	                            <h5 class="card-title col-9 fw-bold ellipsis fs-6">${recordList[index].recordTitle}</h5>
	                            <span class="col-3">
	                            	<img src="/user/rest/emoji/${recordList[index].categoryImoji}" alt="Category Imoji" 
	                            	onclick = "categoryClick(${recordList[index].categoryNo})" class="recordEmoji"/>
	                            </span>
	                        </div>
	                        <p class="card-text">${recordList[index].recordAddDate}</p>
	                        <p class="card-text">${recordList[index].checkpointAddress}</p>
	                        <p class="card-text">
	                            ${recordList[index].hashtag && recordList[index].hashtag.length > 0 ? recordList[index].hashtag.map(tag => `
	                             <a href="#"> <small class="text-primary hashTag">${tag.imageTagText}</small></a>`).join('') : ''}
	                        </p>
	                        
	                    </div>
	                </div>
	                <div class="col-3">
	                	<div>
	                		<span class="badge bg-primary">${recordList[index].markerTypeString}</span>
	                		<span class="badge bg-primary">${recordList[index].stringDistance}</span>
	                	</div>
	                	
	                	<div class="recordImageContainer">
	      					${recordList[index].imageName && recordList[index].imageName.length > 0 ? 
	      					recordList[index].imageName.slice(0, 1).map(image =>
	      					`<img src="/user/rest/thumbnail/${image.imageTagText}" class="img-fluid rounded-start" alt="기록 사진"/>`) : 
	      					`<img src="/user/rest/thumbnail/default.jpg" class="img-fluid rounded-start" alt="기본 사진"/>`}
	      				</div>
	                    
	                    <div class="profileImageContainer">
	                    	<img class="rounded-image" src="/user/rest/profile/${recordList[index].profileImageName}" />
	                    	<span class="">${recordList[index].nickName}</span>
	                        ${recordList[index].isSubscribed ? `<img src="/user/rest/profile/sub.png"/>` : ''}
	                    </div>
	                </div>
	            </div>
	            
	            <div class="routeButtonGroup">
	        	<button id="mainButton" class="btn btn-primary">경로</button>
    				<div class="routeAdditionalButtons">
      					<button class="btn btn-primary" onclick="drawRoute('1')"><i class="fas fa-walking"></i></button>
	        			<button class="btn btn-primary" onclick="drawRoute('2')"><i class="fas fa-car"></i></button>
	        			<button class="btn btn-primary" onclick="drawTransitRoute()"><i class="fas fa-bus"></i></button>
 					</div>
	        	</div>
	    `;
}

const detailRecordElement = (index) => {
	return `
	        <div class="card detailRecord" data-index=${index}>
	            <div class="row g-0">
	                <div class="col-9">
	                    <div class="card-body p-1">
	                        <div class="row">
	                            <h5 class="card-title col-9 fw-bold fs-6">${recordList[index].recordTitle}</h5>
	                            <span class="col-3">
	                            	<img src="/user/rest/emoji/${recordList[index].categoryImoji}" alt="Category Imoji"
	                            	onclick = "categoryClick(${recordList[index].categoryNo})" class="recordEmoji"/>
	                            </span>
	                        </div>
	                        <p class="card-text">${recordList[index].recordAddDate}</p>
	                        <p class="card-text">${recordList[index].checkpointAddress}</p>
	                        <div class="container mt-5">
    							<div class="media">
    								<video width="320" height="240" controls class="mr-3">
        								<source src="${recordList[index].mediaName}" type="video/mp4">
      								</video>
      								
    							</div>
  							</div>
  							
	                        <p class="recordImageContainer">
	                        	<div class="container mt-5">
        							<div id="carouselExample" class="carousel slide" data-bs-ride="carousel">
            							<div class="carousel-inner">
            							${recordList[index].imageName.map(image => 
            							` <div class="carousel-item active">
            								<img src="/user/rest/thumbnail/${image.imageTagText}" class="d-block w-100"/>
                						   </div>
            								`).join('')}
            							</div>
            							
            					<button class="carousel-control-prev" type="button" data-bs-target="#carouselExample" data-bs-slide="prev">
                					<span class="carousel-control-prev-icon" aria-hidden="true"></span>
                					<span class="visually-hidden">이전</span>
            					</button>
            					
            					<button class="carousel-control-next" type="button" data-bs-target="#carouselExample" data-bs-slide="next">
                					<span class="carousel-control-next-icon" aria-hidden="true"></span>
                					<span class="visually-hidden">다음</span>
            					</button>
        						</div>
    						</div>
    						
	                        <!--<div>
	                        	${recordList[index].imageName.map(image => `<img src="/user/rest/thumbnail/${image.imageTagText}"/>`).join('')}
	                        </div>-->
	                        </p>
	                        <p class="card-text">
	                            ${recordList[index].hashtag && recordList[index].hashtag.length > 0 ? recordList[index].hashtag.map(tag => `
	                             <a href="#"> <small class="text-primary hashTag">${tag.imageTagText}</small></a>`).join('') : ''}
	                        </p>
	                        <p class="card-text">${recordList[index].recordText}</p>

	                    </div>
	                </div>
	                <div class="col-3">
	                	<div>
	                		<span class="badge bg-primary">${recordList[index].markerTypeString}</span>
	                		<span class="badge bg-primary">${recordList[index].stringDistance}</span>
	                	</div>
                        
                        <div class="profileImageContainer">
	                    	<img class="rounded-image" src="/user/rest/profile/${recordList[index].profileImageName}" />
	                    	<span class="">${recordList[index].nickName}</span>
	                        ${recordList[index].isSubscribed ? `<img src="/user/rest/profile/sub.png"/>` : ''}</span>
	                    </div>
	                </div>
	            </div>
	            
	            <div class="routeButtonGroup">
	        	<button id="mainButton" class="btn btn-primary">경로</button>
    				<div class="routeAdditionalButtons">
      					<button class="btn btn-primary" onclick="drawRoute('1')"><i class="fas fa-walking"></i></button>
	        			<button class="btn btn-primary" onclick="drawRoute('2')"><i class="fas fa-car"></i></button>
	        			<button class="btn btn-primary" onclick="drawTransitRoute()"><i class="fas fa-bus"></i></button>
 					</div>
	        	</div>
	        </div>
	    `;
}

const recommendListElement = (index) => {
	return `<div class="place" onClick='clickMarkerFromCard( ${JSON.stringify(index)}, placeList )'>
    				<h2>  ${placeList[index].placeName} </h2>
    				<p>Category: ${placeList[index].categoryName} </p>
    				<p>Address: ${placeList[index].addressName} </p>
    			</div>
    			`;
}// recommendListElement: 추천 장소 리스트

const detailPlaceElement = (index) => {
	return `
	        <div class="card detailPlace" data-index=${index}>
	            <div class="g-0">
	                    <div class="card-body">
	                        <div class="row">
	                            <h5 class="card-title fw-bold">${placeList[index].placeName}</h5>
	                        </div>
	                        <p class="card-text">${placeList[index].categoryName}</p>
	                        <p class="card-text">${placeList[index].addressName}</p>
    						<p class="card-text">${placeList[index].phone} </p> 
    						<p class="card-text"><a href=${placeList[index].placeUrl}>More Info</a></p>
	                        
	                </div>
	            </div>
	        </div>
	        <button class="btn btn-primary" onclick="drawRoute('1')"><i class="fas fa-walking"></i></button>
	        <button class="btn btn-primary" onclick="drawRoute('2')"><i class="fas fa-car"></i></button>
	        <button class="btn btn-primary" onclick="drawTransitRoute()"><i class="fas fa-bus"></i></button>
	    `;
}
	
const routeListElement = (response) => {
	return `
		<span class="badge bg-primary">총 거리: ${(response.totalDistance / 1000 ).toFixed(1)} km</span>
		<span class="badge bg-primary">총 시간: ${(response.totalTime / 60) < 60 ? (response.totalTime / 60).toFixed(0) +'분' : 
  						( (response.totalTime / 60) / 60).toFixed(0) + '시간 ' +  ( (response.totalTime / 60) % 60).toFixed(0) +'분'
  					 }</span>
		<div class="list-group">
			${response.description.map((item, index) =>
  			`<div class="list-group-item route-guide list-group-flush">${index == 0 ? '출발지에서 ' : ''} ${item} </div>`
			).join('')}
		</div>
`;
}

const transitRouteListElement = (paths) => {
	return `
		${paths.map( (path, index)  => 
		`<div class="list-group-item list-group-item-action">
			<ul class="list-group list-group-flush">
  				<li class="transitRoute list-group-item justify-content-between align-items-center" data-index =${index}>
  					<h1>${(path.totalTime / 60) < 60 ? (path.totalTime / 60).toFixed(0) +'분' : 
  						( (path.totalTime / 60) / 60).toFixed(0) + '시간 ' +  ( (path.totalTime / 60) % 60).toFixed(0) +'분'
  					 }</h1>
    				<span>도보 ${(path.totalWalkTime/60).toFixed(0) }분</span>
    				<span>환승 ${path.transferCount}회</span>
    				<span>요금 ${path.totalFare}원</span>
    				<span>거리 ${(path.totalDistance / 1000 ).toFixed(1)}km</span>
  				</li>
			</ul>
		</div>`
		).join('')}
		`;
}

const transitRouteDescriptionElement = (path) => {
	return `
		<div class="list-group list-group-flush">
		${path.routes.map((route) => `
			<div class="transitRoutePath list-group-item">
				${route.mode === 'WALK' ? route.endName + '까지 도보로 이동' : route.startName + '에서 ' + route.routeName + ' 승차 후 ' + route.endName + ' 하차'}
			 </div>
		`).join('')}
		</div>
		`;
}