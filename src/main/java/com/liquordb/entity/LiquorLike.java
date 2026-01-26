package com.liquordb.entity;

import com.liquordb.entity.id.LiquorLikeId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@IdClass(LiquorLikeId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "liquor_likes")
public class LiquorLike {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "liquor_id")
    private Liquor liquor;

    @Builder
    public LiquorLike(User user, Liquor liquor) {
        this.user = user;
        this.liquor = liquor;
    }

    public static LiquorLike create(User user, Liquor liquor) {
        return LiquorLike.builder()
                .user(user)
                .liquor(liquor)
                .build();
    }
}
