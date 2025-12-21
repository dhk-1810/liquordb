package com.liquordb.entity;

import com.liquordb.dto.tag.TagRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    private boolean isDeleted = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "tag")
    private Set<LiquorTag> liquorTags = new HashSet<>();

    @OneToMany(mappedBy = "tag")
    private Set<UserTag> userTags = new HashSet<>();

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void update(TagRequestDto request) {
        this.name = request.name();
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete(){
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void restore(){
        this.isDeleted = false;
        this.deletedAt = null;
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Tag(String name) {
        this.name = name;
    }

    public static Tag create(String name){
        return Tag.builder()
                .name(name)
                .build();
    }

}