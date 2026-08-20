package com.smartspend.auth.repository;

import com.smartspend.auth.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    List<UserSession> findAllByUserIdOrderByLastActiveAtDesc(Long userId);

    long deleteByUserId(Long userId);
}
