package com.liquordb.repository.liquor;

import com.liquordb.entity.Liquor;
import com.liquordb.entity.LiquorSubcategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LiquorRepository extends JpaRepository<Liquor, Long>, CustomLiquorRepository{

    // 주류 단건 조회
    Optional<Liquor> findByIdAndIsDeleted(Long id, boolean deleted);

    // 좋아요 수 변경
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Liquor l SET l.likeCount = l.likeCount + :delta WHERE l.id = :id")
    void updateLikeCount(@Param("id") Long id, @Param("delta") int delta);
}
