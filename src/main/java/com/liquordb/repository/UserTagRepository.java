package com.liquordb.repository;

import com.liquordb.entity.Tag;
import com.liquordb.entity.User;
import com.liquordb.entity.UserTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * 유저가 선택한 태그 저장소입니다.
 */
public interface UserTagRepository extends JpaRepository<UserTag, Long> {

    @Query("SELECT t FROM Tag t JOIN t.userTags ut WHERE ut.user.id = :userId")
    List<Tag> findTagsByUserId(@Param("userId") UUID userId);

    UserTag getReferenceById(Long tagId);

    List<UserTag> findByUserId(UUID userId);

    void deleteByUserIdAndTagId(UUID userId, Long tagId);

    boolean existsUserTagByUserAndTag_Id(User user, Long tagId);
}
