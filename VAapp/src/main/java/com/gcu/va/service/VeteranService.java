package com.gcu.va.service;

import com.gcu.va.entity.*;
import java.util.List;

public interface VeteranService {
    Veteran saveVeteran(Veteran veteran);
    List<Veteran> getAllVeterans();
}
