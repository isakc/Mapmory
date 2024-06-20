/**
 * 
 */

const recordElement = (index) => {
	return `
		<div class="card" onClick='clickMarkerFromCard( ${JSON.stringify(index)}, recordList )'>
	    <div class="row g-0">
	      <div class="col-9">
	        <div class="card-body">

	            <div class="row">
	                <h5 class="card-title col-8 fw-bold">${recordList[index].recordTitle}</h5>
	                <span class="col-4"> <img src="${recordList[index].categoryImoji}" alt="Category Imoji" /> </span>
	            </div>

	          <p class="card-text">${recordList[index].recordAddDate}</p>
	          <p class="card-text">
	          	${recordList[index].hashtag && recordList[index].hashtag.length > 0
			? recordList[index].hashtag.slice(0, 3).map(tag => `<a href="#">
            		<small class="text-primary" onclick="hasTagClick('${tag.imageTagText}')">${tag.imageTagText}</small></a>`).join('')
			: ''}
	          </p>

	        </div>
	      </div>

	      <div class="col-3">
	      	${`<img src="https://via.placeholder.com/150" class="img-fluid rounded-start" alt="기록 사진"/>`}
	      	<!--${`<img src="${recordList[index].imageName[0]}" class="img-fluid rounded-start" alt="기록 사진"/>`}-->
	      	<span class="">${recordList[index].stringDistance}</span>
	      </div><!-- 이미지 부분-->
	      
	    </div>
	  </div> <!-- card -->
	  `;
}// recordElement: 기록 html

const simpleRecordElement = (index) => {
	return `
	        <div class="card simpleRecord" data-index=${index} >
	            <div class="row g-0">
	                <div class="col-9">
	                    <div class="card-body">
	                        <div class="row">
	                            <h5 class="card-title col-8 fw-bold">${recordList[index].recordTitle}</h5>
	                            <span class="col-4"> <img src="${recordList[index].categoryImoji}" alt="Category Imoji" onclick= categoryClick(${recordList[index].categoryNo})/> </span>
	                        </div>
	                        <p class="card-text">${recordList[index].recordAddDate}</p>
	                        <p class="card-text">${recordList[index].checkpointAddress}</p>
	                        <p class="card-text">
	                            ${recordList[index].hashtag && recordList[index].hashtag.length > 0 ? recordList[index].hashtag.map(tag => `
	                             <a href="#"> <small class="text-primary" onclick="hasTagClick('${tag.imageTagText}')">${tag.imageTagText}</small></a>`).join('') : ''}
	                        </p>
	                        
	                    </div>
	                </div>
	                <div class="col-3">
	                	<span class="">${recordList[index].markerTypeString}</span>
	                    <img src="https://via.placeholder.com/150" class="img-fluid rounded-start" alt="기록 사진"/>
	                    <span class="">${recordList[index].stringDistance}</span>
	                    
	                    <div class="">
	                    <img class="rounded-image" src="/user/rest/profile/${recordList[index].profileImageName}" />
	                        <span class="">${recordList[index].nickName}</span>
	                        <span class="">${recordList[index].isSubscribed ? 'V' : 'X'}</span>
	                    </div>
	                </div>
	            </div>
	        </div>
	        <button class="btn btn-primary" onclick="drawRoute('1')"><i class="fas fa-walking"></i></button>
	        <button class="btn btn-primary" onclick="drawRoute('2')"><i class="fas fa-car"></i></button>
	        <button class="btn btn-primary" onclick="drawTransitRoute()"><i class="fas fa-bus"></i></button>
	    `;
}

const detailRecordElement = (index) => {
	return `
	        <div class="card detailRecord" data-index=${index}>
	            <div class="row g-0">
	                <div class="col-9">
	                    <div class="card-body">
	                        <div class="row">
	                            <h5 class="card-title col-8 fw-bold">${recordList[index].recordTitle}</h5>
	                            <span class="col-4"> <img src="${recordList[index].categoryImoji}" alt="Category Imoji" data-categoryNo=${recordList[index].categoryNo}/> </span>
	                        </div>
	                        <p class="card-text">${recordList[index].recordAddDate}</p>
	                        <p class="card-text">${recordList[index].checkpointAddress}</p>
	                        <p class="card-text">${recordList[index].mediaName}</p>
	                        <p class="card-text">
	                        	${recordList[index].imageName.map(image => `<img src="${image.imageTagText}"/>`).join('')}
	                        </p>
	                        <p class="card-text">
	                            ${recordList[index].hashtag && recordList[index].hashtag.length > 0 ? recordList[index].hashtag.map(tag => `
	                             <a href="#"> <small class="text-primary" onclick="hasTagClick('${tag.imageTagText}')">${tag.imageTagText}</small></a>`).join('') : ''}
	                        </p>
	                        <p class="card-text">${recordList[index].recordText}</p>

	                    </div>
	                </div>
	                <div class="col-3">
	                	<span class="">${recordList[index].markerTypeString}</span>
	                    <img src="https://via.placeholder.com/150" class="img-fluid rounded-start" alt="기록 사진"/>
	                    <span class="">${recordList[index].stringDistance}</span>
                        
                        <div class="">
                            <img class="rounded-image" src="/user/rest/profile/${recordList[index].profileImageName}" />
                            <span class="">${recordList[index].nickName}</span>
                            <span class="">${recordList[index].isSubscribed ? 'V' : 'X'}</span>
                        </div>
	                </div>
	            </div>
	        </div>
	        <button class="btn btn-primary" onclick="drawRoute('1')"><i class="fas fa-walking"></i></button>
	        <button class="btn btn-primary" onclick="drawRoute('2')"><i class="fas fa-car"></i></button>
	        <button class="btn btn-primary" onclick="drawTransitRoute()"><i class="fas fa-bus"></i></button>
	    `;
}

const recommendElement = (index) => {
	return `<div class="place" onClick='clickMarkerFromCard( ${JSON.stringify(index)}, placeList )'>
    				<h2>  ${placeList[index].placeName} </h2>
    				<p>Category: ${placeList[index].categoryName} </p>
    				<p>Address: ${placeList[index].addressName} </p>
    			</div>
    			`;
}// recommendElement: 추천 html

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