package com.mapmory.unit.timeline;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.mapmory.common.domain.Search;
import com.mapmory.services.timeline.domain.Category;
import com.mapmory.services.timeline.domain.ImageTag;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.dto.CountAddressDto;
import com.mapmory.services.timeline.service.TimelineService;

@SpringBootTest
public class TimelineServiceTest {

	@Autowired
	@Qualifier("timelineService")
	private TimelineService timelineService;
	
//	@Test
	@Transactional
	public void testAddTimeline() throws Exception{
		System.out.println("\n===================================");
		List<ImageTag> image= new ArrayList<ImageTag>();
		image.add(ImageTag.builder().imageTagText("A.png").build());
		image.add(ImageTag.builder().imageTagText("B.jpg").build());
		List<ImageTag> hash= new ArrayList<ImageTag>();
		hash.add(ImageTag.builder().imageTagText("#test").build());
		hash.add(ImageTag.builder().imageTagText("#test2").build());
		System.out.println("image : "+image);
		System.out.println("hash : "+hash);
		Record record = Record.builder()
				.recordUserId("user1")
				.recordTitle("test 기록")
				.latitude(37.1232322)
				.longitude(127.1233322)
				.checkpointAddress("중구남방")
				.mediaName("가.mp4")
				.imageName(image)
				.hashtag(hash)
				.categoryNo(0)
				.recordText("test 중")
				.tempType(0)
				.recordAddDate("2024-06-01 04:52:35")
				.sharedDate("2024-06-02 05:55:27")
				.updateCount(0)
				.d_DayDate(Date.valueOf("2024-06-02"))
				.timecapsuleType(0)
				.build();
		timelineService.addTimeline(record);
	}
	
	@Test
	@Transactional
	public void testUpdateTimeline() throws Exception{
		System.out.println("\n===================================");
		List<ImageTag> image= new ArrayList<ImageTag>();
		image.add(ImageTag.builder().imageTagText("A.png").build());
		image.add(ImageTag.builder().imageTagText("B.jpg").build());
		List<ImageTag> hash= new ArrayList<ImageTag>();
		hash.add(ImageTag.builder().imageTagText("#test").build());
		hash.add(ImageTag.builder().imageTagText("#test2").build());
		Record record = Record.builder()
				.recordNo(2)
				.recordUserId("user1")
				.recordTitle("update 기록")
				.latitude(37.1232322)
				.longitude(127.1233322)
				.checkpointAddress("변경된 동서남북")
				.checkpointDate("2013-06-13 12:22:30")
				.mediaName("가.mp4")
				.imageName(image)
				.hashtag(hash)
				.categoryNo(0)
				.recordText("update 완료")
				.tempType(0)
				.recordAddDate("2024-06-01 04:52:35")
				.sharedDate("2024-06-02 05:55:27")
				.updateCount(0)
				.d_DayDate(Date.valueOf("2024-06-02"))
				.timecapsuleType(0)
				.build();
		timelineService.updateTimeline(record);
	}
	
//	@Test
	@Transactional
	public void testDeleteTimeline() throws Exception{
		System.out.println("\n===================================");
		timelineService.deleteTimeline(91);
	}
	
//	@Test
//	@Transactional
//	public void testSelectImageForDelete() throws Exception{
//		System.out.println("\n===================================");
//		System.out.println(timelineService.getImageForDelete(1));
//	}
	
//	@Test
	@Transactional
	public void testDeleteImageNo() throws Exception{
		System.out.println("\n===================================");
		timelineService.deleteImage(1);
	}
	
//	@Test
	@Transactional
	public void testCategory() throws Exception{
		System.out.println("\n===================================");
		Category category=Category.builder()
				.categoryName("테스트")
				.categoryImoji("테스트.jpg")
				.build();
		timelineService.addCategory(category);
		System.out.println("timelineService.getCategory(15) : "+timelineService.getCategory(15));
		System.out.println("timelineService.getCategoryList() :"+timelineService.getCategoryList()); 
		
		category=Category.builder()
				.categoryNo(15)
				.categoryName("업데이트")
				.categoryImoji("업데이트.jpg")
				.build();
		timelineService.updateCategory(category);
		timelineService.deleteCategory(15);
	}
	
//	@Test
	@Transactional
	public void testRecordToUserId() throws Exception{
		System.out.println("\n===================================");
		Search search=Search.builder()
				.userId("user1")
				.currentPage(1)
				.limit(5)
				.sharedType(1)
				.tempType(1)
				.timecapsuleType(0)
				.build();
		System.out.println(timelineService.getTimelineList(search).size());
	}
	
//	@Test
	@Transactional
	public void testGetCountAddress() throws Exception{
		System.out.println("\n===================================");
		Record record = Record.builder()
				.recordUserId("user1")
				.checkpointAddress("서울시 강남구")
				.build();
		CountAddressDto c =timelineService.getCountAddress(record);
		
		System.out.println(c);

		Assertions.assertThat(c.getCheckpointCount()).isEqualTo(4);
		Assertions.assertThat(c.getCheckpointDate()).isEqualTo("2024-08-01 12:00:00");
		
	}
}
