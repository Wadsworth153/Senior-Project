package com.gcu.va.service;

import com.gcu.va.dto.RegistrationForm;
import com.gcu.va.entity.User;

public interface RegistrationService {
    User registerVeteran(RegistrationForm form);
}