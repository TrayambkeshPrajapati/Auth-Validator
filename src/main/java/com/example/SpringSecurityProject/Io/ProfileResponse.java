package com.example.SpringSecurityProject.Io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponse {
    private String userId;
    private String username;
    private String email;
    private boolean isAccountVerified;
}
