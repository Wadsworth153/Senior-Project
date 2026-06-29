package com.gcu.va.service.impl;

import com.gcu.va.entity.*;
import com.gcu.va.repository.*;
import com.gcu.va.service.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VeteranServiceImpl implements VeteranService {

    private final VeteranRepository repository;

    public VeteranServiceImpl(VeteranRepository repository) {
        this.repository = repository;
    }

    @Override
    public Veteran saveVeteran(Veteran veteran) {
    	System.out.println("SERVICE SAVE: " + veteran);

        Veteran saved = repository.save(veteran);

        System.out.println("SAVED ENTITY: " + saved);

        return saved;
    }

    @Override
    public List<Veteran> getAllVeterans() {
        return repository.findAll();
    }
}