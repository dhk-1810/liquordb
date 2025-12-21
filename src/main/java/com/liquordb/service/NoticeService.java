package com.liquordb.service;

import com.liquordb.dto.notice.NoticeRequestDto;
import com.liquordb.dto.notice.NoticeResponseDto;
import com.liquordb.dto.notice.NoticeSummaryDto;
import com.liquordb.entity.Notice;
import com.liquordb.entity.User;
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
    public NoticeResponseDto getNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException(id));
        return NoticeMapper.toDto(notice);
    }

    // 공지사항 목록 조회
    // TODO 페이지네이션
    @Transactional(readOnly = true)
    public List<NoticeSummaryDto> getAllNotices() {
        return noticeRepository.findAll().stream()
                .map(NoticeMapper::toSummaryDto)
                .toList();
    }

    /**
     * 관리자용
     */
    // 공지사항 등록
    @Transactional
    public NoticeResponseDto create(NoticeRequestDto dto, User author) {
        Notice notice = toEntity(dto, author);
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

