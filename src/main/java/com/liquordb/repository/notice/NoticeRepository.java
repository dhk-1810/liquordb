package com.liquordb.repository.notice;

import com.liquordb.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long>, CustomNoticeRepository {

    Optional<Notice> findByIdAndIsDeleted(Long id, boolean isDeleted);
}
