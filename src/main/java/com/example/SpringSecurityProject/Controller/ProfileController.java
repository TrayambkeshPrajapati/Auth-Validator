package com.example.SpringSecurityProject.Controller;

import com.example.SpringSecurityProject.Io.ProfileRequest;
import com.example.SpringSecurityProject.Io.ProfileResponse;
import com.example.SpringSecurityProject.Service.EmailService;
import com.example.SpringSecurityProject.Service.ProfileServiceImplementation;
import jakarta.persistence.PersistenceContexts;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileServiceImplementation profileServiceImplementation;
    private final EmailService emailService;
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse register(@Valid @RequestBody ProfileRequest request){
        ProfileResponse response = profileServiceImplementation.createProfile(request);
        emailService.sendWelcomeEmail(response.getEmail(), response.getUsername());
        return response;
    }
    @GetMapping("/profile")
    public ProfileResponse getProfile(@CurrentSecurityContext(expression = "authentication.name")String email){
        return profileServiceImplementation.getProfile(email);
    }

}