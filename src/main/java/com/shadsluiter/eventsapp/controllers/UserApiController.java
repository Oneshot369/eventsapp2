package com.shadsluiter.eventsapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import com.shadsluiter.eventsapp.models.UserModel;
import com.shadsluiter.eventsapp.security.JwtTokenProvider;
import com.shadsluiter.eventsapp.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/users")
public class UserApiController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserModel user) {
        UserModel existingUser = userService.findByLoginName(user.getUserName());
        if (existingUser != null) {
            return new ResponseEntity<String>("User already exists!", HttpStatus.BAD_REQUEST);
        }
        //encrypt the pw
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // save the user to the database
        userService.save(user);
        return new ResponseEntity<String>("User added", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserModel user) {
       Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                user.getUserName(), user.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = jwtTokenProvider.generateToken(auth);

        Map<String, String> res = new HashMap<>();
        res.put("token", jwt);

        
        return ResponseEntity.ok(res);
    }
    
}
