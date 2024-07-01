/**
 * 
 */

const preloadImage = (src, callback) => {
	const img = new Image();
	img.src = src;
	img.onload = () => callback(null, src);
	img.onerror = () => callback(new Error('Image load error'));
};

const recordListSwipe = (index) => {
	if (swiper) {
        swiper.destroy(true, true);
    }
    
	swiper = new Swiper('.swiper-container', {
        slidesPerView: 1,
        spaceBetween: 10,
        pagination: {
            el: '.swiper-pagination',
            clickable: true,
        },
        observer: true,
        observeParents: true,
        watchOverflow: true,
        on: {
			slideChange: function () {
				let visibleIndex = this.activeIndex;
				let realIndex = 0;
				
				$("#swiper-wrapper .resultListItem").each(function(i) {
					if ($(this).is(":visible")) {
						if (realIndex === visibleIndex) {
							navigateToMarkerOnSelect(i, recordList, visibleIndex);
							return false; // 루프 종료
						}
						realIndex++;
					}
				});
			}
		}
    });
    
	const categoryImgSrc = recordList[index].categoryImoji != null ? `/user/rest/emoji/${recordList[index].categoryImoji}` : '';
	const recordImgSrc = recordList[index].imageName && recordList[index].imageName.length > 0 ? 
		`/user/rest/thumbnail/${recordList[index].imageName[0].imageTagText}` : 
		'';
	const htmlString = `
		<div class="swiper-slide card border-0 container resultListItem" data-marker-type=${recordList[index].markerType}>

	    	<div class="row g-0">
	      		<div class="col-9 card-body p-1">
	            	<div class="mb-2">
	                	<h5 class="card-title fw-bold ellipsis fs-3">${recordList[index].recordTitle}</h5>
	            	</div><!-- 제목 -->
	            
	      			<div>
	      				${recordList[index].categoryImoji != null ? 
	      				`<img id="category-img-${index}" src="https://via.placeholder.com/150?text=Loading..." alt="Category Imoji"
	    				class="recordEmoji mr-3 rounded-image" data-categoryNo ="${recordList[index].categoryNo}"/>` : ''}
	    		
	    				<span class="badge bg-primary">${recordList[index].stringDistance}</span>
	      			</div><!-- 이모지 + 거리 -->
	      			
	          		<p class="card-text">
        				${recordList[index].hashtag && recordList[index].hashtag.length > 0 ? recordList[index].hashtag.map(tag => `
         				<a href="#"> <small class="text-primary hashTag">${tag.imageTagText}</small></a>`).join('') : ''}
    				</p><!-- 해시태그 -->
	      	
	          		<p class="card-text text-muted mt-3"><i class="fas fa-calendar"></i> ${recordList[index].recordAddDate}</p><!-- 날짜 -->
	          		
	      		</div><!-- 본문 중간부분 col-9 -->
	      
	      	<div class="col-3">
	      		<div class="recordImageContainer">
	      		
	      			${recordList[index].imageName && recordList[index].imageName.length > 0 ?
	      			`<img id="record-img-${index}" src="https://via.placeholder.com/150?text=Loading..." class="img-fluid rounded-start" alt="기록 사진"/>`:
					''}
	      		</div>
	      	</div><!-- 사진 부분 col-3 -->
	     </div><!--row-->
	  </div><!-- card -->
	`;
	
	const htmlElement = $(htmlString);
	
	preloadImage(categoryImgSrc, (err, src) => {
		const categoryImgElement = htmlElement.find(`#category-img-${index}`);
		categoryImgElement.attr('src', err ? categoryImgSrc : src);
  	});

	preloadImage(recordImgSrc, (err, src) => {
    	const recordImgElement = htmlElement.find(`#record-img-${index}`);
    	recordImgElement.attr('src', err ? recordImgSrc : src);
  	});
  
	swiper.appendSlide(htmlElement);
};

