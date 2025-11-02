package com.liquordb.service;

import com.liquordb.dto.notice.NoticeRequestDto;
import com.liquordb.dto.notice.NoticeResponseDto;
import com.liquordb.dto.notice.NoticeSummaryDto;
import com.liquordb.entity.Notice;
import com.liquordb.exception.NoticeNotFoundException;
import com.liquordb.mapper.NoticeMapper;
import com.liquordb.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.liquordb.mapper.NoticeMapper.*;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    // 공지사항 단건 조회
    @Transactional(readOnly = true)
    public NoticeResponseDto findById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException(id));
        return NoticeMapper.toDto(notice);
    }

    // 공지사항 목록 조회
    @Transactional(readOnly = true)
    public List<NoticeSummaryDto> findAll() {
        return noticeRepository.findAll().stream()
                .map(NoticeMapper::toSummaryDto)
                .toList();
    }

    /**
     * 관리자용
     */
    // 공지사항 등록
    @Transactional
    public NoticeResponseDto create(NoticeRequestDto dto) {
        Notice notice = toEntity(dto);
        noticeRepository.save(notice);
        return toDto(notice);
    }

    // 공지사항 수정
    @Transactional
    public NoticeResponseDto update(Long id, NoticeRequestDto dto) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException(id));
        if (dto.getTitle() != null) notice.setTitle(dto.getTitle());
        if (dto.getContent() != null) notice.setContent(dto.getContent());
        return toDto(notice);
    }

    // 고정 토글
    @Transactional
    public NoticeResponseDto togglePin(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException(id));
        notice.setPinned(!notice.isPinned());
        if (notice.isPinned()) {
            notice.setPinnedAt(LocalDateTime.now());
        }
        return NoticeMapper.toDto(noticeRepository.save(notice));
    }

    // 공지사항 삭제
    @Transactional
    public void delete(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException(id));
        notice.setDeleted(true);
        notice.setDeletedAt(LocalDateTime.now());
        noticeRepository.save(notice);
    }
}

