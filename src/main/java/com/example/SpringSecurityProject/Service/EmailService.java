package com.example.SpringSecurityProject.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.properties.mail.smtp.from}")
    private String froEmail;

    public void sendWelcomeEmail(String toEmail,String name){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(froEmail);
        message.setTo(toEmail);
        message.setSubject("Welcome To Our Platform");
        message.setText("Hello "+ name+" ,\n Thanks for registering with us.\n\n Regards,\n DreadKing Team");
        mailSender.send(message);
    }

    public void sendResetOtpEmail(String toEmail, String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(froEmail);
        message.setTo(toEmail);
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP for resetting the password is "+ otp);
        mailSender.send(message);
    }
    public void sendOtpEmail(String toEmail, String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(froEmail);
        message.setTo(toEmail);
        message.setSubject("Account Verification OTP");
        message.setText("Your OTP for account Verification is "+ otp);
        mailSender.send(message);
    }
}
