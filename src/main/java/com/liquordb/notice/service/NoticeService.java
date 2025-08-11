package com.liquordb.notice.service;

import com.liquordb.notice.dto.NoticeRequestDto;
import com.liquordb.notice.dto.NoticeResponseDto;
import com.liquordb.notice.entity.Notice;
import com.liquordb.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

import static com.liquordb.notice.mapper.NoticeMapper.*;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    // 공지사항 등록
    public NoticeResponseDto createNotice(NoticeRequestDto dto) {
        Notice notice = toEntity(dto);
        noticeRepository.save(notice);
        return toDto(notice);
    }

    // 공지사항 수정
    public NoticeResponseDto updateNotice(Long id, NoticeRequestDto dto) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("공지사항을 찾을 수 없습니다."));
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setPinned(dto.isPinned());
        return toDto(notice);
    }

    // 공지사항 삭제
    public void deleteNotice(Long id) {
        if (!noticeRepository.existsById(id)) {
            throw new NoSuchElementException("공지사항이 존재하지 않습니다.");
        }
        noticeRepository.deleteById(id);
    }
}

