package com.mapmory.common.util;

import java.util.HashMap;
import java.util.Map;

public class GeoUtil {

    // 지구 반지름 (킬로미터 단위)
    private static final double EARTH_RADIUS = 6371.0;

    public static Map<String, Object> calculateRadius (double currentLatitude, double currentLongitude ,double radius) {
        // 현재 위치 (위도, 경도)
//        double currentLatitude = 37.7749;
//        double currentLongitude = -122.4194;
    	Map<String,Object> map=new HashMap<String, Object>();

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

        map.put("minLatitude", minLatitude);
        map.put("maxLatitude", maxLatitude);
        map.put("minLongitude", minLongitude);
        map.put("maxLongitude", maxLongitude);
        
        return map;
    }

    // 위도 1km 차이 계산
    public static double calculateLatitudeDifference(double km) {
        return km / 111.0;
    }

    // 주어진 위도에서 경도 1km 차이 계산
    public static double calculateLongitudeDifference(double km, double latitude) {
        return km / (111.0 * Math.cos(Math.toRadians(latitude)));
    }
}

