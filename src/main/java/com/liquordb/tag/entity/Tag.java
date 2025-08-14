package com.liquordb.tag.entity;

import com.liquordb.liquor.entity.LiquorTag;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {
    @Id
    @Column
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @OneToMany(mappedBy = "tag")
    private Set<LiquorTag> liquorTags = new HashSet<>();

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL)
    private List<UserTagPreference> userTagPreferences = new ArrayList<>();
}