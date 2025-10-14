package com.liquordb.repository;

import com.liquordb.entity.User;
import com.liquordb.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // 유저 단건 조회
//    Optional<User> findById(UUID id);

    // 전체 유저 조회 (관리자용)
    List<User> findAll();

    Optional<User> findByIdAndStatusNot(UUID id, UserStatus status);

    // 가입된 사용자의 User객체 반환
    Optional<User> findByEmailAndStatusNot(String email, UserStatus status);

    // 이미 가입된 이메일인지 확인 (회원가입 시)
    boolean existsByEmailAndStatusNot(String email, UserStatus status);

    @Query("""
        SELECT u FROM User u
        WHERE (:keyword IS NULL OR u.email LIKE %:keyword% OR u.nickname LIKE %:keyword%)
        AND (:status IS NULL OR u.status = :status)
    """)
    List<User> search(@Param("keyword") String keyword, @Param("status") UserStatus status);
}