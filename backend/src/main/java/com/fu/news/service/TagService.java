package com.fu.news.service;

import com.fu.news.dto.TagRequest;
import com.fu.news.dto.TagResponse;
import com.fu.news.entity.Tag;
import com.fu.news.exception.ApiException;
import com.fu.news.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<TagResponse> findAll() {
        return tagRepository.findAll().stream().map(TagService::toResponse).toList();
    }

    @Transactional
    public TagResponse create(TagRequest request) {
        String name = request.tagName().trim();
        if (tagRepository.existsByTagNameIgnoreCase(name)) {
            throw new ApiException(HttpStatus.CONFLICT, "This tag already exists.");
        }
        Tag tag = new Tag();
        tag.setTagName(name);
        tag.setNote(request.note() == null || request.note().isBlank() ? null : request.note().trim());
        return toResponse(tagRepository.save(tag));
    }

    public static TagResponse toResponse(Tag t) {
        return new TagResponse(t.getTagId(), t.getTagName(), t.getNote());
    }
}
