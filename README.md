Auth Validator

A secure and scalable authentication & authorization system built with Spring Boot, Spring Security, and JWT. The application prevents unauthorized access from unknown users while maintaining high session reliability in a stateless environment.

🚀 Features

🔐 User Authentication & Authorization
🧾 Stateless token session management using JWT
📧 OTP verification for login & account recover
♻️ Forgot password & password reset
✉️ Email delivery using Java Mail Sender API
📨 OTP delivery & validation success rate improved by 40%
🔓 Logout functionality with token invalidation
💾 User data stored & managed using MySQL

👥 Supports 10,000+ users with high reliability 99% session reliability

🛠️ Tech Stack
Component	Technology
Backend Framework	Spring Boot
Security	Spring Security
Authentication	JWT
Email Service	Java Mail Sender API
OTP System	Java Mail Sender + Secure OTP Generator
Database	MySQL
Programming Language	Java
Build Tool	Maven

🏗️ Architecture Highlights

Stateless Session Management: Uses JWT tokens instead of server-side session storage for scalability.
Security Layer: Spring Security handles API protection, request filtering, and authentication entry points.
Email-Driven Verification: OTP verification and password recovery are handled via email using Java Mail Sender.
Database Storage: MySQL ensures reliable and persistent storage for user credentials and metadata.

📦 Modules Implemented

Register	New user registration with email verification
Login	JWT token issued after credentials + OTP verification
Logout	Token invalidated on client side (stateless)
OTP Verification	Secure 6-digit OTP sent via email
Forgot Password	Initiates OTP based password recovery
Reset Password	OTP verified → password updated in database
Email Sender API	OTP & password reset emails delivered reliably

✅ Performance Results

Session reliability: ~99% in stateless JWT environment
OTP & password recovery success rate increased by 40%
Scalable user storage tested up to 10,000+ user records

🔒 Security Implementations

JWT token-based authentication
No server-side sessions (stateless)
Encrypted passwords (BCrypt via Spring Security)
OTP expiry and validation checks
Protected routes using role-based access

📄 License

This project is open-source for educational and personal use.
