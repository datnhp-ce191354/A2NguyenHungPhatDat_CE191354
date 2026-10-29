package com.fu.news.service;

import com.fu.news.dto.NewsRequest;
import com.fu.news.dto.NewsResponse;
import com.fu.news.entity.Category;
import com.fu.news.entity.NewsArticle;
import com.fu.news.entity.SystemAccount;
import com.fu.news.entity.Tag;
import com.fu.news.exception.ApiException;
import com.fu.news.repository.CategoryRepository;
import com.fu.news.repository.NewsArticleRepository;
import com.fu.news.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsArticleRepository newsRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    private static String normalize(String keyword) {
        return keyword == null ? "" : keyword.trim();
    }

    @Transactional(readOnly = true)
    public List<NewsResponse> findPublic(String keyword) {
        return newsRepository.searchActive(normalize(keyword)).stream().map(NewsService::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<NewsResponse> findAll(String keyword) {
        return newsRepository.search(normalize(keyword)).stream().map(NewsService::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<NewsResponse> findHistory(Integer accountId, String keyword) {
        return newsRepository.searchByCreator(accountId, normalize(keyword))
                .stream().map(NewsService::toResponse).toList();
    }

    @Transactional
    public NewsResponse create(NewsRequest request, SystemAccount actor) {
        Category category = findCategory(request.categoryId());
        if (!Boolean.TRUE.equals(category.getActive())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "The selected category is inactive.");
        }
        NewsArticle news = new NewsArticle();
        apply(news, request, category);
        news.setCreatedBy(actor);
        news.setCreatedDate(LocalDateTime.now());
        news.setModifiedDate(LocalDateTime.now());
        return toResponse(newsRepository.save(news));
    }

    @Transactional
    public NewsResponse update(Integer id, NewsRequest request, SystemAccount actor) {
        NewsArticle news = newsRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "News article not found."));
        Category category = findCategory(request.categoryId());
        boolean changedCategory = !category.getCategoryId().equals(news.getCategory().getCategoryId());
        if (changedCategory && !Boolean.TRUE.equals(category.getActive())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "The selected category is inactive.");
        }
        apply(news, request, category);
        news.setUpdatedBy(actor);
        news.setModifiedDate(LocalDateTime.now());
        return toResponse(newsRepository.save(news));
    }

    @Transactional
    public void delete(Integer id) {
        NewsArticle news = newsRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "News article not found."));
        newsRepository.delete(news); // rows in the NewsTag join table are removed with it
    }

    private Category findCategory(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Category does not exist."));
    }

    private void apply(NewsArticle news, NewsRequest request, Category category) {
        news.setNewsTitle(request.newsTitle().trim());
        news.setHeadline(request.headline().trim());
        news.setNewsContent(request.newsContent().trim());
        String source = request.newsSource();
        news.setNewsSource(source == null || source.isBlank() ? null : source.trim());
        news.setCategory(category);
        news.setNewsStatus(request.newsStatus());

        Set<Integer> ids = request.tagIds() == null ? Set.of() : new HashSet<>(request.tagIds());
        List<Tag> found = tagRepository.findAllById(ids);
        if (found.size() != ids.size()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Some selected tags do not exist.");
        }
        news.setTags(new HashSet<>(found));
    }

    public static NewsResponse toResponse(NewsArticle n) {
        return new NewsResponse(
                n.getNewsArticleId(),
                n.getNewsTitle(),
                n.getHeadline(),
                n.getNewsContent(),
                n.getNewsSource(),
                n.getCategory().getCategoryId(),
                n.getCategory().getCategoryName(),
                n.getNewsStatus(),
                n.getCreatedBy().getAccountId(),
                n.getCreatedBy().getAccountName(),
                n.getUpdatedBy() == null ? null : n.getUpdatedBy().getAccountName(),
                n.getCreatedDate(),
                n.getModifiedDate(),
                n.getTags().stream().map(TagService::toResponse).toList());
    }
}
