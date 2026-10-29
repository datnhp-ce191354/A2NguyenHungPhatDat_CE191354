package com.fu.news.dto;

/** Never contains the password. */
public record AccountResponse(Integer accountId, String accountName, String accountEmail, Integer accountRole) {
}
