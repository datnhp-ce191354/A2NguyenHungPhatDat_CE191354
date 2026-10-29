package com.fu.news.service;

import com.fu.news.dto.AccountRequest;
import com.fu.news.dto.AccountResponse;
import com.fu.news.dto.ProfileRequest;
import com.fu.news.entity.SystemAccount;
import com.fu.news.exception.ApiException;
import com.fu.news.repository.NewsArticleRepository;
import com.fu.news.repository.SystemAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final SystemAccountRepository accountRepository;
    private final NewsArticleRepository newsRepository;

    @Transactional(readOnly = true)
    public List<AccountResponse> search(String keyword) {
        String k = keyword == null ? "" : keyword.trim();
        return accountRepository
                .findByAccountNameContainingIgnoreCaseOrAccountEmailContainingIgnoreCaseOrderByAccountIdAsc(k, k)
                .stream().map(AccountService::toResponse).toList();
    }

    @Transactional
    public AccountResponse create(AccountRequest request) {
        if (request.accountPassword() == null || request.accountPassword().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Password is required.");
        }
        String email = request.accountEmail().trim();
        if (accountRepository.existsByAccountEmailIgnoreCase(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "This email is already used by another account.");
        }
        SystemAccount account = new SystemAccount();
        account.setAccountName(request.accountName().trim());
        account.setAccountEmail(email);
        account.setAccountRole(request.accountRole());
        account.setAccountPassword(request.accountPassword());
        return toResponse(accountRepository.save(account));
    }

    @Transactional
    public AccountResponse update(Integer id, AccountRequest request, SystemAccount actor) {
        SystemAccount account = find(id);
        String email = request.accountEmail().trim();
        if (accountRepository.existsByAccountEmailIgnoreCaseAndAccountIdNot(email, id)) {
            throw new ApiException(HttpStatus.CONFLICT, "This email is already used by another account.");
        }
        if (actor.getAccountId().equals(id) && !request.accountRole().equals(actor.getAccountRole())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "You cannot change your own role.");
        }
        account.setAccountName(request.accountName().trim());
        account.setAccountEmail(email);
        account.setAccountRole(request.accountRole());
        if (request.accountPassword() != null && !request.accountPassword().isBlank()) {
            account.setAccountPassword(request.accountPassword());
        }
        return toResponse(accountRepository.save(account));
    }

    /** Rule from the assignment: an account that already created news cannot be deleted. */
    @Transactional
    public void delete(Integer id, SystemAccount actor) {
        SystemAccount account = find(id);
        if (actor.getAccountId().equals(id)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "You cannot delete the account you are signed in with.");
        }
        if (newsRepository.existsByCreatedBy_AccountId(id) || newsRepository.existsByUpdatedBy_AccountId(id)) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Cannot delete this account because it is linked to news articles.");
        }
        accountRepository.delete(account);
    }

    @Transactional
    public AccountResponse updateProfile(SystemAccount current, ProfileRequest request) {
        String email = request.accountEmail().trim();
        if (accountRepository.existsByAccountEmailIgnoreCaseAndAccountIdNot(email, current.getAccountId())) {
            throw new ApiException(HttpStatus.CONFLICT, "This email is already used by another account.");
        }
        current.setAccountName(request.accountName().trim());
        current.setAccountEmail(email);
        if (request.newPassword() != null && !request.newPassword().isBlank()) {
            current.setAccountPassword(request.newPassword());
        }
        return toResponse(accountRepository.save(current));
    }

    private SystemAccount find(Integer id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found."));
    }

    public static AccountResponse toResponse(SystemAccount a) {
        return new AccountResponse(a.getAccountId(), a.getAccountName(), a.getAccountEmail(), a.getAccountRole());
    }
}
