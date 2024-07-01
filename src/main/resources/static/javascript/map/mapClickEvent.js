/**
 * 
 */

$(".fa-search").on('click', function() {
	searchRecord();
});//검색어 버튼 눌렀을 경우 검색

$(document).on('click', '.hashTag', function(event) {
	event.stopPropagation();
	$('input[name="searchKeyword"]').val($(this).text());
	searchRecord();
});// 해시태그를 클릭했을 때 검색

$(document).on("click", ".recordEmoji", function(event) {
	event.stopPropagation();
	$('input[name="categoryNo"]').val($(this).data('categoryno'));
	searchRecord();
});// 이모지를 클릭했을 때 검색

$(document).on('click', '.transitRoute', function() {
	$(".transitRoute .list-group").empty(); // 기존에 있던 설명 지우기

	if ($(this).hasClass('active')) {
		$(this).removeClass('active');
	} else {
		$(".transitRoute").removeClass('active');
		$(this).addClass('active');
		$(this).append(showPathDetails($(this).data('index')));
	}
});// 대중교통에서 해당 경로를 선택할 경우


/*$('#private, #public, #follow, input[name="radius"]').on('change', function(event) {
	event.preventDefault();
	searchRecord();
});// private, public, follow, 반경 변경 시 다시 검색*/

$('input[name="radius"]').on('change', function(event) {
	event.preventDefault();
	searchRecord();
});// 반경 변경 시 다시 검색

$('#private, #public, #follow').on('change', function(event) {
	event.preventDefault();	
	let tempMarkers = markers;
	let privateValue = parseInt($("#private").val(), 10);
	let publicValue = parseInt($("#public").val(), 10);
	let followValue = parseInt($("#follow").val(), 10);
	
	if(recordList.length != 0){
		
		if( !($("#private").prop('checked')) ){
			tempMarkers = tempMarkers.filter(marker=>marker.locationData.markerType !== privateValue);
		}
		
		if( !($("#public").prop('checked')) ){
			tempMarkers = tempMarkers.filter(marker=>marker.locationData.markerType !== publicValue);
		}
		
		if( !($("#follow").prop('checked')) ){
			tempMarkers = tempMarkers.filter(marker=>marker.locationData.markerType !== followValue);
		}
	}
	
	hideMarkers();
	
	tempMarkers.forEach(marker => {
		marker.setMap(map);
	});
	
	$("#result .resultListItem").each(function() {
		const markerType = $(this).data('marker-type');
		let show = true;

		if (markerType == privateValue && !$("#private").prop('checked')) {
			show = false;
		}
		if (markerType == publicValue && !$("#public").prop('checked')) {
			show = false;
		}
		if (markerType == followValue && !$("#follow").prop('checked')) {
			show = false;
		}

		if (show) {
			$(this).show();
		} else {
			$(this).hide();
		}
	});
	
	$("#swiper-wrapper .resultListItem").each(function() {
		const markerType = $(this).data('marker-type');
		let show = true;

		if (markerType == privateValue && !$("#private").prop('checked')) {
			show = false;
		}
		if (markerType == publicValue && !$("#public").prop('checked')) {
			show = false;
		}
		if (markerType == followValue && !$("#follow").prop('checked')) {
			show = false;
		}

		if (show) {
			$(this).show();
		} else {
			$(this).hide();
		}
	});
	swiper.update(); // 슬라이드가 다시 보이도록 업데이트
});// private, public, follow, 변경 시 지도와 리스트에서 제거



$(document).on("click", "#routeButton", function(event) {
	event.stopPropagation();
	console.log("dd");
	$('.routeAdditionalButtons').css('visibility', function(i, visibility) {
		return (visibility === 'visible') ? 'hidden' : 'visible';
	});
}); // 경로클릭 시 도보, 자동차, 대중교통 버튼 보이기

$(document).on("click", ".routeButton", function(event) {
	event.stopPropagation();

	if ($(this).hasClass('pedestrianRouteButton')) {
		drawRoute(1);
	} else if ($(this).hasClass('carRouteButton')) {
		drawRoute(2);
	} else if ($(this).hasClass('transitRouteButton')) {
		drawTransitRoute();
	}
});// 도보, 자동차, 대중교통 버튼 눌렀을 경우

