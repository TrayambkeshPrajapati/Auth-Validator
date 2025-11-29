package com.example.SpringSecurityProject.Controller;

import com.example.SpringSecurityProject.Io.AuthRequest;
import com.example.SpringSecurityProject.Io.AuthResponse;
import com.example.SpringSecurityProject.Io.ResetPasswordRequest;
import com.example.SpringSecurityProject.Service.AppUserDetailsService;
import com.example.SpringSecurityProject.Service.ProfileServiceImplementation;
import com.example.SpringSecurityProject.Utility.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private final AuthenticationManager authenticationManager;
    private final AppUserDetailsService appUserDetailsService;
    private final JwtUtil jwtUtil;
    private final ProfileServiceImplementation profileServiceImplementation;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest
                                   request){
        try {
            authenticate(request.getEmail(),request.getPassword());
            final UserDetails userDetails = appUserDetailsService.loadUserByUsername(request.getEmail());
            final String jwtToken = jwtUtil.generateToken(userDetails);
            ResponseCookie cookie = ResponseCookie.from("jwt",jwtToken)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .sameSite("Strict")
                    .build();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString())
                    .body(new AuthResponse(request.getEmail(),jwtToken));
        }catch (BadCredentialsException ex){
            Map<String,Object> error = new HashMap<>();
            error.put("error",true);
            error.put("message","Email or Password Is Incorrect");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (DisabledException ex){
            Map<String,Object> error = new HashMap<>();
            error.put("error",true);
            error.put("message","Account Is Disabled");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        catch (Exception ex){
            Map<String,Object> error = new HashMap<>();
            error.put("error",true);
            error.put("message","Authentication is failed");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
    private void authenticate(String email,String password){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));
    }
    @GetMapping("/is-authenticated")
    public ResponseEntity<Boolean> isAuthenticated(@CurrentSecurityContext(
            expression = "authentication?.name"
    )String email){
        return ResponseEntity.ok(email != null);
    }
    @PostMapping("/send-reset-otp")
    public void sendResetOtp(@RequestParam String email){
        try {
            profileServiceImplementation.sentResetOtp(email);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }
    @PostMapping("/reset-password")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest passwordRequest){
        try {
            profileServiceImplementation.resetPassword(passwordRequest.getEmail(),
                    passwordRequest.getOtp(), passwordRequest.getNewPassword());
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR
            ,e.getMessage());
        }
    }
    @PostMapping("/send-otp")
    public void sendVerifyOtp(@CurrentSecurityContext(expression = "authentication?.name")String email){
          try {
              profileServiceImplementation.sendOtp(email);
          }catch (Exception e){
              throw new ResponseStatusException(HttpStatus.
                      INTERNAL_SERVER_ERROR,e.getMessage());
          }
    }
    @PostMapping("verify-otp")
    public void verifyEmail(@RequestBody Map<String ,Object> request,
    @CurrentSecurityContext(expression = "authentication?.name") String email){
        if(request.get("otp").toString() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Missing Details");
        }
        try {
            profileServiceImplementation.verifyOtp(email,request.get("otp").toString());
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<?> Logout(HttpServletResponse response){
        ResponseCookie cookie = ResponseCookie.from("jwt","")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,cookie.toString())
                .body("Logged Out Successfully");
    }
}
