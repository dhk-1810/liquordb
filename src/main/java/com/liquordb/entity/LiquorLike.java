package com.liquordb.entity;

import com.liquordb.entity.id.LiquorLikeId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "liquor_likes")
public class LiquorLike {

    @EmbeddedId
    private LiquorLikeId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("liquorId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liquor_id")
    private Liquor liquor;

    public LiquorLike(User user, Liquor liquor) {
        this.id = new LiquorLikeId(user.getId(), liquor.getId());
        this.user = user;
        this.liquor = liquor;
    }

    public static LiquorLike create(User user, Liquor liquor) {
        return new LiquorLike(user, liquor);
    }
}
