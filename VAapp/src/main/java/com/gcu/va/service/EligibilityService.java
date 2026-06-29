package com.gcu.va.service;

import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com.gcu.va.entity.Veteran;

@Service
public class EligibilityService {

    public String calculateStatus(Veteran v) {
        if (v.getServiceStartDate() == null || v.getServiceEndDate() == null) {
            return "Ineligible";
        }

        long months = ChronoUnit.MONTHS.between(
            v.getServiceStartDate(),
            v.getServiceEndDate()
        );

        if (months >= 24 && "Honorable".equalsIgnoreCase(v.getDischargeStatus())) {
            return "Eligible";
        }

        return "Not Eligible";
    }
}
