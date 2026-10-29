package com.fu.news.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileRequest(
        @NotBlank(message = "Account name is required.")
        @Size(max = 100, message = "Account name must be 100 characters or fewer.")
        String accountName,

        @NotBlank(message = "Email is required.")
        @Email(message = "Email is not valid.")
        @Size(max = 100, message = "Email must be 100 characters or fewer.")
        String accountEmail,

        @Size(min = 6, max = 100, message = "Password must be 6-100 characters.")
        String newPassword) {
}
