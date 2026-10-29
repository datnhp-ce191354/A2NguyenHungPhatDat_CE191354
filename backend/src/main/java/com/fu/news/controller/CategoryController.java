package com.fu.news.controller;

import com.fu.news.config.AuthHeaders;
import com.fu.news.config.Role;
import com.fu.news.dto.CategoryRequest;
import com.fu.news.dto.CategoryResponse;
import com.fu.news.service.AuthService;
import com.fu.news.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Staff only. */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final AuthService authService;

    @GetMapping
    public List<CategoryResponse> search(
            @RequestHeader(value = AuthHeaders.ACCOUNT_ID, required = false) Integer accountId,
            @RequestParam(required = false) String keyword) {
        authService.requireRole(accountId, Role.STAFF);
        return categoryService.search(keyword);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(
            @RequestHeader(value = AuthHeaders.ACCOUNT_ID, required = false) Integer accountId,
            @Valid @RequestBody CategoryRequest request) {
        authService.requireRole(accountId, Role.STAFF);
        return categoryService.create(request);
    }

    @PutMapping("/{id}")
    public CategoryResponse update(
            @RequestHeader(value = AuthHeaders.ACCOUNT_ID, required = false) Integer accountId,
            @PathVariable Integer id,
            @Valid @RequestBody CategoryRequest request) {
        authService.requireRole(accountId, Role.STAFF);
        return categoryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestHeader(value = AuthHeaders.ACCOUNT_ID, required = false) Integer accountId,
            @PathVariable Integer id) {
        authService.requireRole(accountId, Role.STAFF);
        categoryService.delete(id);
    }
}
