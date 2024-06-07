package com.mapmory.services.recommend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class RecommendServiceImplJM {
    @Value("${naver.clova.speech.client.id}")
    private String clientId;

    @Value("${naver.clova.speech.client.secret}")
    private String clientSecret;

    private String apiUrl = "https://naveropenapi.apigw.ntruss.com/recog/v1/stt";

    private final OkHttpClient client = new OkHttpClient();

    @PostConstruct
    private void initApiUrl() {
        // 프로퍼티 파일에서 값을 읽어온 경우, 여기에서 apiUrl을 설정할 수 있습니다.
    }

    public String recognizeSpeech(byte[] audioData) throws IOException {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "audio.wav", RequestBody.create(MediaType.parse("audio/wav"), audioData))
                .addFormDataPart("lang", "Kor")
                .build();

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .addHeader("X-NCP-APIGW-API-KEY-ID", clientId)
                .addHeader("X-NCP-APIGW-API-KEY", clientSecret)
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> result = objectMapper.readValue(responseBody.string(), HashMap.class);
                return (String) result.get("text");
            }
        }

        return null;
    }
}