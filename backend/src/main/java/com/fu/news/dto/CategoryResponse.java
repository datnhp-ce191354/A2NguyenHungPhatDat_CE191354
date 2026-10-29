package com.fu.news.dto;

public record CategoryResponse(
        Integer categoryId,
        String categoryName,
        String categoryDescription,
        Integer parentCategoryId,
        String parentCategoryName,
        Boolean active) {
}
