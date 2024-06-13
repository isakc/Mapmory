package com.mapmory.common.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mapmory.services.timeline.domain.MapRecord;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.dto.SearchDto;

public class GeoUtil {

    // 지구 반지름 (킬로미터 단위)
    private static final double EARTH_RADIUS = 6371.0;

    public static SearchDto calculateRadius (double currentLatitude, double currentLongitude ,double radius) {
        // 현재 위치 (위도, 경도)
//        double currentLatitude = 37.7749;
//        double currentLongitude = -122.4194;

        // 1km 내의 위도와 경도 계산
        double latitude = calculateLatitudeDifference(radius);
        double longitude = calculateLongitudeDifference(radius, currentLatitude);

        System.out.println("radius km in latitude: " + latitude + " degrees");
        System.out.println("radius km in longitude at latitude " + currentLatitude + ": " + longitude + " degrees");

        // 현재 위치에서 1km 내의 경계
        double minLatitude = currentLatitude - latitude;
        double maxLatitude = currentLatitude + latitude;
        double minLongitude = currentLongitude - longitude;
        double maxLongitude = currentLongitude + longitude;

        System.out.println("radius km boundary:");
        System.out.println("Latitude: [" + minLatitude + ", " + maxLatitude + "]");
        System.out.println("Longitude: [" + minLongitude + ", " + maxLongitude + "]");
        
        return SearchDto.builder()
    			.minLatitude(minLatitude)
    			.maxLatitude(maxLatitude)
    			.minLongitude(minLongitude)
    			.maxLongitude(maxLongitude)
    			.build();
    }

    // 위도 1km 차이 계산
    public static double calculateLatitudeDifference(double km) {
        return km / 111.0;
    }

    // 주어진 위도에서 경도 1km 차이 계산
    public static double calculateLongitudeDifference(double km, double latitude) {
        return km / (111.0 * Math.cos(Math.toRadians(latitude)));
    }
    

	public static Double calculateCloseDistance(Double currentLatitude, Double currentLongitude, Double latitude, Double longitude){
		// 위도 및 경도를 라디안 단위로 변환
        double currentLatRad = Math.toRadians(currentLatitude);
        double currentLongRad = Math.toRadians(currentLongitude);
        double targetLatRad = Math.toRadians(latitude);
        double targetLongRad = Math.toRadians(longitude);

        // 위도 및 경도의 차이 계산
        double deltaLat = targetLatRad - currentLatRad;
        double deltaLong = targetLongRad - currentLongRad;

        // Haversine 공식 적용
        double a = Math.pow(Math.sin(deltaLat / 2), 2) +
                   Math.cos(currentLatRad) * Math.cos(targetLatRad) *
                   Math.pow(Math.sin(deltaLong / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;

        return distance;
	}

    public static void sortByDistance(List<MapRecord> recordList) {
        Collections.sort(recordList, new Comparator<MapRecord>() {
            @Override
            public int compare(MapRecord record1, MapRecord record2) {
                // record1과 record2의 거리를 비교하여 정렬
                return Double.compare(record1.getDistance(), record2.getDistance());
            }
        });
    }
}

