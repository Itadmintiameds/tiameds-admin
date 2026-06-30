package com.example.tiamedsadmin.entity.module;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "otps")
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "otp_id")
    private Long otpId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private TiamedsModule moduleId;     // ex: Lab, Inventory, Marketplace

    @Column(name = "otp", nullable = false, length = 6)
    private String otp;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "expiry_at")
    private LocalDateTime expiryAt;

    @Column(name = "retry_limit")
    private Long retryLimit;

    @Column(name = "delivery_type", nullable = false, length = 20)
    private String deliveryType;    // ex: SMS, Email, WhatsApp

    @Column(name = "recipient", nullable = false, length = 100)
    private String recipient;   // ex: phone number or email address

    @Column(name = "is_locked", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isLocked;

    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    // Prevent OTP reuse
    @Column(name = "is_used", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isUsed;

    @Column(name = "used_at")
    private LocalDateTime usedAt;
}
