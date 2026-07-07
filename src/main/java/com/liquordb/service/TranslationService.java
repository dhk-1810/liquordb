package com.liquordb.service;

import com.liquordb.dto.translation.CommentTranslationResponseDto;
import com.liquordb.dto.translation.ReviewTranslationResponseDto;
import com.liquordb.entity.Comment;
import com.liquordb.entity.Review;
import com.liquordb.exception.comment.CommentNotFoundException;
import com.liquordb.exception.review.ReviewNotFoundException;
import com.liquordb.repository.comment.CommentRepository;
import com.liquordb.repository.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;

@Service
@RequiredArgsConstructor
public class TranslationService {

    private final TranslateClient translateClient;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public ReviewTranslationResponseDto translateReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        String targetLanguage = determineTargetLanguage(review.getContent());

        String translatedTitle = translateText(review.getTitle(), targetLanguage);
        String translatedContent = translateText(review.getContent(), targetLanguage);

        return new ReviewTranslationResponseDto(
                review.getId(),
                review.getTitle(),
                review.getContent(),
                translatedTitle,
                translatedContent,
                targetLanguage
        );
    }

    @Transactional(readOnly = true)
    public CommentTranslationResponseDto translateComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        String targetLanguage = determineTargetLanguage(comment.getContent());
        String translatedContent = translateText(comment.getContent(), targetLanguage);

        return new CommentTranslationResponseDto(
                comment.getId(),
                comment.getContent(),
                translatedContent,
                targetLanguage
        );
    }

    private String translateText(String text, String targetLanguage) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        TranslateTextRequest request = TranslateTextRequest.builder()
                .text(text)
                .sourceLanguageCode("auto")
                .targetLanguageCode(targetLanguage)
                .build();

        TranslateTextResponse response = translateClient.translateText(request);
        return response.translatedText();
    }

    private String determineTargetLanguage(String text) {
        if (text == null) {
            return "en";
        }
        // 텍스트에 한글 유니코드 블록이 포함되어 있는지 확인
        for (char c : text.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_SYLLABLES ||
                    Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_JAMO ||
                    Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO) {
                return "en"; // 한국어가 포함되어 있다면 영어로 번역
            }
        }
        return "ko"; // 한국어가 없다면 한국어로 번역 (영어 -> 한국어)
    }
}
