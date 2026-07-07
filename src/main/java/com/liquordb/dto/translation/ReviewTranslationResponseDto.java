package com.liquordb.dto.translation;

public record ReviewTranslationResponseDto(
        Long reviewId,
        String originalTitle,
        String originalContent,
        String translatedTitle,
        String translatedContent,
        String targetLanguage
) {
}
