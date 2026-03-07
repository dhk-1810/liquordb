package com.liquordb.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CommentLikeId implements Serializable {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "comment_id")
    private Long commentId;
}
