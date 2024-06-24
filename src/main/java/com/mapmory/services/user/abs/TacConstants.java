package com.mapmory.services.user.abs;

public abstract class TacConstants {

    public static final String PERSONAL_INFO_FILE_NAME = "/개인정보 수집 및 이용 약관.txt";
    public static final String LOCATION_BASED_FILE_NAME = "/위치 기반 서비스 이용약관.txt";

    public static String getFilePath(int tacType) {
        switch (tacType) {
            case 1:
                return PERSONAL_INFO_FILE_NAME;
            case 2:
                return LOCATION_BASED_FILE_NAME;
            default:
                throw new IllegalArgumentException("Invalid tacType: " + tacType);
        }
    }
}

