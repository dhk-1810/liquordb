package com.liquordb.dto;

import org.springframework.data.domain.Slice;

import java.util.List;

public record CursorPageResponse<T> (
        List<T> content,
        Object nextCursor,
        Long idAfter,
        int size,
        boolean hasNext
) {
    public static <T> CursorPageResponse<T> from(Slice<T> slice, Object nextCursor, Long idAfter) {
        return new CursorPageResponse<>(
                slice.getContent(),
                nextCursor,
                idAfter,
                slice.getSize(),
                slice.hasNext()
        );
    }

    public static <T> CursorPageResponse<T> from(Slice<T> slice, Object nextCursor) {
        return from(slice, nextCursor, null);
    }
}
