package com.example.tiamedsadmin.repository.module;

import com.example.tiamedsadmin.entity.module.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findTopByRecipientAndModuleId_ModuleIdAndIsUsedFalseAndIsLockedFalseOrderByIssuedAtDesc(
            String recipient, Long moduleId);
}
