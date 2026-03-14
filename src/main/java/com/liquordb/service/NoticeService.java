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
import com.liquordb.redis.RedisLockProvider;
import com.liquordb.repository.notice.NoticeListGetCondition;
import com.liquordb.repository.notice.NoticeRepository;
import com.liquordb.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

import static com.liquordb.mapper.NoticeMapper.*;

@RequiredArgsConstructor
@Service
public class NoticeService {

    private static final String DATA_KEY_PREFIX = "notice:data:"; // 단건 NoticeResponseDto
    private static final String LIST_KEY_PREFIX = "notice:ids:"; // sorted set 형태의 ID 목록

    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisLockProvider redisLockProvider;

    // 단건 조회
    @Transactional(readOnly = true)
    public NoticeResponseDto get(Long id) {

        String key = DATA_KEY_PREFIX + id;
        NoticeResponseDto cached = (NoticeResponseDto) redisTemplate.opsForValue().get(key);
        if (cached != null) return cached;

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

//        캐시를 먼저 조회

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
    public NoticeResponseDto create(NoticeRequest request, UUID authorId) {

        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException(authorId));
        Notice notice = toEntity(request, user);
        noticeRepository.save(notice);
        NoticeResponseDto response = NoticeMapper.toDto(notice);

        redisTemplate.opsForValue().set(DATA_KEY_PREFIX + notice.getId(), response); // 단건 캐싱
//        cacheNoticeIds(response);
        return response;
    }

    // 공지사항 수정
    @Transactional
    public NoticeResponseDto update(Long id, NoticeRequest request) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException(id));
        notice.update(request);
        NoticeResponseDto response = NoticeMapper.toDto(notice);

        // 단건 삭제 - 조회 시 다시 캐싱
        redisTemplate.delete(DATA_KEY_PREFIX + notice.getId());
        return response;
    }

    // 고정 토글
    @Transactional
    public NoticeResponseDto togglePin(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException(id));
        notice.togglePin();
        NoticeResponseDto response = NoticeMapper.toDto(noticeRepository.save(notice));

        // 단건 삭제 - 조회 시 다시 캐싱
        redisTemplate.delete(DATA_KEY_PREFIX + notice.getId());

        // 목록 캐싱 (대체)

        return response;
    }

    // 공지사항 삭제
    @Transactional
    public void delete(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException(id));
        notice.softDelete();
        noticeRepository.save(notice);

        redisTemplate.delete(DATA_KEY_PREFIX + notice.getId());

        // 목록 캐싱 (대체)

    }

    private void cacheNoticeIds(NoticeResponseDto dto) {

        double score = System.currentTimeMillis();
        if (dto.isPinned()) score += 1000000000000.0; // 핀 고정 시 가중치 부여
        redisTemplate.opsForZSet().add(LIST_KEY_PREFIX, dto.id(), score);
    }
}