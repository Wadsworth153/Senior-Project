package com.gcu.va.service;

import com.gcu.va.entity.Claim;
import com.gcu.va.repository.ClaimRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;

    public ClaimService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public List<Claim> getClaimsForVeteran(Long veteranId) {
        return claimRepository.findByVeteranId(veteranId);
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll()
                .stream()
                .filter(c -> c != null)
                .toList();
    }

    public Claim saveClaim(Claim claim) {
        return claimRepository.save(claim);
    }

    public Claim getById(Long id) {
        return claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found with id: " + id));
    }

	
}
