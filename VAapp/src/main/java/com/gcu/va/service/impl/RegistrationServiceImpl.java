package com.gcu.va.service.impl;

import com.gcu.va.dto.RegistrationForm;
import com.gcu.va.entity.*;
import com.gcu.va.repository.*;
import com.gcu.va.service.RegistrationService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final VeteranRepository veteranRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationServiceImpl(UserRepository userRepository,
                                   VeteranRepository veteranRepository,
                                   PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.veteranRepository = veteranRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerVeteran(RegistrationForm form) {

        // 1. Validate passwords match
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // 2. Check username exists
        if (userRepository.findByUsername(form.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }

        // 3. Create User
        User user = new User();
        user.setUsername(form.getUsername());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setRole(Role.ROLE_VETERAN);

        user = userRepository.save(user);

        // 4. Create Veteran
        Veteran veteran = new Veteran();
        veteran.setFirstName(form.getFirstName());
        veteran.setLastName(form.getLastName());
        veteran.setEmail(form.getEmail());
        veteran.setDateOfBirth(form.getDateOfBirth());
        veteran.setBranchOfService(form.getBranchOfService());
        veteran.setServiceStartDate(form.getServiceStartDate());
        veteran.setServiceEndDate(form.getServiceEndDate());
        veteran.setDischargeStatus(form.getDischargeStatus());

        // 5. Link user → veteran
        veteran.setUser(user);

        // 6. Save veteran
        veteranRepository.save(veteran);
        
        return user;
    }
}