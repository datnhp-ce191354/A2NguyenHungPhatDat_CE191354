package com.fu.news.controller;

import com.fu.news.config.AuthHeaders;
import com.fu.news.config.Role;
import com.fu.news.dto.NewsRequest;
import com.fu.news.dto.NewsResponse;
import com.fu.news.entity.SystemAccount;
import com.fu.news.service.AuthService;
import com.fu.news.service.NewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;
    private final AuthService authService;

    /** No login needed. Only ACTIVE news is returned. */
    @GetMapping("/public")
    public List<NewsResponse> publicNews(@RequestParam(required = false) String keyword) {
        return newsService.findPublic(keyword);
    }

    @GetMapping
    public List<NewsResponse> search(
            @RequestHeader(value = AuthHeaders.ACCOUNT_ID, required = false) Integer accountId,
            @RequestParam(required = false) String keyword) {
        authService.requireRole(accountId, Role.STAFF);
        return newsService.findAll(keyword);
    }

    /** Staff: only the news created by the signed-in staff. */
    @GetMapping("/my-history")
    public List<NewsResponse> myHistory(
            @RequestHeader(value = AuthHeaders.ACCOUNT_ID, required = false) Integer accountId,
            @RequestParam(required = false) String keyword) {
        SystemAccount actor = authService.requireRole(accountId, Role.STAFF);
        return newsService.findHistory(actor.getAccountId(), keyword);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NewsResponse create(
            @RequestHeader(value = AuthHeaders.ACCOUNT_ID, required = false) Integer accountId,
            @Valid @RequestBody NewsRequest request) {
        SystemAccount actor = authService.requireRole(accountId, Role.STAFF);
        return newsService.create(request, actor);
    }

    @PutMapping("/{id}")
    public NewsResponse update(
            @RequestHeader(value = AuthHeaders.ACCOUNT_ID, required = false) Integer accountId,
            @PathVariable Integer id,
            @Valid @RequestBody NewsRequest request) {
        SystemAccount actor = authService.requireRole(accountId, Role.STAFF);
        return newsService.update(id, request, actor);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestHeader(value = AuthHeaders.ACCOUNT_ID, required = false) Integer accountId,
            @PathVariable Integer id) {
        authService.requireRole(accountId, Role.STAFF);
        newsService.delete(id);
    }
}
