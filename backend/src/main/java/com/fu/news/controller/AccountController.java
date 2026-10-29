package com.fu.news.controller;

import com.fu.news.config.AuthHeaders;
import com.fu.news.config.Role;
import com.fu.news.dto.AccountRequest;
import com.fu.news.dto.AccountResponse;
import com.fu.news.entity.SystemAccount;
import com.fu.news.service.AccountService;
import com.fu.news.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Admin only. */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AuthService authService;

    @GetMapping
    public List<AccountResponse> search(
            @RequestHeader(value = AuthHeaders.ACCOUNT_ID, required = false) Integer accountId,
            @RequestParam(required = false) String keyword) {
        authService.requireRole(accountId, Role.ADMIN);
        return accountService.search(keyword);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(
            @RequestHeader(value = AuthHeaders.ACCOUNT_ID, required = false) Integer accountId,
            @Valid @RequestBody AccountRequest request) {
        authService.requireRole(accountId, Role.ADMIN);
        return accountService.create(request);
    }

    @PutMapping("/{id}")
    public AccountResponse update(
            @RequestHeader(value = AuthHeaders.ACCOUNT_ID, required = false) Integer accountId,
            @PathVariable Integer id,
            @Valid @RequestBody AccountRequest request) {
        SystemAccount actor = authService.requireRole(accountId, Role.ADMIN);
        return accountService.update(id, request, actor);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestHeader(value = AuthHeaders.ACCOUNT_ID, required = false) Integer accountId,
            @PathVariable Integer id) {
        SystemAccount actor = authService.requireRole(accountId, Role.ADMIN);
        accountService.delete(id, actor);
    }
}
