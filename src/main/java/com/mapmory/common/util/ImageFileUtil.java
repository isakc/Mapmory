package com.mapmory.common.util;

import java.util.UUID;

public class ImageFileUtil {

    // 파일명에서 UUID 형식의 파일명으로 변경하는 메서드
    public static String getImageUUIDFileName(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("Invalid file name: " + originalFilename);
        }
        // 파일 확장자 추출
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        
        // UUID 생성 및 파일 확장자와 결합하여 새로운 파일명 생성
        String uuidFilename = UUID.randomUUID().toString().replace("-", "") + fileExtension;
        
        return uuidFilename;
    }
}
