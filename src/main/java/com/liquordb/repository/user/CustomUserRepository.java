package com.liquordb.repository.user;

import com.liquordb.entity.User;
import org.springframework.data.domain.Page;

public interface CustomUserRepository {

    Page<User> findAll(UserSearchCondition condition);
}
