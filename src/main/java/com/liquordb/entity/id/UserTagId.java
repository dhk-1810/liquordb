package com.liquordb.entity.id;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserTagId implements Serializable {
    private UUID userId;
    private Long tagId;
}
