package com.mapmory.common.util;

import com.mapmory.services.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TextToImage {

    @Autowired
    private ProductService productService;

    @Value("${object.folderName}")
    private String folderName;

    // 이미지 태그 패턴 (/확인/ 형식)
    private static final Pattern IMAGE_TAG_PATTERN = Pattern.compile("/([^/]+)/");

    public String processImageTags(String content) throws Exception {
        StringBuilder sb = new StringBuilder();
        Matcher matcher = IMAGE_TAG_PATTERN.matcher(content);
        int lastEnd = 0;

        while (matcher.find()) {
            sb.append(content, lastEnd, matcher.start());
            String imageTag = matcher.group(1); // 태그 이름만 추출

            try {
                // 이미지 태그를 URL로 변환
                String imageUrl = getImageUrlByImageTag(imageTag);
                System.out.println("Image URL: " + imageUrl); // Add this line to log the URL
                if (imageUrl != null) {
                    // HTML 이미지 태그로 변환하여 추가
                    sb.append("<img src='").append(imageUrl).append("'/>");
                } else {
                    // 이미지 태그가 아닌 경우에는 그대로 추가
                    sb.append(matcher.group());
                }
            } catch (Exception e) {
                System.err.println("Error processing image tag: " + imageTag);
                e.printStackTrace();
            }

            lastEnd = matcher.end();
        }
        sb.append(content.substring(lastEnd)); // 마지막 남은 텍스트 추가

        String finalString = sb.toString();
        System.out.println("Final String: " + finalString); // 최종 문자열을 출력

        return finalString;
    }

    private String getImageUrlByImageTag(String imageTag) throws Exception {
        return productService.getImageUrlByImageTag(imageTag, folderName);
    }
}
