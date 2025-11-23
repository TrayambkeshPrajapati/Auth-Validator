package com.example.SpringSecurityProject.Io;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileRequest {
    @NotBlank(message = "Name Should Not be empty")
    private String username;
    @Email(message = "Enter a valid e-mail address")
    @NotNull(message = "Email Should Not be empty")
    private String email;
    @Size(min = 6,message = "Password must be at least 6 characters")
    private String password;
}