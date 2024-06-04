package com.mapmory.unit.timeline;

public class GeoUtils {

    // 지구 반지름 (킬로미터 단위)
    private static final double EARTH_RADIUS = 6371.0;

    public static void main(String[] args) {
        // 현재 위치 (위도, 경도)
        double currentLatitude = 37.7749;
        double currentLongitude = -122.4194;

        // 1km 내의 위도와 경도 계산
        double latitude1km = calculateLatitudeDifference(1);
        double longitude1km = calculateLongitudeDifference(1, currentLatitude);

        System.out.println("1 km in latitude: " + latitude1km + " degrees");
        System.out.println("1 km in longitude at latitude " + currentLatitude + ": " + longitude1km + " degrees");

        // 현재 위치에서 1km 내의 경계
        double minLatitude = currentLatitude - latitude1km;
        double maxLatitude = currentLatitude + latitude1km;
        double minLongitude = currentLongitude - longitude1km;
        double maxLongitude = currentLongitude + longitude1km;

        System.out.println("1 km boundary:");
        System.out.println("Latitude: [" + minLatitude + ", " + maxLatitude + "]");
        System.out.println("Longitude: [" + minLongitude + ", " + maxLongitude + "]");
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

