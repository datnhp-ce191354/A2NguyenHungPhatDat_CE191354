package com.fu.news.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagRequest(
        @NotBlank(message = "Tag name is required.")
        @Size(max = 50, message = "Tag name must be 50 characters or fewer.")
        String tagName,

        @Size(max = 400, message = "Note must be 400 characters or fewer.")
        String note) {
}
