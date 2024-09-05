package com.example.omg_project.domain.user.entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 사용자 랜덤 닉네임 생성 클래스
 */
public class RandomNickname {

    public static String generateRandomNickname() {
        // 형용사
        List<String> adjective = Arrays.asList(
                "행복한", "슬픈", "게으른", "슬기로운", "수줍은",
                "그리운", "더러운", "귀여운", "배고픈", "배부른",
                "부자", "재벌", "웃고있는", "깨발랄한", "용감한",
                "신나는", "기운찬", "느긋한", "치열한", "차분한"
        );

        // 명사
        List<String> name = Arrays.asList(
                "우주미아", "코딩고수", "슈퍼스타", "바나나", "고양이",
                "강아지", "사자", "호랑이", "햄버거", "피자",
                "유니콘", "드래곤", "펭귄", "아이스크림", "나비",
                "풍선", "해바라기", "자두", "체리", "수박",
                "곽유진", "전현진", "손설빈", "김혜주", "박경서"
        );

        Collections.shuffle(adjective);
        Collections.shuffle(name);

        String adj = adjective.get(0);
        String selectedName = name.get(0);
        String number = (int)(Math.random() * 99) + 1 + "";

        return adj + selectedName + number;
    }
}