const recordListElement = (index) => {
	const profileImgSrc = `/user/rest/profile/${recordList[index].profileImageName}`;
	const subImgSrc = `/user/rest/profile/sub.png`;
	const categoryImgSrc = recordList[index].categoryImoji != null ? `/user/rest/emoji/${recordList[index].categoryImoji}` : '';
	const recordImgSrc = recordList[index].imageName && recordList[index].imageName.length > 0 ? 
		`/user/rest/thumbnail/${recordList[index].imageName[0].imageTagText}` : 
		'';
	const htmlString = `
		<div class="card border-0 border-bottom mb-3 container resultListItem" data-marker-type=${recordList[index].markerType}>
		
			<!--<div class="profileImageContainer">
				<img id="profileImg-${index}" class="rounded-image" src="https://via.placeholder.com/150?text=Loading..." />
	        	<span class="fs-5 ">${recordList[index].nickName}</span>
	        	${recordList[index].subscribed ? `<img src="https://via.placeholder.com/150?text=Loading..." class="rounded-image subImage"/>` : ''}
			</div> 프로필 상자 -->

	    	<div class="row g-0">
	      		<div class="col-9 card-body p-1">
	            	<div class="mb-2">
	                	<h5 class="card-title fw-bold ellipsis fs-3">${recordList[index].recordTitle}</h5>
	            	</div><!-- 제목 -->
	            
	      			<div>    
	      				${recordList[index].categoryImoji != null ? 
	      				`<img id="category-img-${index}" src="https://via.placeholder.com/150?text=Loading..." alt="Category Imoji"
	    				class="recordEmoji mr-3 rounded-image" data-categoryNo ="${recordList[index].categoryNo}"/>` : ''}
	    		
	    				<span class="badge bg-primary">${recordList[index].stringDistance}</span>
	      			</div><!-- 이모지 + 거리 -->
	      			
	          		<p class="card-text">
        				${recordList[index].hashtag && recordList[index].hashtag.length > 0 ? recordList[index].hashtag.map(tag => `
         				<a href="#"> <small class="text-primary hashTag">${tag.imageTagText}</small></a>`).join('') : ''}
    				</p><!-- 해시태그 -->
	      	
	          		<p class="card-text text-muted mt-3"><i class="fas fa-calendar"></i> ${recordList[index].recordAddDate}</p><!-- 날짜 -->
	          		
	      		</div><!-- 본문 중간부분 col-9 -->
	      
	      	<div class="col-3">
	      		<div class="recordImageContainer">
	      			${recordList[index].imageName && recordList[index].imageName.length > 0 ?
	      			`<img id="record-img-${index}" src="https://via.placeholder.com/150?text=Loading..." class="img-fluid rounded-start" alt="기록 사진"/>`:
					''}
	      		</div>
	      	</div><!-- 사진 부분 col-3 -->
	     </div><!--row-->
	  </div><!-- card -->
	`;

	const htmlElement = $(htmlString);
	
	preloadImage(categoryImgSrc, (err, src) => {
		const categoryImgElement = htmlElement.find(`#category-img-${index}`);
		categoryImgElement.attr('src', err ? categoryImgSrc : src);
  	});

	preloadImage(recordImgSrc, (err, src) => {
    	const recordImgElement = htmlElement.find(`#record-img-${index}`);
    	recordImgElement.attr('src', err ? recordImgSrc : src);
  	});
  		
  	preloadImage(subImgSrc, (err, src) => {
    	const subImgElement = htmlElement.find(`.subImage`);
    	subImgElement.attr('src', err ? subImgSrc : src);
  	});
  	
  	preloadImage(profileImgSrc, (err, src) => {
    	const profileImgElement = htmlElement.find(`#profileImg-${index}`);
    	profileImgElement.attr('src', err ? profileImgSrc : src);
  	});
  
  return htmlElement;
};

