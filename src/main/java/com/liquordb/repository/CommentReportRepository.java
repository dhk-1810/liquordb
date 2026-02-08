package com.liquordb.repository;

import com.liquordb.entity.CommentReport;
import com.liquordb.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {

    // 같은 유저가 이미 신고했는지 여부 (중복 신고 방지)
    boolean existsByCommentIdAndUser_Id(Long commentId, UUID userId);

    // 누적 신고 건수 count
    long countByComment_Id(Long comment);

    // 목록 조회
    @Query("SELECT cr FROM CommentReport cr WHERE (:status IS NULL OR cr.status = :status)")
    Page<CommentReport> findAllByStatus(@Param("status") ReportStatus status, Pageable pageable);
}
