package com.liquordb.service;

import com.liquordb.dto.PageResponse;
import com.liquordb.dto.notice.NoticeListGetRequest;
import com.liquordb.dto.notice.NoticeRequest;
import com.liquordb.dto.notice.NoticeResponseDto;
import com.liquordb.dto.notice.NoticeSummaryDto;
import com.liquordb.entity.Notice;
import com.liquordb.entity.User;
import com.liquordb.enums.SortDirection;
import com.liquordb.exception.notice.NoticeNotFoundException;
import com.liquordb.exception.user.UserNotFoundException;
import com.liquordb.mapper.NoticeMapper;
import com.liquordb.repository.notice.NoticeListGetCondition;
import com.liquordb.repository.notice.NoticeRepository;
import com.liquordb.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.liquordb.mapper.NoticeMapper.*;

@RequiredArgsConstructor
@Service
public class NoticeService {

    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;

    // 단건 조회
    @Transactional(readOnly = true)
    public NoticeResponseDto get(Long id) {
        Notice notice = noticeRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(() -> new NoticeNotFoundException(id));
        return NoticeMapper.toDto(notice);
    }

    // 목록 조회
    @Transactional(readOnly = true)
    public PageResponse<NoticeSummaryDto> getAll(NoticeListGetRequest request) {

        boolean deleted = request.deleted() != null && request.deleted();
        int page = request.page() == null ? 0 : request.page();
        int limit = request.limit() == null ? 50 : request.limit();
        boolean descending = request.sortDirection() == null || request.sortDirection() == SortDirection.DESC;

        NoticeListGetCondition condition = new NoticeListGetCondition(deleted, page, limit, descending);
        Page<Notice> notices = noticeRepository.findAll(condition);
        Page<NoticeSummaryDto> response = notices.map(NoticeMapper::toSummaryDto);
        return PageResponse.from(response);
    }

    /**
     * 관리자용
     */
    // 공지사항 등록
    @Transactional
    public NoticeResponseDto create(NoticeRequest dto, UUID authorId) {
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException(authorId));
        Notice notice = toEntity(dto, user);
        noticeRepository.save(notice);
        return toDto(notice);
    }

    // 공지사항 수정
    @Transactional
    public NoticeResponseDto update(Long id, NoticeRequest request) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException(id));
        notice.update(request);
        return NoticeMapper.toDto(notice);
    }

    // 고정 토글
    @Transactional
    public NoticeResponseDto togglePin(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException(id));
        notice.togglePin();
        return NoticeMapper.toDto(noticeRepository.save(notice));
    }

    // 공지사항 삭제
    @Transactional
    public void delete(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException(id));
        notice.softDelete();
        noticeRepository.save(notice);
    }
}

