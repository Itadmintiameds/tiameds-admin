package com.example.tiamedsadmin.service.phramaInventory.phramaInventoryImpl;

import com.example.tiamedsadmin.dto.pharmaInventory.InventoryPharmacyResponseDto;
import com.example.tiamedsadmin.dto.pharmaInventory.PharmacyStatusReviewDto;
import com.example.tiamedsadmin.entity.pharmaInventory.PharmacyRegistrationDetails;
import com.example.tiamedsadmin.entity.pharmaInventory.PharmacyStatusReview;
import com.example.tiamedsadmin.exception.ApplicationException;
import com.example.tiamedsadmin.exception.NotFoundException;
import com.example.tiamedsadmin.repository.pharmaInventory.PharmacyRegistrationDetailsRepository;
import com.example.tiamedsadmin.service.phramaInventory.PharmacyAdminService;
import com.example.tiamedsadmin.utility.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PharmacyAdminServiceImpl implements PharmacyAdminService {

    public static final String SUPPORT_TIAMEDS_COM = "support@tiameds.com";

    @Value("${app.inventory-login-url}")
    public String LOGIN_URL;

    private final PharmacyRegistrationDetailsRepository pharmacyRegistrationDetailsRepository;
    private final EmailService emailService;

    @Autowired
    @Qualifier("inventoryWebClient")
    private final WebClient webClient;

    @Override
    @Transactional
    public void reviewPharmacy(PharmacyStatusReviewDto dto) {
        PharmacyRegistrationDetails existing = pharmacyRegistrationDetailsRepository.findById(dto.getRegistrationId())
                .orElseThrow(() -> new NotFoundException("Pharmacy Registration not found with id: " + dto.getRegistrationId()));

        switch (dto.getStatus().toUpperCase()) {

            case "CORRECTION" -> handleCorrection(existing, dto.getRemark());

            case "REJECT" -> handleRejection(existing, dto.getRemark());

            case "ACCEPT" -> handleApproval(existing, dto.getRemark());

            default -> throw new ApplicationException("Invalid Status");
        }

    }

    private void handleApproval(PharmacyRegistrationDetails existing, String remark) {
        PharmacyStatusReview pharmacyStatusReview = new PharmacyStatusReview();
        pharmacyStatusReview.setStatus("ACCEPT");
        pharmacyStatusReview.setRemark(remark);
        pharmacyStatusReview.setStatusDate(LocalDateTime.now());
        pharmacyStatusReview.setReviewedBy("admin"); // Set the admin username or ID here
        pharmacyStatusReview.setPharmacy_registration_id(existing);

        existing.getPharmacyStatusReview().add(pharmacyStatusReview);
        pharmacyRegistrationDetailsRepository.save(existing);

        // Call inventory service to create the pharmacy
        createPharmacyInInventory(existing);

        // HTML Email Body
        String body = """
                <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                
                    <p>Dear %s,</p>
                
                    <p>
                        We are pleased to inform you that your pharmacy registration on the
                        <b>TiaMeds platform</b> has been <b>approved</b>!
                        Your Registration ID is <b>%s</b>.
                    </p>
                
                    <p>
                        You can now log in to your account and start managing your pharmacy inventory 
                        on the TiaMeds Marketplace.
                    </p>
                
                    <p>
                        <a href="%s" style="color: #1a73e8; text-decoration: none;">Click Here</a> to login
                    </p>
                
                    <p>
                        If you have any questions or need assistance getting started, 
                        please contact our support team at 
                        <a href="mailto:%s">%s</a>.
                    </p>
                
                    <p>
                        Warm Regards,<br>
                        TiaMeds<br>
                        Pharmacy Onboarding & Compliance Team
                    </p>
                
                </body>
                </html>
                """.formatted(
                existing.getPharmacyName(),
                existing.getPharmacyRegistrationId(),
                LOGIN_URL,
                SUPPORT_TIAMEDS_COM,
                SUPPORT_TIAMEDS_COM
        );

        emailService.sendHtmlMail(
                existing.getPharmacyEmail(),
                "Congratulations! Pharmacy Registration Approved - TiaMeds",
                body
        );
    }

    private void createPharmacyInInventory(PharmacyRegistrationDetails existing) {
        // Build documents list from existing pharmacy documents
        List<Map<String, Object>> documents = existing.getPharmacyRegistrationDocuments().stream()
                .map(doc -> {
                    Map<String, Object> docMap = new HashMap<>();
                    docMap.put("documentNo", doc.getDocumentNumber());
                    docMap.put("documentType", doc.getDocumentType());
                    docMap.put("documentUrl", doc.getDocumentUrl());
                    docMap.put("issueDate", doc.getIssueDate());
                    docMap.put("issueAuthority", doc.getIssueAuthority());
                    docMap.put("expiryDate", doc.getExpiryDate());
                    return docMap;
                })
                .toList();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("pharmacyRegistrationId", existing.getPharmacyRegistrationId());
        requestBody.put("userId", existing.getUserId());
        requestBody.put("pharmacyName", existing.getPharmacyName());
        requestBody.put("pharmacyType", existing.getPharmacyType());
        requestBody.put("pharmacyEmail", existing.getPharmacyEmail());
        requestBody.put("pharmacyPhone", existing.getPharmacyPhone());
        requestBody.put("panNumber", existing.getPanNumber());
        requestBody.put("gstNumber", existing.getGstNumber());
        requestBody.put("pharmacyBranch", existing.getPharmacyBranch());
        requestBody.put("pharmacyBuildingNo", existing.getPharmacyBuildingNo());
        requestBody.put("pharmacyStreet", existing.getPharmacyStreet());
        requestBody.put("pharmacyCity", existing.getPharmacyCity());
        requestBody.put("pharmacyTaluka", existing.getPharmacyTaluka());
        requestBody.put("pharmacyDistricts", existing.getPharmacyDistricts());
        requestBody.put("pharmacyPincode", existing.getPharmacyPincode());
        requestBody.put("pharmacyLandmark", existing.getPharmacyLandmark());
        requestBody.put("pharmacyState", existing.getPharmacyState());
        requestBody.put("organizationId", existing.getOrganizationId());
        requestBody.put("organizationName", existing.getOrganizationName());
        requestBody.put("organizationType", existing.getOrganizationType());
        requestBody.put("ownershipType", existing.getOwnershipType());
        requestBody.put("organizationPanNumber", existing.getOrganizationPanNumber());
        requestBody.put("organizationGstNumber", existing.getOrganizationGstNumber());
        requestBody.put("documents", documents);

        InventoryPharmacyResponseDto response = webClient.post()
                .uri("/api/v1/pharmacy/create")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(InventoryPharmacyResponseDto.class)
                .doOnSuccess(v -> log.info("Pharmacy created in inventory: {}", existing.getPharmacyRegistrationId()))
                .doOnError(e -> log.error("Inventory create failed for pharmacy {}: {}", existing.getPharmacyRegistrationId(), e.getMessage()))
                .block();

        if (response == null || response.getPharmacyId() == null) {
            throw new ApplicationException("Inventory service did not return a pharmacy ID");
        }

        existing.setPharmacyId(response.getPharmacyId());
        pharmacyRegistrationDetailsRepository.save(existing);
        log.info("Pharmacy ID {} saved for registration {}", response.getPharmacyId(), existing.getPharmacyRegistrationId());
    }

    private void handleRejection(PharmacyRegistrationDetails existing, String remark) {
        PharmacyStatusReview pharmacyStatusReview = new PharmacyStatusReview();
        pharmacyStatusReview.setStatus("REJECT");
        pharmacyStatusReview.setRemark(remark);
        pharmacyStatusReview.setStatusDate(LocalDateTime.now());
        pharmacyStatusReview.setReviewedBy("admin");
        pharmacyStatusReview.setPharmacy_registration_id(existing);

        existing.getPharmacyStatusReview().add(pharmacyStatusReview);

        existing.getPharmacyRegistrationDocuments().forEach(doc -> {
            doc.setVerified(false);
            doc.setActive(false);
        });
        pharmacyRegistrationDetailsRepository.save(existing);

        // Call inventory service to mark the user/organization as rejected
        rejectUserInInventory(existing);

        // HTML Email Body
        String body = """
                <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                
                    <p>Dear %s,</p>
                
                    <p>
                        Thank you for submitting your pharmacy registration on the
                        <b>TiaMeds platform</b>. Your Registration ID is <b>%s</b>.
                    </p>
                
                    <p>
                        After a thorough review by our compliance team, we regret to inform you that 
                        your pharmacy registration application has been <b>rejected</b>.
                    </p>
                
                    <p>Reason for rejection:<br>
                    <b>%s</b></p>
                
                    <p>
                        If you believe this decision was made in error or have any questions, 
                        please reach out to our support team at 
                        <a href="mailto:%s">%s</a>.
                    </p>
                
                    <p>
                        Warm Regards,<br>
                        TiaMeds<br>
                        Pharmacy Onboarding & Compliance Team
                    </p>
                
                </body>
                </html>
                """.formatted(
                existing.getPharmacyName(),
                existing.getPharmacyRegistrationId(),
                remark,
                SUPPORT_TIAMEDS_COM,
                SUPPORT_TIAMEDS_COM
        );

        emailService.sendHtmlMail(
                existing.getPharmacyEmail(),
                "Pharmacy Registration Rejected - TiaMeds",
                body
        );
    }

    private void rejectUserInInventory(PharmacyRegistrationDetails existing) {
        webClient.put()
                .uri("/api/v1/organization/reject/" + existing.getUserId())
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("User rejected in inventory: {}", existing.getUserId()))
                .doOnError(e -> log.error("Inventory reject failed for user {}: {}", existing.getUserId(), e.getMessage()))
                .block();
    }

    private void handleCorrection(PharmacyRegistrationDetails existing, String remark) {

        PharmacyStatusReview pharmacyStatusReview = new PharmacyStatusReview();
        pharmacyStatusReview.setStatus("CORRECTION");
        pharmacyStatusReview.setRemark(remark);
        pharmacyStatusReview.setStatusDate(LocalDateTime.now());
        pharmacyStatusReview.setReviewedBy("admin");
        pharmacyStatusReview.setPharmacy_registration_id(existing);

        existing.getPharmacyStatusReview().add(pharmacyStatusReview);
        pharmacyRegistrationDetailsRepository.save(existing);

        // HTML Email Body
        String body = """
                <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                
                    <p>Dear %s,</p>
                
                    <p>
                        Thank you for submitting your pharmacy registration on the 
                        <b>TiaMeds platform</b>. Your Registration ID is <b>%s</b>.
                    </p>
                
                    <p>
                        Our compliance team has reviewed your registration and identified certain items that 
                        require <b>correction or additional information</b> before we can proceed with approval.
                    </p>
                
                    <p>Please review and address the following points:<br>
                    <b>%s</b></p>
                
                    <p>
                        Kindly log in to your account to make the necessary corrections:
                    </p>
                
                    <p>
                        <a href="%s" style="color: #1a73e8; text-decoration: none;">Click Here</a> to login
                    </p>
                
                    <p>
                        Once the corrections are submitted, your registration will be re-evaluated by our compliance team.
                    </p>
                
                    <p>
                        Please note that timely completion of corrections will help us process your registration faster.
                    </p>
                
                    <p>
                        For any assistance, please contact our support team at 
                        <a href="mailto:%s">%s</a>.
                    </p>
                
                    <p>
                        Warm Regards,<br>
                        TiaMeds<br>
                        Pharmacy Onboarding & Compliance Team
                    </p>
                
                </body>
                </html>
                """.formatted(
                existing.getPharmacyName(),       // Dear %s
                existing.getPharmacyRegistrationId(), // Registration ID %s
                remark,                           // correction points %s  <-- added
                LOGIN_URL,                        // href %s
                SUPPORT_TIAMEDS_COM,              // mailto %s
                SUPPORT_TIAMEDS_COM               // email text %s
        );

        emailService.sendHtmlMail(
                existing.getPharmacyEmail(),
                "Action Required: Pharmacy Registration Correction",
                body
        );
    }
}
