package com.liquordb.repository.notice;

import com.liquordb.entity.Notice;
import org.springframework.data.domain.Page;

public interface CustomNoticeRepository {

    Page<Notice> findAll(NoticeListGetCondition condition);

}
