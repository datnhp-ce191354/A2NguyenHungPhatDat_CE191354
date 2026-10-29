package com.fu.news.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Category name is required.")
        @Size(max = 100, message = "Category name must be 100 characters or fewer.")
        String categoryName,

        @Size(max = 250, message = "Description must be 250 characters or fewer.")
        String categoryDescription,

        Integer parentCategoryId,

        @NotNull(message = "Status is required.")
        Boolean active) {
}
