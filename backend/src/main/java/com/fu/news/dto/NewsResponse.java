package com.fu.news.dto;

import java.time.LocalDateTime;
import java.util.List;

public record NewsResponse(
        Integer newsArticleId,
        String newsTitle,
        String headline,
        String newsContent,
        String newsSource,
        Integer categoryId,
        String categoryName,
        Integer newsStatus,
        Integer createdById,
        String createdByName,
        String updatedByName,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate,
        List<TagResponse> tags) {
}
