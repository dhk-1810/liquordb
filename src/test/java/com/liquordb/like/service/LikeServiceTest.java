package com.liquordb.like.service;

import com.liquordb.like.dto.LikeRequestDto;
import com.liquordb.like.dto.LikeResponseDto;
import com.liquordb.like.entity.Like;
import com.liquordb.like.repository.LikeRepository;
import com.liquordb.liquor.repository.*;
import com.liquordb.like.entity.LikeTargetType;
import com.liquordb.review.repository.CommentRepository;
import com.liquordb.review.repository.ReviewRepository;
import com.liquordb.user.entity.User;
import com.liquordb.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class LikeServiceTest {

    @Mock private LikeRepository likeRepository;
    @Mock private UserRepository userRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private LiquorRepository liquorRepository;

    @InjectMocks private LikeService likeService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).nickname("tester").build();
    }

    @Test
    void 좋아요_추가_성공() {
        // given
        LikeRequestDto request = new LikeRequestDto(10L, LikeTargetType.REVIEW);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.existsById(10L)).thenReturn(true);
        when(likeRepository.findByUserIdAndTargetIdAndTargetType(1L, 10L, LikeTargetType.REVIEW))
                .thenReturn(Optional.empty());

        Like like = Like.builder()
                .id(100L)
                .user(user)
                .targetId(10L)
                .targetType(LikeTargetType.REVIEW)
                .likedAt(LocalDateTime.now())
                .build();

        when(likeRepository.save(any())).thenReturn(like);

        // when
        LikeResponseDto response = likeService.toggleLike(1L, request);

        // then
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getTargetId()).isEqualTo(10L);
        assertThat(response.getTargetType()).isEqualTo(LikeTargetType.REVIEW);
        assertThat(response.getLikedAt()).isNotNull();

        verify(likeRepository, times(1)).save(any());
    }

    @Test
    void 좋아요_취소_성공() {
        // given
        LikeRequestDto request = new LikeRequestDto(10L, LikeTargetType.REVIEW);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.existsById(10L)).thenReturn(true);

        Like existingLike = Like.builder()
                .id(200L)
                .user(user)
                .targetId(10L)
                .targetType(LikeTargetType.REVIEW)
                .likedAt(LocalDateTime.now())
                .build();

        when(likeRepository.findByUserIdAndTargetIdAndTargetType(1L, 10L, LikeTargetType.REVIEW))
                .thenReturn(Optional.of(existingLike));

        // when
        LikeResponseDto response = likeService.toggleLike(1L, request);

        // then
        assertThat(response.getId()).isEqualTo(200L);
        assertThat(response.getLikedAt()).isNull(); // 취소니까 null

        verify(likeRepository, times(1)).delete(existingLike);
    }

    @Test
    void 존재하지_않는_리뷰에_좋아요시_예외발생() {
        // given
        LikeRequestDto request = new LikeRequestDto(99L, LikeTargetType.REVIEW);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.existsById(99L)).thenReturn(false);

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> likeService.toggleLike(1L, request));
    }

    @Test
    void 좋아요_카운트() {
        // given
        when(likeRepository.countByTargetIdAndTargetType(10L, LikeTargetType.REVIEW)).thenReturn(5L);

        // when
        long count = likeService.countLikes(10L, LikeTargetType.REVIEW);

        // then
        assertThat(count).isEqualTo(5L);
    }
}
