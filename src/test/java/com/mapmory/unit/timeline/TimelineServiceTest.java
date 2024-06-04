package com.mapmory.unit.timeline;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.service.TimelineService;

@SpringBootTest
public class TimelineServiceTest {

	@Autowired
	@Qualifier("timelineService")
	private TimelineService timelineService;
	
//	@Test
//	@Transactional
	public void testAddTimeline() throws Exception{
		System.out.println("\n===================================");
		List<String> image= new ArrayList<String>();
		image.add("A.png");
		image.add("B.jpg");
		List<String> hash= new ArrayList<String>();
		hash.add("#test");
		hash.add("#test2");
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
				.recordAddDate(LocalDateTime.parse("2024-06-01T04:52:35"))
				.sharedDate(LocalDateTime.parse("2024-06-02T05:55:27"))
				.updateCount(0)
				.d_DayDate(Date.valueOf("2024-06-02"))
				.timecapsuleType(0)
				.build();
		timelineService.addTimeline(record);

	}
	
//	@Test
	@Transactional
	public void testUpdateTimeline() throws Exception{
		System.out.println("\n===================================");
		List<String> image= new ArrayList<String>();
		image.add("A.png");
		image.add("B.jpg");
		List<String> hash= new ArrayList<String>();
		hash.add("#test");
		hash.add("#test2");
		Record record = Record.builder()
				.recordNo(2)
				.recordUserId("user1")
				.recordTitle("update 기록")
				.latitude(37.1232322)
				.longitude(127.1233322)
				.checkpointAddress("변경된 동서남북")
				.mediaName("가.mp4")
				.imageName(image)
				.hashtag(hash)
				.categoryNo(0)
				.recordText("update 완료")
				.tempType(0)
				.recordAddDate(LocalDateTime.parse("2024-06-01T04:52:35"))
				.sharedDate(LocalDateTime.parse("2024-06-02T05:55:27"))
				.updateCount(0)
				.d_DayDate(Date.valueOf("2024-06-02"))
				.timecapsuleType(0)
				.build();
		timelineService.updateTimeline(record);

	}
	
//	@Test
//	@Transactional
	public void testDeleteTimeline() throws Exception{
		System.out.println("\n===================================");
		
		timelineService.deleteTimeline(6);

	}
	
	@Test
//	@Transactional
	public void testSelectImageForDelete() throws Exception{
		System.out.println("\n===================================");
		
		System.out.println(timelineService.getImageForDelete(1));

	}
	
//	@Test
//	@Transactional
	public void testDeleteImageNo() throws Exception{
		System.out.println("\n===================================");
		
		timelineService.deleteImage(1);

	}
}
