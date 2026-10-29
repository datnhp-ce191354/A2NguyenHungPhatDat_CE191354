package com.fu.news.controller;

import com.fu.news.config.AuthHeaders;
import com.fu.news.config.Role;
import com.fu.news.dto.AccountResponse;
import com.fu.news.dto.ProfileRequest;
import com.fu.news.entity.SystemAccount;
import com.fu.news.service.AccountService;
import com.fu.news.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** Staff manage their own profile. */
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final AccountService accountService;
    private final AuthService authService;

    @GetMapping
    public AccountResponse get(
            @RequestHeader(value = AuthHeaders.ACCOUNT_ID, required = false) Integer accountId) {
        return AccountService.toResponse(authService.requireRole(accountId, Role.STAFF));
    }

    @PutMapping
    public AccountResponse update(
            @RequestHeader(value = AuthHeaders.ACCOUNT_ID, required = false) Integer accountId,
            @Valid @RequestBody ProfileRequest request) {
        SystemAccount current = authService.requireRole(accountId, Role.STAFF);
        return accountService.updateProfile(current, request);
    }
}
