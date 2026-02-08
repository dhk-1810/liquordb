package com.liquordb;

import org.springframework.data.domain.Slice;

import java.util.List;

public record CursorPageResponse<T> (
        List<T> content,
        Object nextCursor,
        int size,
        boolean hasNext
) {
    public static <T> CursorPageResponse<T> from(Slice<T> slice, Object nextCursor) {
        return new CursorPageResponse<>(
                slice.getContent(),
                nextCursor,
                slice.getSize(),
                slice.hasNext()
        );
    }
}
