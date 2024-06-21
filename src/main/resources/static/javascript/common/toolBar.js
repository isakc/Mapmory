$("a[name='신고된사용자목록']").on("click", function() {
	const userId = $(this).attr("value");
	window.location.href=`/community/getUserReportList/${userId}`;
});

$("a[name='신고된사용자목록']").css("cursor","pointer");