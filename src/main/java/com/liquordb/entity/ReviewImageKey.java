package com.liquordb.entity;

import com.liquordb.entity.id.ReviewImageKeyId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "review_image_key")
public class ReviewImageKey {

    @EmbeddedId
    private ReviewImageKeyId id;

}
