package com.liquordb.repository.user;

import com.liquordb.entity.User;
import com.liquordb.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, CustomUserRepository {

    // 유저 단건 조회
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByIdAndStatus(UUID id, UserStatus status);

    // 유저 존재 확인
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    // 탈퇴된 회원 정보 삭제 시 사용.
    List<User> findAllByStatusAndWithdrawnAtBefore(UserStatus status, LocalDateTime withdrawnAt);

}