const simpleRecordElement = (index) => {
	const categoryImgSrc = recordList[index].categoryImoji != null ? `/user/rest/emoji/${recordList[index].categoryImoji}` : '';
	const recordImgSrc = recordList[index].imageName && recordList[index].imageName.length > 0 ? 
		`/user/rest/thumbnail/${recordList[index].imageName[0].imageTagText}` : 
		'';
	const profileImgSrc = `/user/rest/profile/${recordList[index].profileImageName}`;
	const subImgSrc = `/user/rest/profile/sub.png`;
	const htmlString = `
			<div class="d-flex justify-content-center align-items-center"  style="position: relative; width: 100%;">
				<i class="fas fa-arrow-left fs-3 simpleRecordBackButton" style="position: absolute; left: 0;"></i>
				<i class="fas fa-minus fs-3"></i>
			</div>
	        
	        <div class="card border-0 border-bottom mb-3 simpleRecord container" data-index=${index}>
	        
	        	<div class="row">
	        		<div class="profileImageContainer col-6">
						<img id="profileImg-${index}" class="rounded-image" src="/user/rest/profile/${recordList[index].profileImageName}" />
	        			<span class="fs-5 ">${recordList[index].nickName}</span>
	        			${recordList[index].subscribed ? `<img src="/user/rest/profile/sub.png" class="rounded-image subImage"/>` : ''}
					</div><!-- 프로필 상자 -->
					
					<div class="routeButtonGroup col-6 p-0 d-flex justify-content-end">

					<div class="routeAdditionalButtons bg-primary">
						<button class="btn btn-primary pedestrianRouteButton routeButton"><i class="fas fa-walking"></i></button>
						<button class="btn btn-primary carRouteButton routeButton"><i class="fas fa-car"></i></button>
						<button class="btn btn-primary transitRouteButton routeButton"><i class="fas fa-bus"></i></button>
					</div>

					<button id="routeButton" class="btn btn-primary"><i class="fas fa-directions"></i></button>

				</div>
			</div><!-- row -->
	        		
				<div class="row g-0">
	      			<div class="col-9 card-body p-1">
	            		<div class="mb-4 border-bottom">
	                		<h5 class="card-title fw-bold ellipsis fs-3">${recordList[index].recordTitle}</h5>
	            		</div><!-- 제목 -->
	            		
	      			<div class="border-0 border-bottom">    
	      				<!--<img src="/user/rest/emoji/${recordList[index].categoryImoji}" alt="Category Imoji"-->
	      				${recordList[index].categoryImoji != null ? 
	      				`<img id="category-img-${index}" src="https://via.placeholder.com/150?text=Loading..." alt="Category Imoji"
	    				class="recordEmoji mr-3 rounded-image" data-categoryNo ="${recordList[index].categoryNo}"/>` : ''}
	    		
	    				<span class="badge bg-primary">${recordList[index].stringDistance}</span>
	    				<span class="badge bg-primary">${recordList[index].markerTypeString}</span>
	      			</div><!-- 이모지 + 거리 -->
	      			
	      			<p class="card-text">
        				${recordList[index].hashtag && recordList[index].hashtag.length > 0 ? recordList[index].hashtag.map(tag => `
         				<a href="#"> <small class="text-primary hashTag">${tag.imageTagText}</small></a>`).join('') : ''}
    				</p><!-- 해시태그 -->
	      			
	      			<div class="mt-3">
	      				<p class="card-text text-muted border-bottom"><i class="fas fa-calendar"></i> ${recordList[index].recordAddDate}</p><!-- 날짜 -->
	      			</div>
	          		
	      			</div><!-- 본문 중간부분 col-9 -->
	      
	      		<div class="col-3">
	      			<div class="recordImageContainer">
	      				${recordList[index].imageName && recordList[index].imageName.length > 0 ?
	      			`<img id="record-img-${index}" src="https://via.placeholder.com/150?text=Loading..." class="img-fluid rounded-start" alt="기록 사진"/>`:
					''}
	      			</div>
	      		</div><!-- 사진 부분 col-3 -->
	     	</div><!--row-->
	     	
	    	<div class="mt-3">
	    		<p class="card-text fs-5 border-0"><i class="fas fa-map-marker-alt"></i> ${recordList[index].checkpointAddress}</p><!-- 주소 -->
	        </div>
	    `;
	    
	    const htmlElement = $(htmlString);
  
  		preloadImage(categoryImgSrc, (err, src) => {
    		const categoryImgElement = htmlElement.find(`.recordEmoji`);
    		categoryImgElement.attr('src', err ? categoryImgSrc : src);
  		});

  		preloadImage(recordImgSrc, (err, src) => {
    		const recordImgElement = htmlElement.find(`#record-img-${index}`);
    		recordImgElement.attr('src', err ? recordImgSrc : src);
  		});
  		
  		preloadImage(subImgSrc, (err, src) => {
    		const subImgElement = htmlElement.find(`.subImage`);
    		subImgElement.attr('src', err ? subImgSrc : src);
  		});
  		
  		preloadImage(profileImgSrc, (err, src) => {
    		const profileImgElement = htmlElement.find(`#profileImg-${index}`);
    		profileImgElement.attr('src', err ? profileImgSrc : src);
  		});
  
  return htmlElement;
}

