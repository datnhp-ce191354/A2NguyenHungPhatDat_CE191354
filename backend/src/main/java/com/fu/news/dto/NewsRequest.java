package com.fu.news.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public record NewsRequest(
        @NotBlank(message = "News title is required.")
        @Size(max = 400, message = "News title must be 400 characters or fewer.")
        String newsTitle,

        @NotBlank(message = "Headline is required.")
        @Size(max = 150, message = "Headline must be 150 characters or fewer.")
        String headline,

        @NotBlank(message = "News content is required.")
        @Size(max = 4000, message = "News content must be 4000 characters or fewer.")
        String newsContent,

        @Size(max = 400, message = "News source must be 400 characters or fewer.")
        String newsSource,

        @NotNull(message = "Category is required.")
        Integer categoryId,

        @NotNull(message = "Status is required.")
        @Min(value = 0, message = "Status must be 0 (inactive) or 1 (active).")
        @Max(value = 1, message = "Status must be 0 (inactive) or 1 (active).")
        Integer newsStatus,

        List<Integer> tagIds) {
}
