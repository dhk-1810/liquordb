package com.liquordb.dto;

import jakarta.annotation.Nullable;

public record NotificationListGetRequest(

        @Nullable
        Long cursor
) {
}
