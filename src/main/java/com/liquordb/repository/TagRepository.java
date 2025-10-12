package com.liquordb.repository;

import com.liquordb.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    // 특정 주류에 연결된 태그 조회
    @Query("SELECT t FROM Tag t JOIN t.liquorTags lt WHERE lt.liquor.id = :liquorId")
    List<Tag> findTagsByLiquorId(@Param("liquorId") Long liquorId);

    // 특정 유저가 선호하는 태그 조회
    @Query("SELECT t FROM Tag t JOIN t.userTags ut WHERE ut.user.id = :userId")
    List<Tag> findTagsByUserId(@Param("userId") Long userId);
}