package com.liquordb.repository.user;

import com.liquordb.dto.user.UserActivityCountDto;
import com.liquordb.entity.User;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface CustomUserRepository {

    Page<User> findAll(UserSearchCondition condition);

    UserActivityCountDto getUserActivityCounts(UUID userId);
}
