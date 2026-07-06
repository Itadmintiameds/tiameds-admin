package com.example.tiamedsadmin.repository.auth;

import com.example.tiamedsadmin.entity.auth.RefreshToken;
import com.example.tiamedsadmin.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true, rt.revokedAt = :now " +
            "WHERE rt.user = :user AND rt.revoked = false")
    void revokeAllByUser(@Param("user") User user, @Param("now") LocalDateTime now);
}
