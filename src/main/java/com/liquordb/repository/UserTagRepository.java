package com.liquordb.repository;

import com.liquordb.entity.Liquor;
import com.liquordb.entity.Tag;
import com.liquordb.entity.User;
import com.liquordb.entity.UserTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserTagRepository extends JpaRepository<UserTag, Long> {

    Optional<UserTag> findByUser_IdAndTag_Id(UUID userId, Long tagId);

    @Query("SELECT t FROM Tag t JOIN t.userTags ut WHERE ut.user.id = :userId")
    List<Tag> findTagsByUserId(@Param("userId") UUID userId);

    List<UserTag> findByUserId(UUID userId);

    boolean existsByUserAndTag_Id(User user, Long tagId);

    @Query("""
        SELECT DISTINCT lt.liquor FROM LiquorTag lt
        JOIN lt.tag t
        JOIN UserTag ut ON ut.tag = t
        WHERE ut.user.id = :userId
    """)
    List<Liquor> findLiquorsByUser_Id(@Param("userId") UUID userId);

    boolean existsByUserIdAndTagId(UUID userId, Long tagId);

    void deleteByUser_IdAndTag_Id(UUID userId, Long tagId);
}
