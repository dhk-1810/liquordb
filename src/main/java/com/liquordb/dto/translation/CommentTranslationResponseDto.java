package com.liquordb.dto.translation;

public record CommentTranslationResponseDto(
        Long commentId,
        String originalContent,
        String translatedContent,
        String targetLanguage
) {
}
