package com.gcu.va.repository;

import com.gcu.va.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VeteranRepository extends JpaRepository<Veteran, Long> {

	Veteran findByUserId(Long userId);
}
