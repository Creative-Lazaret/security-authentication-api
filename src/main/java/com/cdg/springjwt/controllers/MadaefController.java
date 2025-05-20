package com.cdg.springjwt.controllers;

import com.cdg.springjwt.models.Collaborateur;
import com.cdg.springjwt.models.Filiale;
import com.cdg.springjwt.models.User;
import com.cdg.springjwt.repository.CollaborateurRepository;
import com.cdg.springjwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/madaef")
public class MadaefController {


    @Autowired
    UserRepository userRepository;

    @Autowired
    CollaborateurRepository collaborateurRepository;


    @GetMapping("/myCollaborateurs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCollaborateurs(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("ERROR: user not found"));
        List<Long> filiales = user.getFiliales().stream().map(Filiale::getId).collect(Collectors.toList());
        Page<Collaborateur> employesParFiliale = getEmployesParFiliale(filiales, page, size);

        return ResponseEntity.ok(employesParFiliale);

    }

    public Page<Collaborateur> getEmployesParFiliale(List<Long> filiales, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("colabNom").ascending());
        return collaborateurRepository.findByFiliale_IdIn(filiales, pageable);
    }
}
