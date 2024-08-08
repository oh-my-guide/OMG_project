package com.example.omg_project.global.oauth2.dto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.math.BigInteger;
import java.util.*;

@Slf4j
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2Response oAuth2Response;
    private final String role;

    public CustomOAuth2User(OAuth2Response oAuth2Response, String role) {
        this.oAuth2Response = oAuth2Response;
        this.role = role;
    }

    @Override
    public Map<String, Object> getAttributes() {

        return oAuth2Response.getAttributes();
    }

    // role에 해당하는 값
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new SimpleGrantedAuthority(role));
        return collection;
    }

    @Override
    public String getName() {
        return oAuth2Response.getName();
    }

    public String getUsername() {
        return oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();
    }

    /**
     * 각 소셜 로그인마다 OAuth2 제공자의 ID 형식이 다르기 때문에 각 ID에 맞는 처리
     * @return getProviderId
     * Google : 매우 큰 숫자 BigInt
     * Naver : 문자열 String
     * Kakao : 작은 숫자 Long
     */
    public Long getUserIdAsLong() {
        String providerId = oAuth2Response.getProviderId();

        if (providerId == null) {
            return null;
        }

        try {
            // providerId가 정수인지 확인
            if (providerId.matches("\\d+")) {
                BigInteger bigInt = new BigInteger(providerId);
                if (bigInt.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
                    return Long.MAX_VALUE;
                } else if (bigInt.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0) {
                    return Long.MIN_VALUE;
                } else {
                    return bigInt.longValue();
                }
            } else {
                // providerId가 정수가 아닌 경우 문자열의 해시코드를 사용하여 Long으로 변환
                return (long) providerId.hashCode();
            }
        } catch (NumberFormatException e) {
            log.error("user ID parsing 오류 :: {}", providerId, e);
            return null;
        }
    }

    public List<String> getRoles() {
        return Collections.singletonList(role);
    }
}
