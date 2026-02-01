package com.liquordb.controller;

import com.liquordb.dto.user.UserLoginRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/restore")
    public ResponseEntity<?> restore(@RequestBody UserLoginRequestDto request) {

    }
}
