package com.liquordb.user.repository;

import com.liquordb.user.entity.UserLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserLevelRepository extends JpaRepository<UserLevel, Long> {

    @Query("""
        SELECT ul
        FROM UserLevel ul
        WHERE ul.minReviewCount <= :reviewCount
        ORDER BY ul.minReviewCount DESC
    """)
    List<UserLevel> findApplicableLevels(@Param("reviewCount") Long reviewCount);
}