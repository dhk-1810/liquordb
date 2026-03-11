package com.liquordb.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주종 소분류 클래스입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "liquor_subcategories")
public class LiquorSubcategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 예: 페일에일, 바이젠, 스카치

    private String description;

    @Enumerated(EnumType.STRING)
    private Liquor.LiquorCategory category; // 대분류: BEER, WHISKY 등

    public LiquorSubcategory(String name, String description, Liquor.LiquorCategory category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public static LiquorSubcategory create(String name, String description, Liquor.LiquorCategory category){
        return new LiquorSubcategory(
                name,
                description,
                category
        );
    }
}