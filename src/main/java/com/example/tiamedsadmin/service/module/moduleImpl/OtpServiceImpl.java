package com.example.tiamedsadmin.service.module.moduleImpl;

import com.example.tiamedsadmin.dto.module.OtpRequestDto;
import com.example.tiamedsadmin.dto.module.OtpResponseDto;
import com.example.tiamedsadmin.dto.module.OtpVerifyDto;
import com.example.tiamedsadmin.entity.module.Otp;
import com.example.tiamedsadmin.entity.module.TiamedsModule;
import com.example.tiamedsadmin.exception.ApplicationException;
import com.example.tiamedsadmin.exception.NotFoundException;
import com.example.tiamedsadmin.repository.module.OtpRepository;
import com.example.tiamedsadmin.repository.module.TiamedsModuleRepository;
import com.example.tiamedsadmin.service.module.OtpService;
import com.example.tiamedsadmin.utility.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final long RETRY_LIMIT = 3;

    private final OtpRepository otpRepository;
    private final TiamedsModuleRepository tiamedsModuleRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public OtpResponseDto sendOtp(OtpRequestDto dto) {
        TiamedsModule module = tiamedsModuleRepository.findByModuleNameIgnoreCase(dto.getModuleName())
                .orElseThrow(() -> new NotFoundException("Module not found: " + dto.getModuleName()));

        String otpCode = generateOtp();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryAt = now.plusMinutes(OTP_EXPIRY_MINUTES);

        Otp otp = new Otp();
        otp.setModuleId(module);
        otp.setOtp(otpCode);
        otp.setRecipient(dto.getRecipient());
        otp.setDeliveryType(dto.getDeliveryType().toUpperCase());
        otp.setIssuedAt(now);
        otp.setExpiryAt(expiryAt);
        otp.setRetryLimit(RETRY_LIMIT);
        otp.setLocked(false);
        otp.setUsed(false);

        otpRepository.save(otp);

        deliver(dto.getDeliveryType().toUpperCase(), dto.getRecipient(), otpCode, module.getModuleName());

        return new OtpResponseDto("OTP sent successfully", expiryAt);
    }

    @Override
    @Transactional
    public void verifyOtp(OtpVerifyDto dto) {
        TiamedsModule module = tiamedsModuleRepository.findByModuleNameIgnoreCase(dto.getModuleName())
                .orElseThrow(() -> new NotFoundException("Module not found: " + dto.getModuleName()));

        Otp otp = otpRepository
                .findTopByRecipientAndModuleId_ModuleIdAndIsUsedFalseAndIsLockedFalseOrderByIssuedAtDesc(
                        dto.getRecipient(), module.getModuleId())
                .orElseThrow(() -> new NotFoundException("No active OTP found for: " + dto.getRecipient()));

        if (LocalDateTime.now().isAfter(otp.getExpiryAt())) {
            throw new ApplicationException("OTP has expired. Please request a new one.");
        }

        if (!otp.getOtp().equals(dto.getOtp())) {
            long remaining = otp.getRetryLimit() - 1;
            otp.setRetryLimit(remaining);
            if (remaining <= 0) {
                otp.setLocked(true);
                otp.setLockedAt(LocalDateTime.now());
                otpRepository.save(otp);
                throw new ApplicationException("Too many incorrect attempts. OTP is locked.");
            }
            otpRepository.save(otp);
            throw new ApplicationException("Invalid OTP. " + remaining + " attempt(s) remaining.");
        }

        otp.setUsed(true);
        otp.setUsedAt(LocalDateTime.now());
        otpRepository.save(otp);
    }

    private void deliver(String deliveryType, String recipient, String otpCode, String moduleName) {
        switch (deliveryType) {
            case "EMAIL" -> {
                String body = buildEmailBody(otpCode, moduleName);
                emailService.sendHtmlMail(recipient, "Your OTP - TiaMeds " + moduleName, body);
            }
            case "SMS", "WHATSAPP" ->
                log.info("SMS/WhatsApp OTP delivery not yet integrated. OTP for {}: {}", recipient, otpCode);
            default -> throw new ApplicationException("Unsupported delivery type: " + deliveryType);
        }
    }

    private String buildEmailBody(String otpCode, String moduleName) {
        return """
                <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                    <p>Dear User,</p>
                    <p>Your One-Time Password (OTP) for <b>TiaMeds %s</b> is:</p>
                    <h2 style="letter-spacing: 6px; color: #1a73e8;">%s</h2>
                    <p>This OTP is valid for <b>%d minutes</b>. Do not share it with anyone.</p>
                    <p>If you did not request this, please ignore this email.</p>
                    <p>Warm Regards,<br>TiaMeds Team</p>
                </body>
                </html>
                """.formatted(moduleName, otpCode, OTP_EXPIRY_MINUTES);
    }

    private String generateOtp() {
        return String.format("%06d", new SecureRandom().nextInt(1_000_000));
    }
}
