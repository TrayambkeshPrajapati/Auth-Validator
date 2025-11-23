package com.example.SpringSecurityProject.Io;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String email;
    private String token;
}
