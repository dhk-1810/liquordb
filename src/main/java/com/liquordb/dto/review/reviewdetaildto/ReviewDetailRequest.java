package com.liquordb.dto.review.reviewdetaildto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type" // JSON에 "type": "BEER" 같은 필드가 있어야 함
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BeerReviewRequest.class, name = "BEER"),
        @JsonSubTypes.Type(value = WhiskyReviewRequest.class, name = "WHISKY"),
        @JsonSubTypes.Type(value = WineReviewRequest.class, name = "WINE")
})
public sealed interface ReviewDetailRequest
        permits BeerReviewRequest, WhiskyReviewRequest, WineReviewRequest {
}