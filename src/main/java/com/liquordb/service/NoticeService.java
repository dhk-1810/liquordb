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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.liquordb.mapper.NoticeMapper.*;

@RequiredArgsConstructor
@Service
public class NoticeService {

    private static final String DETAIL_KEY_PREFIX = "notice:detail:"; // 단건 조회
    private static final String SUMMARY_KEY_PREFIX = "notice:summary:"; // 목록에서 보일 요약 정보
    private static final String INDEX_KEY_PREFIX = "notice:ids:"; // sorted set 형태의 ID 목록

    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisLockProvider redisLockProvider;

    // 단건 조회
    @Transactional(readOnly = true)
    public NoticeResponseDto get(Long id) {

        String key = DETAIL_KEY_PREFIX + id;
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

        int start = page * limit;
        int end = start + limit - 1;
        Set<Object> idSet = redisTemplate.opsForZSet().reverseRange(INDEX_KEY_PREFIX, start, end);

        // 2. Redis에 ID 목록이 있으면 MGET으로 상세 데이터 조회
        if (idSet != null && !idSet.isEmpty()) {
            List<Long> ids = idSet.stream().map(id -> Long.valueOf(id.toString())).toList();
            List<String> keys = ids.stream().map(id -> SUMMARY_KEY_PREFIX + id).toList();
            List<Object> cachedData = redisTemplate.opsForValue().multiGet(keys);

            // 모든 데이터가 캐시에 있다면 바로 반환
            if (cachedData != null && cachedData.stream().allMatch(Objects::nonNull)) {
                List<NoticeSummaryDto> dtos = cachedData.stream()
                        .map(obj -> (NoticeSummaryDto) obj)
                        .toList();
                // 전체 카운트는 Redis ZSet의 size로 대체 가능
                long total = redisTemplate.opsForZSet().size(INDEX_KEY_PREFIX);
                return PageResponse.from(new PageImpl<>(dtos, PageRequest.of(page, limit), total));
            }
        }

        // 3. Redis에 데이터가 없거나 일부 유실되었다면 DB 조회 (Fallback)
        NoticeListGetCondition condition = new NoticeListGetCondition(deleted, page, limit, true);
        Page<Notice> notices = noticeRepository.findAll(condition);

        // DB 조회 결과를 다시 캐싱 (이 로직은 별도 비동기로 처리해도 좋음)
        notices.forEach(notice -> {
            NoticeSummaryDto dto = NoticeMapper.toSummaryDto(notice);
            redisTemplate.opsForValue().set(SUMMARY_KEY_PREFIX + notice.getId(), dto, Duration.ofHours(1));
            syncNoticeIndex(notice); // 인덱스도 혹시 모르니 동기화
        });
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

        redisTemplate.opsForValue().set(DETAIL_KEY_PREFIX + notice.getId(), response); // 단건 캐싱
        syncNoticeIndex(notice);
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
        redisTemplate.delete(DETAIL_KEY_PREFIX + notice.getId());
        return response;
    }

    // 고정 토글
    @Transactional
    public NoticeResponseDto togglePin(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException(id));
        notice.togglePin();
        NoticeResponseDto response = NoticeMapper.toDto(noticeRepository.save(notice));

        // ID 목록 업데이트
        syncNoticeIndex(notice);
        return response;
    }

    // 공지사항 삭제
    @Transactional
    public void delete(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException(id));
        notice.softDelete();
        noticeRepository.save(notice);

        redisTemplate.delete(DETAIL_KEY_PREFIX + notice.getId());

        // ID 목록 업데이트
        syncNoticeIndex(notice);
    }

    private void syncNoticeIndex(Notice notice) {

        if (notice.isDeleted()) {
            redisTemplate.delete(DETAIL_KEY_PREFIX + notice.getId());
            redisTemplate.delete(SUMMARY_KEY_PREFIX + notice.getId());
            redisTemplate.opsForZSet().remove(INDEX_KEY_PREFIX, notice.getId());
            return;
        }

        redisTemplate.opsForValue().set(SUMMARY_KEY_PREFIX + notice.getId(), NoticeMapper.toSummaryDto(notice));
        redisTemplate.opsForValue().set(DETAIL_KEY_PREFIX + notice.getId(), NoticeMapper.toDto(notice));

        double score = notice.getId().doubleValue();
        if (notice.isPinned()) score += 1_000_000_000_000.0;

        redisTemplate.opsForZSet().add(INDEX_KEY_PREFIX, notice.getId(), score);
    }
}