package com.liquordb.service;

import com.liquordb.PageResponse;
import com.liquordb.dto.notice.NoticeRequestDto;
import com.liquordb.dto.notice.NoticeResponseDto;
import com.liquordb.dto.notice.NoticeSummaryDto;
import com.liquordb.entity.Notice;
import com.liquordb.entity.User;
import com.liquordb.exception.notice.NoticeNotFoundException;
import com.liquordb.exception.user.UserNotFoundException;
import com.liquordb.mapper.NoticeMapper;
import com.liquordb.repository.NoticeRepository;
import com.liquordb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.liquordb.mapper.NoticeMapper.*;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;

    // 공지사항 단건 조회
    @Transactional(readOnly = true)
    public NoticeResponseDto getNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException(id));
        return NoticeMapper.toDto(notice);
    }

    // 공지사항 목록 조회
    @Transactional(readOnly = true)
    public PageResponse<NoticeSummaryDto> getAllNotices(Pageable pageable) {

        Sort sort = Sort.by(Sort.Order.desc("isPinned")) // 고정된 공지 우선 - 첫 페이지에만 표시됨
                .and(pageable.getSort());

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
        Page<Notice> notices = noticeRepository.findAllAndIsDeletedFalse(sortedPageable);
        Page<NoticeSummaryDto> response = notices.map(NoticeMapper::toSummaryDto);
        return PageResponse.from(response);
    }

    /**
     * 관리자용
     */
    // 공지사항 등록
    @Transactional
    public NoticeResponseDto create(NoticeRequestDto dto, UUID authorId) {
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException(authorId));
        Notice notice = toEntity(dto, user);
        noticeRepository.save(notice);
        return toDto(notice);
    }

    // 공지사항 수정
    @Transactional
    public NoticeResponseDto update(Long id, NoticeRequestDto request) {
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

