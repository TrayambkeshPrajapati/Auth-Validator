package com.example.SpringSecurityProject.Service;

import com.example.SpringSecurityProject.Entity.UserEntity;
import com.example.SpringSecurityProject.Io.ProfileRequest;
import com.example.SpringSecurityProject.Io.ProfileResponse;
import com.example.SpringSecurityProject.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ProfileServiceImplementation implements ProfileService{
    @Autowired
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    @Override
    public ProfileResponse createProfile(ProfileRequest request) {
        UserEntity newProfile = convertTouserEntity(request);
        if(!userRepository.existsByEmail(request.getEmail())){
            newProfile = userRepository.save(newProfile);
            return covertToFileResponse(newProfile);
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already exists");
    }

    @Override
    public ProfileResponse getProfile(String email) {
        UserEntity existingUser = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User Not Found" + email));
        return covertToFileResponse(existingUser);
    }

    @Override
    public void sentResetOtp(String email) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found " + email));
        //OTP generator
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));
        long expireTime = System.currentTimeMillis() + (15 * 60 * 1000);
        existingUser.setResetOtp(otp);
        existingUser.setResetOtpExpireAt(expireTime);

        userRepository.save(existingUser);
        try {
            emailService.sendResetOtpEmail(existingUser.getEmail(), otp);
        }catch (Exception e){
            throw new RuntimeException("Unable to send email");
        }
    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not Found " +
                        "with the email " + email));
        if(existingUser.getResetOtp() == null || !existingUser.getResetOtp().equals(otp)){
            throw new RuntimeException("Invalid OTP");
        }
        if(existingUser.getResetOtpExpireAt() < System.currentTimeMillis()){
            throw new RuntimeException("OTP expired");
        }
        existingUser.setPassword(passwordEncoder.encode(newPassword));
        existingUser.setResetOtp(null);
        existingUser.setResetOtpExpireAt(0L);

        userRepository.save(existingUser);
    }

    @Override
    public void sendOtp(String email) {
        UserEntity existingUser =  userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("Not Found" + email));
            if(existingUser.getIsAccountVerified() != null && existingUser.getIsAccountVerified()){
                return;
            }
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));
        long expireTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000);
        existingUser.setVerifyOtp(otp);
        existingUser.setVerifyOtpExpiredAt(expireTime);
        userRepository.save(existingUser);
        try {
            emailService.sendOtpEmail(existingUser.getEmail(), otp);
        }catch (Exception e){
            throw new RuntimeException("Unable to send email");
        }
    }

    @Override
    public void verifyOtp(String email, String otp) {
        UserEntity existingUser =  userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("Not Found" + email));
        if(existingUser.getVerifyOtp() == null || !existingUser.getVerifyOtp().equals(otp)){
            throw new RuntimeException("Invalid OTP");
        }
        if(existingUser.getVerifyOtpExpiredAt() < System.currentTimeMillis()){
            throw new RuntimeException("OTP expired");
        }
        existingUser.setIsAccountVerified(true);
        existingUser.setVerifyOtp(null);
        existingUser.setVerifyOtpExpiredAt(0L);
        userRepository.save(existingUser);
    }

    private ProfileResponse covertToFileResponse(UserEntity newProfile){
        return ProfileResponse.builder()
                .username(newProfile.getName())
                .email(newProfile.getEmail())
                .userId(newProfile.getUserId())
                .isAccountVerified(newProfile.getIsAccountVerified())
                .build();
    }

    private UserEntity convertTouserEntity(ProfileRequest request){
        return UserEntity.builder()
                .email(request.getEmail())
                .name(request.getUsername())
                .userId(UUID.randomUUID().toString())
                .password(passwordEncoder.encode(request.getPassword()))
                .isAccountVerified(false)
                .resetOtpExpireAt(0L)
                .verifyOtp(null)
                .verifyOtpExpiredAt(0L)
                .resetOtp(null)
                .build();
    }
}