const detailRecordElement = (index) => {
	const categoryImgSrc = recordList[index].categoryImoji != null ? `/user/rest/emoji/${recordList[index].categoryImoji}` : '';
	const profileImgSrc = `/user/rest/profile/${recordList[index].profileImageName}`;
	const subImgSrc = `/user/rest/profile/sub.png`;
	const videoId = `my-video-${index}`; // 각 비디오 요소에 고유한 ID를 부여
	const htmlString = `
			<div class="d-flex justify-content-center align-items-center"  style="position: relative; width: 100%;">
				<i class="fas fa-arrow-left fs-3 detailRecordBackButton" style="position: absolute; left: 0;" data-index=${index}></i>
				<i class="fas fa-minus fs-3"></i>
			</div>
	        
	        <div class="card border-0 border-bottom mb-3 detailRecord container" data-index=${index} >

	        	<div class="row">
	        		<div class="profileImageContainer col-6">
						<img id="profileImg-${index}" class="rounded-image" src="/user/rest/profile/${recordList[index].profileImageName}" />
	        			<span class="fs-5 ">${recordList[index].nickName}</span>
	        			${recordList[index].subscribed ? `<img src="/user/rest/profile/sub.png" class="rounded-image subImage"/>` : ''}
					</div><!-- 프로필 상자 -->
					
					<div class="routeButtonGroup col-6 p-0">
	        			<button id="routeButton" class="btn btn-primary"><i class="fas fa-directions"></i></button>
	        			
    					<div class="routeAdditionalButtons bg-primary">
      						<button class="btn btn-primary pedestrianRouteButton routeButton"><i class="fas fa-walking"></i></button>
	        				<button class="btn btn-primary carRouteButton routeButton"><i class="fas fa-car"></i></button>
	        				<button class="btn btn-primary transitRouteButton routeButton"><i class="fas fa-bus"></i></button>
	        			</div>
	        		</div>
	        	</div><!-- row -->
	        		
				<div class="row g-0">
	      			<div class="col-9 card-body p-1">
	            		<div class="mb-4 border-bottom">
	                		<h5 class="card-title fw-bold fs-3"> ${recordList[index].recordTitle}</h5>
	            		</div><!-- 제목 -->
	            		
	      			<div class="border-0 border-bottom">    
	      				${recordList[index].categoryImoji != null ? 
	      				`<img id="category-img-${index}" src="https://via.placeholder.com/150?text=Loading..." alt="Category Imoji"
	    				class="recordEmoji mr-3 rounded-image" data-categoryNo ="${recordList[index].categoryNo}"/>` : ''}
	    		
	    				<span class="badge bg-primary">${recordList[index].stringDistance}</span>
	    				<span class="badge bg-primary">${recordList[index].markerTypeString}</span>
	      			</div><!-- 이모지 + 거리 -->
	      			
	      			<p class="card-text">
        				${recordList[index].hashtag && recordList[index].hashtag.length > 0 ? recordList[index].hashtag.map(tag => `
         				<a href="#"> <small class="text-primary hashTag">${tag.imageTagText}</small></a>`).join('') : ''}
    				</p><!-- 해시태그 -->
	      			
	      			<div class="mt-3">
	      				<p class="card-text text-muted border-bottom"><i class="fas fa-calendar"></i> ${recordList[index].recordAddDate}</p><!-- 날짜 -->
	      			</div>
	          		
	          		<div class="mt-3">
	          			<p class="card-text fs-5 border-0"><i class="fas fa-map-marker-alt"></i> ${recordList[index].checkpointAddress}</p><!-- 주소 -->
	          		</div>
	          		
	      			</div><!-- 본문 중간부분 col-9 -->

				${recordList[index].mediaName != '' ?       		
	      		`<div class="mt-3">
	      			<video id="${videoId}" class="video-js vjs-default-skin" controls preload="auto" width="100%" height="100%" data-setup="{}" playsinline>
                		<source src="/timeline/rest/media/${recordList[index].mediaName}" type="video/mp4" />
                	</video>
                </div><!-- video -->`
                : ''}
                
                ${recordList[index].imageName && recordList[index].imageName.length > 0 ?
                `
                <div class="swiper mySwiper mt-3">
                	<div class="swiper-wrapper">
                		${recordList[index].imageName.map(image => 
                		` <div class="swiper-slide">
                    		<img src="/user/rest/thumbnail/${image.imageTagText}" class="swiperImg"/>
                    	   </div>
                    `).join('')}
                	</div>
                	
                	<div class="swiper-pagination"></div>
                	<div class="swiper-scrollbar"></div>
                </div>
                `
                :'' }
        		
    			<p class="card-text mt-3">${recordList[index].recordText}</p><!-- 기록 텍스트 -->
	    `;
	    
	    const htmlElement = $(htmlString);
	    
	    preloadImage(categoryImgSrc, (err, src) => {
    		const categoryImgElement = htmlElement.find(`.recordEmoji`);
    		categoryImgElement.attr('src', err ? categoryImgSrc : src);
  		});
  		
  		recordList[index].imageName.map( (image, index) =>
  			preloadImage("/user/rest/thumbnail/"+ image.imageTagText, (err, src) => {
    			const recordImgElement = htmlElement.find(`.swiperImg`)[index];
    			$(recordImgElement).attr('src', err ? "/user/rest/thumbnail/"+image.imageTagText : src);
  			})
  		);
  		
  		preloadImage(subImgSrc, (err, src) => {
    		const subImgElement = htmlElement.find(`.subImage`);
    		subImgElement.attr('src', err ? subImgSrc : src);
  		});
  		
  		preloadImage(profileImgSrc, (err, src) => {
    		const profileImgElement = htmlElement.find(`#profileImg`);
    		profileImgElement.attr('src', err ? profileImgSrc : src);
  		});
  		
  		recordList[index].mediaName != '' ?
  		setTimeout(() => {
        	new Swiper(htmlElement.find('.mySwiper')[0], {
            	direction: 'horizontal', // 가로 방향 슬라이드
            	loop: true,
            	slidesPerView: 1, // 한 번에 1개 슬라이드만 보이도록 설정
            	spaceBetween: 10,
            
            	pagination: {
                	el: '.swiper-pagination',
                	clickable: true,
            	},
            	scrollbar: {
                	el: '.swiper-scrollbar',
            	},
        	});
        	
        	        	videojs(videoId);
    	}, 0)
    	: '';
    	
  		return htmlElement;
}

