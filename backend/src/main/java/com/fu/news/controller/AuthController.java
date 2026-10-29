package com.fu.news.controller;

import com.fu.news.dto.AccountResponse;
import com.fu.news.dto.LoginRequest;
import com.fu.news.service.AccountService;
import com.fu.news.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AccountResponse login(@Valid @RequestBody LoginRequest request) {
        return AccountService.toResponse(authService.login(request.email(), request.password()));
    }
}
