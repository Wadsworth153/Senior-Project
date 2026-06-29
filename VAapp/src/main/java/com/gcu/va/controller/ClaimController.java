package com.gcu.va.controller;

import com.gcu.va.entity.Claim;
import com.gcu.va.entity.ClaimStatus;
import com.gcu.va.entity.Role;
import com.gcu.va.entity.User;
import com.gcu.va.service.ClaimService;
import com.gcu.va.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ClaimController {

    private final ClaimService claimService;
    private final UserRepository userRepository;

    public ClaimController(ClaimService claimService, UserRepository userRepository) {
        this.claimService = claimService;
        this.userRepository = userRepository;
    }

    @GetMapping("/claims")
    public String viewClaims(Authentication auth, Model model) {

        User user = userRepository.findByUsername(auth.getName());

        if (user.getRole() == Role.ROLE_ADMIN) {
            model.addAttribute("claims", claimService.getAllClaims());
        } else {
            model.addAttribute("claims",
                    claimService.getClaimsForVeteran(user.getVeteran().getId()));
        }

        return "claims";
    }
    
    @GetMapping("/claims/new")
    public String showCreateForm(Model model) {
        model.addAttribute("claim", new Claim());
        return "create-claim";
    }
    
    @PostMapping("/claims/new")
    public String createClaim(Claim claim, Authentication auth) {

        User user = userRepository.findByUsername(auth.getName());

        // attach claim to logged-in veteran
        claim.setVeteran(user.getVeteran());

        // default status
        claim.setStatus(ClaimStatus.SUBMITTED);

        claim.setSubmittedDate(java.time.LocalDate.now());

        claimService.saveClaim(claim);

        return "redirect:/claims";
    }
    
    @GetMapping("/claims/update")
    public String updateClaimStatus(Long id, String status) {

        Claim claim = claimService.getById(id);

        claim.setStatus(ClaimStatus.valueOf(status));

        claimService.saveClaim(claim);

        return "redirect:/admin";
    }
}
