package com.liquordb.user.repository;

import com.liquordb.user.entity.User;
import com.liquordb.user.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 전체 유저 조회 (관리자용)
    List<User> findAll();
    Optional<User> findByIdAndIsDeletedFalse(Long id);

    // 가입된 사용자의 User객체 반환
    Optional<User> findByEmailAndIsDeletedFalse(String email);

    // 이미 가입된 이메일인지 확인 (회원가입 시)
    boolean existsByEmailAndIsDeletedFalse(String email);

    @Query("""
        SELECT u FROM User u
        WHERE (:keyword IS NULL OR u.email LIKE %:keyword% OR u.nickname LIKE %:keyword%)
        AND (:status IS NULL OR u.status = :status)
    """)
    List<User> search(@Param("keyword") String keyword, @Param("status") UserStatus status);
}