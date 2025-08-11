package com.liquordb.user.repository;

import com.liquordb.user.entity.UserLevel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLevelRepository extends JpaRepository<UserLevel, String> {
}
