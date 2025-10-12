package com.liquordb.repository;

import com.liquordb.entity.UserTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 유저가 선택한 태그 저장소입니다.
 */
public interface UserTagRepository extends JpaRepository<UserTag, Long> {
    @Query("SELECT ut.tag.id FROM UserTag ut WHERE ut.user.id = :userId")
    List<Long> findTagIdsByUserId(@Param("userId") Long userId);
}
