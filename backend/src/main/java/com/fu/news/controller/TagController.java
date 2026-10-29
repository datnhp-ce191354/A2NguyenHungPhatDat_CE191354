package com.fu.news.controller;

import com.fu.news.config.AuthHeaders;
import com.fu.news.config.Role;
import com.fu.news.dto.TagRequest;
import com.fu.news.dto.TagResponse;
import com.fu.news.service.AuthService;
import com.fu.news.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Staff only (tags are chosen while editing a news article). */
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final AuthService authService;

    @GetMapping
    public List<TagResponse> findAll(
            @RequestHeader(value = AuthHeaders.ACCOUNT_ID, required = false) Integer accountId) {
        authService.requireRole(accountId, Role.STAFF);
        return tagService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagResponse create(
            @RequestHeader(value = AuthHeaders.ACCOUNT_ID, required = false) Integer accountId,
            @Valid @RequestBody TagRequest request) {
        authService.requireRole(accountId, Role.STAFF);
        return tagService.create(request);
    }
}
