package com.mapmory.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.mapmory.common.interceptor.LoginInterceptor;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer{
	
	@Bean
	public LoginInterceptor loginInterceptor() {
		return new LoginInterceptor();
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
	}

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://mapmory.co.kr") // 클라이언트 출처 지정
                .allowCredentials(true) // 인증 정보 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE"); // 허용할 HTTP 메서드 지정
    }
    
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// TODO Auto-generated method stub
		
		registry.addInterceptor(loginInterceptor())
				.order(1)
				.addPathPatterns("/**")
				.excludePathPatterns("/css/**", "/*.ico", "/error", "/javascript/**")
				.excludePathPatterns("/", "/user/rest/login", "/user/getIdView", "/user/getPasswordView", 
						"/user/google/auth/callback", "/user/getNaverLoginView", "/user/getGoogleLoginView",
						"/user/kakaoCallback", "/user/getKakaoLoginView", "/user/naver/auth/callback",
						"/user/getRecoverAccountView", "/user/getAgreeTermsAndConditionsList", "/user/getUserDetailTermsAndConditions",
						"/user/getSignUpView", "/user/rest/signUp", "/user/setupForTest", 
						"/user/getSecondaryAuthView", "/user/rest/checkSecondaryKey", "/user/rest/generateKey");
	}

}