const placeListSwipe = (index) => {
	if (swiper) {
		swiper.destroy(true, true);
	}

	swiper = new Swiper('.swiper-container', {
		slidesPerView: 1,
		spaceBetween: 10,
		pagination: {
			el: '.swiper-pagination',
			clickable: true,
		},
		watchOverflow: true,
		on: {
			slideChange: function() {
				let index = this.activeIndex;

				navigateToMarkerOnSelect(index, placeList, index);
			}
		}
	});

	const slideHtml = `
		<div class="swiper-slide card border-0 mb-3 place placeListItem">
			<h2 class="card-title fw-bold ellipsis fs-3">${placeList[index].placeName} </h2>
			<p class="card-text">${placeList[index].categoryName} </p>
			<p class="card-text fs-5">${placeList[index].addressName} </p>
		</div>
	`;

	swiper.appendSlide(slideHtml);
};


const
	recommendListElement = (index) => {
		return `<div class="card border-0 border-bottom mb-3 place placeListItem">
					<h2 class="card-title fw-bold ellipsis fs-3">${placeList[index].placeName}</h2>
					<p class="card-text">${placeList[index].categoryName} </p>
					<p class="card-text fs-5">${placeList[index].addressName} </p>
				</div>
	`;
	}// recommendListElement: 추천 장소 리스트

