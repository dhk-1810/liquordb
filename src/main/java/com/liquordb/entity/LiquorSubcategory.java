package com.liquordb.entity;

import jakarta.persistence.*;

/**
 * 주종 소분류 클래스입니다.
 */

@Entity
@Table(name = "liquor_subcategories")
public class LiquorSubcategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 예: 페일에일, 바이젠, 스카치

    @Enumerated(EnumType.STRING)
    private Liquor.LiquorCategory category; // 대분류: BEER, WHISKY 등
}