$(document).on("click", ".simpleRecordBackButton", function() {
	showResult();
}); // 간단보기에서 뒤로 눌렀을 때 result보이기

$(document).on("click", ".simpleRecord .row", function() {
	$(".infoItem").removeClass('on');
	description.addClass('on');
	deleteDescription();
	description.append(detailRecordElement($(this).parent().data('index')));
}); // 간단 보기 눌렀을 때 상세 보기로 이동

$(document).on("click", ".detailRecordBackButton", function() {
	deleteDescription();
	description.append(simpleRecordElement($(this).data('index')));
}); // 상세 보기 뒤로가기 버튼 눌렀을 때 간단 보기로 이동

$(document).on("click", ".detailRecord", function() {
	deleteDescription();
	description.append(simpleRecordElement($(this).data('index')));
}); // 상세 보기 div 눌렀을 때 간단 보기로 이동

$(document).on("click", "#result .resultListItem", function() {
	clickContentMarker($(this).index('#result .resultListItem'), recordList);
}); // list 기록 검색결과 list에서 눌렀을 경우

$(document).on("click", "#swiper-wrapper .resultListItem", function() {
	clickContentMarker($(this).index('#swiper-wrapper .resultListItem'), recordList);
}); // swiper 기록 검색결과 list에서 눌렀을 경우

$(document).on("click", "#result .placeListItem", function() {
	clickContentMarker($(this).index('#result .placeListItem'), placeList);
})//추천 검색결과 list에서 눌렀을 경우

$(document).on("click", "#swiper-wrapper .placeListItem", function() {
	clickContentMarker($(this).index('#swiper-wrapper .placeListItem'), placeList);
}); // swiper 추천 검색결과 list에서 눌렀을 경우

$(document).on('click', '.mySwiper', function(event) {
	event.stopPropagation(); // 이벤트 전파 중지
});// swipe할 때 들어가는 클릭 현상 방지

$(document).on('click', '.video-js', function(event) {
	event.stopPropagation();
});// 상세보기에서 비디오 재생 누른 경우 다시 돌아가는 것 막기

$(document).on('click', function(event) {
	if (!$(event.target).closest('.searchKeywordBox').length) {
		$('input[name="searchKeyword"]').blur();
	}
});// input 요소 이외의 영역을 클릭하면 포커스를 해제

$(document).on('click', function(event) {
	if (!$(event.target).closest('#searchKeyword').length) {
		$('#suggestions').hide();
	}
}); // 입력 필드 밖을 클릭하면 추천 검색어 숨기기

$('#suggestions').on('click', 'div', function() {
	$('input[name="searchKeyword"]').val($(this).text());
	$('#suggestions').hide();
	searchRecord();
}); //추천 검색어 클릭하면 검색

$('#toggle-switch').click(function() {
	$(this).toggleClass('active');
	

	if ($(this).hasClass('active')) {
		$(this).attr('title', '추천');
		checkboxGroup.css('display', 'none');
		searchBoxGroup.css('display', 'none');
		mapFilterButton.hide();
		getRecommendPlace();
	} else {
		$(this).attr('title', '기록');
		checkboxGroup.css('display', 'inline-block');
		searchBoxGroup.css('display', 'block');
		mapFilterButton.show();
		searchRecord();
	}
});// 토글 스위치

refreshButton.on('click', function() {
	if (searchBoxGroup.css('display') === 'none') {
		getRecommendPlace();
	} else {
		location.reload();
	}
});// 재검색 버튼 클릭시

kakao.maps.event.addListener(map, 'click', function() {
	hideResultDivs();

	$(".mapButton").removeClass('on');
	if (recordList.length != 0 || placeList.length != 0) {
		resultDivsBtn.addClass('on');
	}

});// 맵을 클릭한 경우 해제 시키기

listBtn.on('click', function(){
	
	if( $(".swiper-container").hasClass('on')){
		$(".infoItem").removeClass('on');
		result.addClass('on');
		listBtn.addClass('list');
		listBtn.text("지도에서 보기");
	}else{
		$(".infoItem").removeClass('on');
		$(".swiper-container").addClass('on');
		listBtn.removeClass('list');
		listBtn.text("리스트로 보기");
	}
}) //리스트로 보기, 넘기기로 보기 토글