const detailPlaceElement = (index) => {
	return `
		<div class="d-flex justify-content-center align-items-center" style="position: relative; width: 100%;">
			<i class="fas fa-arrow-left fs-3 simpleRecordBackButton" style="position: absolute; left: 0;"></i>
			<i class="fas fa-minus fs-3"></i>
		</div>

		<div class="card border-0 border-bottom mb-3 container" data-index=${index}>
			<div class="row">
				<div class="col-6"></div>

				<div class="routeButtonGroup col-6 p-0 d-flex justify-content-end">
					<div class="routeAdditionalButtons bg-primary">
						<button class="btn btn-primary pedestrianRouteButton routeButton"><i class="fas fa-walking"></i></button>
						<button class="btn btn-primary carRouteButton routeButton"><i class="fas fa-car"></i></button>
						<button class="btn btn-primary transitRouteButton routeButton"><i class="fas fa-bus"></i></button>
					</div>

					<button id="routeButton" class="btn btn-primary"><i class="fas fa-directions"></i></button>
				</div>
			</div><!-- row -->

			<div class="g-0">
				<div class="card-body">
					<h5 class="card-title fw-bold fs-3">${placeList[index].placeName}</h5>
					<p class="card-text"><i class="fas fa-list"></i> ${placeList[index].categoryName}</p>
					<p class="card-text"><i class="fas fa-map-marker-alt"></i> ${placeList[index].addressName}</p>
					<p class="card-text"><i class="fas fa-phone"></i> ${placeList[index].phone} </p>
					<p class="card-text"><a href=${placeList[index].placeUrl}>More Info</a></p>

				</div>
			</div>
		</div>
	`;
}
	
	
const routeListElement = (response) => {
	console.log(response);
	
	return `
		<div class="btn-secondary-custom">
			<div class="text-center text-light fs-4">
				<span>${response.startEndAddressName[0]} 
			</div>
			<div class="text-center text-light fs-5">
				<i class="fas fa-arrow-down"></i>
			</div>
			<div class="text-center text-light border-bottom fs-4">
				<span>${response.startEndAddressName[1]} 
			</div>
			
			<div class="routeButtonGroup d-flex">
    			<div class="d-flex justify-content-center w-100">
      				<button class="btn text-light mx-2" onclick="drawRoute('1')"><i class="fas fa-walking fs-4"></i></button>
	        		<button class="btn text-light mx-2" onclick="drawRoute('2')"><i class="fas fa-car fs-4"></i></button>
	        		<button class="btn text-light mx-2" onclick="drawTransitRoute()"><i class="fas fa-bus fs-4"></i></button>
	        	</div>
	        	
	        	<div class="ml-auto">
 					<button class="btn text-light" onClick="showDescription()" id="descriptionBtn"><i class="fas fa-times fs-4"></i></button>
 				</div>
	        </div>
		</div>
		
		<div class="border-bottom mb-3 p-3">
			<span class="fs-3 fw-bold">${(response.totalTime / 60) < 60 ? (response.totalTime / 60).toFixed(0) +'분' : 
  						( (response.totalTime / 60) / 60).toFixed(0) + '시간 ' +  ( (response.totalTime / 60) % 60).toFixed(0) +'분'
  					 }</span>
			<span class="fs-6">${(response.totalDistance / 1000 ).toFixed(1)} km</span>
		</div>
		
		<ul class="list-group">
			${response.description.map((item, index) =>
  			`<li class="list-group-item border-0 border-bottom">${index == 0 ? '출발지에서 ' : ''} ${item} </li>`
			).join('')}
		</ul>
`;
}

const transitRouteListElement = (paths) => {
	return `
		<div class="btn-secondary-custom">
			<div class="text-center text-light fs-4">
				<span>${paths[0].startEndAddressName[0]} 
			</div>
			<div class="text-center text-light fs-5">
				<i class="fas fa-arrow-down"></i>
			</div>
			<div class="text-center text-light border-bottom fs-4">
				<span>${paths[0].startEndAddressName[1]} 
			</div>
			
			<div class="routeButtonGroup d-flex">
    			<div class="d-flex justify-content-center w-100">
      				<button class="btn text-light mx-2" onclick="drawRoute('1')"><i class="fas fa-walking fs-4"></i></button>
	        		<button class="btn text-light mx-2" onclick="drawRoute('2')"><i class="fas fa-car fs-4"></i></button>
	        		<button class="btn text-light mx-2" onclick="drawTransitRoute()"><i class="fas fa-bus fs-4"></i></button>
	        	</div>
	        	
	        	<div class="ml-auto">
 					<button class="btn text-light" onClick="showDescription()" id="descriptionBtn"><i class="fas fa-arrow-left fs-4"></i></button>
 				</div>
	        </div>
		</div>
		${paths.map( (path, index)  => 
		`<div class="list-group-item list-group-item-action">
			<ul class="list-group">
  				<li class="transitRoute list-group-item justify-content-between align-items-center border-0" data-index =${index}>
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