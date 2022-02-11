package com.aueb.webapp.controllers;

import javax.validation.Valid;

import com.aueb.webapp.WebappApplication;
import com.aueb.webapp.models.Logging;
import com.aueb.webapp.models.LoggingCompositeKey;
import com.aueb.webapp.payloads.LoginRequest;
import com.aueb.webapp.repositories.LogRepository;
import com.aueb.webapp.repositories.UsersRepository;
import com.aueb.webapp.responses.JwtResponse;
import com.aueb.webapp.responses.TokenRefreshResponse;
import com.aueb.webapp.security.jwt.JwtUtils;
import com.aueb.webapp.security.services.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@AllArgsConstructor
@RequestMapping("/api/")
public class LoginController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UsersRepository userRepository;

    @Autowired
    LogRepository logRepo;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            System.out.println("LOGGING OK");
            return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername()));
        }catch (BadCredentialsException e){
            System.out.println("UNAUTHORIZED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @RequestMapping("error")
    @ResponseBody
    String sample() {
        return WebappApplication.loginMessage;
    }
}