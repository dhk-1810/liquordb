package com.liquordb.repository;

import com.liquordb.entity.User;
import com.liquordb.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // 유저 단건 조회
    Optional<User> findByEmail(String email);
    Optional<User> findByIdAndStatusNot(UUID id, UserStatus status);
    Optional<User> findByEmailAndStatusNot(String email, UserStatus status);
    Optional<User> findByIdAndStatus(UUID id, UserStatus status);

    // 탈퇴된 회원 정보 삭제 시 사용.
    List<User> findAllByStatusAndWithdrawnAtBefore(UserStatus status, LocalDateTime withdrawnAt);

    // 이미 가입된 이메일인지 확인 (회원가입 시)
    boolean existsByEmail(String email);

    // 유저 검색. 검색어 없으면 전체 조회 (관리자용)
    @Query("""
        SELECT u FROM User u
        WHERE (:keyword IS NULL OR u.email LIKE %:keyword% OR u.nickname LIKE %:keyword%)
        AND (:status IS NULL OR u.status = :status)
    """)
    List<User> search(@Param("keyword") String keyword, @Param("status") UserStatus status);

}