package com.liquordb.entity;

import com.liquordb.enums.LiquorCategory;
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
@Table(
        name = "liquor_subcategories",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_category_name", columnNames = {"category", "name"})
        }
)
public class LiquorSubcategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name; // 영문 이름 (기본 식별용, 예: Vodka based, Single Malt)

    @Column(length = 50)
    private String nameKo; // 한국어 이름 (예: 보드카 베이스, 싱글 몰트)

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private LiquorCategory category; // 대분류: BEER, WHISKY 등

    public LiquorSubcategory(String name, String nameKo, LiquorCategory category) {
        this.name = name;
        this.nameKo = nameKo;
        this.category = category;
    }

    public static LiquorSubcategory create(String name, String nameKo, LiquorCategory category){
        return new LiquorSubcategory(
                name,
                nameKo,
                category
        );
    }
}