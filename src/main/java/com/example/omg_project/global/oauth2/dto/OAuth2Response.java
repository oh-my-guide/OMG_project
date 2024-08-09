package com.example.omg_project.global.oauth2.dto;

import java.util.Map;

/**
 * Oauth2 플랫폼마다 전달하는 값들을 받기위한 인터페이스
 */
public interface OAuth2Response {

    //제공자 (naver, google kakao)
    String getProvider();

    //제공자에서 발급해주는 아이디(번호)
    String getProviderId();

    //이메일
    String getEmail();

    //사용자 실명 (설정한 이름)
    String getName();

    // 권한
    Map<String, Object> getAttributes();
}
