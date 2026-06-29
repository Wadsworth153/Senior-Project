package com.gcu.va.repository;

import com.gcu.va.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByVeteranId(Long veteranId);
}
