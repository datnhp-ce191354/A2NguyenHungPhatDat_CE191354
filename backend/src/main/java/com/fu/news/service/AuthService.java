package com.fu.news.service;

import com.fu.news.entity.SystemAccount;
import com.fu.news.exception.ApiException;
import com.fu.news.repository.SystemAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Teaching simplification: there is no JWT. The client sends the account id in the
 * X-Account-Id header and every protected endpoint checks that account's role here.
 * A real system would verify a signed token instead (anyone can fake this header).
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final SystemAccountRepository accountRepository;

    public SystemAccount login(String email, String password) {
        return accountRepository.findByAccountEmailIgnoreCase(email.trim())
                .filter(a -> a.getAccountPassword().equals(password))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Wrong email or password."));
    }

    public SystemAccount requireLogin(Integer accountId) {
        if (accountId == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Please sign in first.");
        }
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED,
                        "This account no longer exists. Please sign in again."));
    }

    public SystemAccount requireRole(Integer accountId, int role) {
        SystemAccount account = requireLogin(accountId);
        if (account.getAccountRole().intValue() != role) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You do not have permission for this action.");
        }
        return account;
    }
}
