package com.liquordb.enums;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Country {

    // 주요 국가는 상수로 관리
    KR("KR", "대한민국"),
    US("US", "미국"),
    JP("JP", "일본"),
    FR("FR", "프랑스"),
    GB("GB", "영국"),
    DE("DE", "독일"),
    RU("RU", "러시아"),
    UNKNOWN("XX", "알 수 없음");

    private final String code; // ISO 2자리 코드 (예: KR)
    private final String koreanName;

    Country(String code, String koreanName) {
        this.code = code;
        this.koreanName = koreanName;
    }

    private static final Map<String, Country> ENUM_MAP =
            Arrays.stream(Country.values()).collect(Collectors.toMap(Country::getCode, Function.identity()));

    public String getCode() { return code; }
    public String getKoreanName() { return koreanName; }

    /**
     * ISO 코드로 Enum 찾기 (예: "KR" -> Country.KR)
     */
    public static Country fromCode(String code) {
        return Optional.ofNullable(ENUM_MAP.get(code.toUpperCase()))
                .orElse(UNKNOWN);
    }

    /**
     * Java Locale 시스템을 이용해 한글 이름을 가져오는 헬퍼 메서드
     */
    public static String getFullKoreanName(String isoCode) {
        Locale locale = new Locale("", isoCode);
        return locale.getDisplayCountry(Locale.KOREAN);
    }
}