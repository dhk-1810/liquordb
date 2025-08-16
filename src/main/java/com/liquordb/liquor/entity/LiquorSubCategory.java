package com.liquordb.liquor.entity;

import jakarta.persistence.*;

/**
 * 주종 소분류 클래스입니다.
 */

@Entity
public class LiquorSubCategory {

    @Id
    @GeneratedValue
    private Long id;

    private String name; // 예: 페일에일, 바이젠, 스카치

    @Enumerated(EnumType.STRING)
    private LiquorCategory type; // 대분류: BEER, WHISKY 등
}