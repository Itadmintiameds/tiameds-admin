package com.example.tiamedsadmin.service.phramaInventory.phramaInventoryImpl;

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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PharmacyAdminServiceImpl implements PharmacyAdminService {

    private final PharmacyRegistrationDetailsRepository pharmacyRegistrationDetailsRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public void reviewPharmacy(PharmacyStatusReviewDto dto) {
        PharmacyRegistrationDetails existing = pharmacyRegistrationDetailsRepository.findById(dto.getRegistrationId())
                .orElseThrow(() -> new NotFoundException("Pharmacy Registration not found with id: " + dto.getRegistrationId()));

        switch (dto.getStatus().toUpperCase()) {

            case "CORRECTION" -> handleCorrection(existing, dto.getRemark());

            case "REJECT" -> handleRejection(existing, dto.getRemark());

//            case "ACCEPT" -> handleApprovalForTempSeller(existing, dto.getRemark()());

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

        // call the inventory API and send mail
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

        // send rejection mail to pharmacy owner
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

        // send mail to pharmacy owner for correction
    }
}
