
package com.gcu.va.service;

import com.gcu.va.entity.BenefitStatus;
import com.gcu.va.entity.BenefitType;
import com.gcu.va.entity.Claim;
import com.gcu.va.entity.ClaimStatus;
import com.gcu.va.entity.ClaimUpdate;
import com.gcu.va.repository.ClaimRepository;
import com.gcu.va.repository.ClaimUpdateRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimUpdateRepository claimUpdateRepository;

    public ClaimService(
            ClaimRepository claimRepository,
            ClaimUpdateRepository claimUpdateRepository) {
        this.claimRepository = claimRepository;
        this.claimUpdateRepository = claimUpdateRepository;
    }

    public List<Claim> getClaimsForVeteran(Long veteranId) {
        return claimRepository.findByVeteranId(veteranId);
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll()
                .stream()
                .filter(claim -> claim != null)
                .toList();
    }

    public Claim saveClaim(Claim claim) {
        return claimRepository.save(claim);
    }

    
    public Claim getById(Long id) {
        return claimRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Claim not found with id: " + id));
    }
  


    @Transactional
    public Claim createBenefitTracker(
            Claim claim,
            BenefitType benefitType,
            BenefitStatus benefitStatus,
            String notes,
            LocalDate followUpDate) {

        claim.setBenefitType(benefitType);
        claim.setBenefitStatus(benefitStatus);
        claim.setStatus(mapToClaimStatus(benefitStatus));

        if (claim.getSubmittedDate() == null) {
            claim.setSubmittedDate(LocalDate.now());
        }

        Claim savedClaim = claimRepository.save(claim);

        ClaimUpdate initialUpdate = new ClaimUpdate();
        initialUpdate.setStatus(savedClaim.getStatus());
        initialUpdate.setBenefitStatus(benefitStatus);
        initialUpdate.setUpdateDate(LocalDate.now());
        initialUpdate.setNotes(notes);
        initialUpdate.setFollowUpDate(followUpDate);
        initialUpdate.setClaim(savedClaim);

        claimUpdateRepository.save(initialUpdate);

        return savedClaim;
    }

    @Transactional
    public ClaimUpdate addBenefitUpdate(
            Long claimId,
            BenefitStatus newStatus,
            LocalDate updateDate,
            String notes,
            LocalDate followUpDate) {

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Benefit tracker not found: " + claimId));

        claim.setBenefitStatus(newStatus);
        claim.setStatus(mapToClaimStatus(newStatus));

        ClaimUpdate update = new ClaimUpdate();
        update.setStatus(claim.getStatus());
        update.setBenefitStatus(newStatus);
        update.setUpdateDate(
                updateDate != null ? updateDate : LocalDate.now());
        update.setNotes(notes);
        update.setFollowUpDate(followUpDate);
        update.setClaim(claim);

        claimRepository.save(claim);

        return claimUpdateRepository.save(update);
    }

    @Transactional(readOnly = true)
    public List<ClaimUpdate> getBenefitUpdates(Long claimId) {
        return claimUpdateRepository
                .findByClaimIdOrderByUpdateDateDesc(claimId);
    }

    private ClaimStatus mapToClaimStatus(BenefitStatus benefitStatus) {
        if (benefitStatus == null) {
            return ClaimStatus.UNDER_REVIEW;
        }

        switch (benefitStatus) {
            case APPROVED:
            case COMPLETE:
                return ClaimStatus.APPROVED;

            case DENIED:
                return ClaimStatus.DENIED;

            default:
                return ClaimStatus.UNDER_REVIEW;
        }
    }
}

