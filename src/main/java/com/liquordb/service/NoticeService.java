package com.liquordb.service;

import com.liquordb.dto.notice.NoticeRequestDto;
import com.liquordb.dto.notice.NoticeResponseDto;
import com.liquordb.dto.notice.NoticeSummaryDto;
import com.liquordb.entity.Notice;
import com.liquordb.exception.NotFoundException;
import com.liquordb.mapper.NoticeMapper;
import com.liquordb.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.liquordb.mapper.NoticeMapper.*;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    // 공지사항 단건 조회
    public NoticeResponseDto findById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 공지사항입니다."));
        return NoticeMapper.toDto(notice);
    }

    // 공지사항 목록 조회
    public List<NoticeSummaryDto> findAll() {
        return noticeRepository.findAll().stream()
                .map(NoticeMapper::toSummaryDto)
                .toList();
    }

    /**
     * 관리자용
     */
    // 공지사항 등록
    public NoticeResponseDto create(NoticeRequestDto dto) {
        Notice notice = toEntity(dto);
        noticeRepository.save(notice);
        return toDto(notice);
    }

    // 공지사항 수정
    public NoticeResponseDto update(Long id, NoticeRequestDto dto) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 공지사항입니다."));
        if (dto.getTitle() != null) notice.setTitle(dto.getTitle());
        if (dto.getContent() != null) notice.setContent(dto.getContent());
        return toDto(notice);
    }

    // 고정 토글
    public NoticeResponseDto togglePin(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 공지사항입니다."));
        notice.setPinned(!notice.isPinned());
        if (notice.isPinned()) {
            notice.setPinnedAt(LocalDateTime.now());
        }
        return NoticeMapper.toDto(noticeRepository.save(notice));
    }

    // 공지사항 삭제
    public void delete(Long id) {
        if (!noticeRepository.existsById(id)) {
            throw new NotFoundException("공지사항이 존재하지 않습니다.");
        }
        noticeRepository.deleteById(id);
    }
}

