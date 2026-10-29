package com.fu.news.dto;

import jakarta.validation.constraints.*;

/** Password is required on create; on update leave it null to keep the old one. */
public record AccountRequest(
        @NotBlank(message = "Account name is required.")
        @Size(max = 100, message = "Account name must be 100 characters or fewer.")
        String accountName,

        @NotBlank(message = "Email is required.")
        @Email(message = "Email is not valid.")
        @Size(max = 100, message = "Email must be 100 characters or fewer.")
        String accountEmail,

        @NotNull(message = "Role is required.")
        @Min(value = 1, message = "Role must be 1 (Admin) or 2 (Staff).")
        @Max(value = 2, message = "Role must be 1 (Admin) or 2 (Staff).")
        Integer accountRole,

        @Size(min = 6, max = 100, message = "Password must be 6-100 characters.")
        String accountPassword) {
}
