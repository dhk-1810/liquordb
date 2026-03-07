package com.liquordb.dto;

import com.liquordb.entity.File;

public record FileResponseDto (
        Long id,
        String key,
        String name,
        File.FileType type
) {
    public static FileResponseDto toDto(File file) {
        return new FileResponseDto(file.getId(), file.getS3key(), file.getName(), file.getType());
    }
}
