package com.liquordb.entity.liquordetail;

import com.liquordb.entity.Liquor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Liquor 엔터티의 주종별 세부 정보를 관리하는 추상 클래스입니다.
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "liquor_type")
@Table(name = "liquor_details")
public abstract class LiquorDetail {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "liquor_id")
    private Liquor liquor;

}
