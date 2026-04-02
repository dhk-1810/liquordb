package com.liquordb.entity;

import com.liquordb.enums.PeriodType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

/**
 * 캐시 에러 발생 대처를 위해 (Fallback) 사용.
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "liquor_ranking")
public class LiquorRanking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private PeriodType periodType;

    @Column(nullable = false, updatable = false)
    private Long liquorId;

    @Column(nullable = false, updatable = false)
    private long score;

    @Column(nullable = false, updatable = false)
    private int ranking;

    @CreatedDate
    private LocalDateTime createdAt;

    private LiquorRanking(PeriodType periodType, Long liquorId, long score, int ranking) {
        this.periodType = periodType;
        this.liquorId = liquorId;
        this.score = score;
        this.ranking = ranking;
    }

    public static LiquorRanking create(PeriodType periodType, Long liquorId, long score, int ranking){
        return new LiquorRanking(periodType, liquorId, score, ranking);
    }
}
