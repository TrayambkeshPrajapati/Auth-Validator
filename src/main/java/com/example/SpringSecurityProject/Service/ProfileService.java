package com.example.SpringSecurityProject.Service;

import com.example.SpringSecurityProject.Io.ProfileRequest;
import com.example.SpringSecurityProject.Io.ProfileResponse;

public interface ProfileService {
    ProfileResponse createProfile(ProfileRequest request);
    ProfileResponse getProfile(String email);
    void sentResetOtp(String email);
    void resetPassword(String email,String otp,String newPassword);
    void sendOtp(String email);
    void verifyOtp(String email,String otp);
}