package com.fu.news.service;

import com.fu.news.dto.CategoryRequest;
import com.fu.news.dto.CategoryResponse;
import com.fu.news.entity.Category;
import com.fu.news.exception.ApiException;
import com.fu.news.repository.CategoryRepository;
import com.fu.news.repository.NewsArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final NewsArticleRepository newsRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> search(String keyword) {
        String k = keyword == null ? "" : keyword.trim();
        return categoryRepository.findByCategoryNameContainingIgnoreCaseOrderByCategoryIdAsc(k)
                .stream().map(CategoryService::toResponse).toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String name = request.categoryName().trim();
        if (categoryRepository.existsByCategoryNameIgnoreCase(name)) {
            throw new ApiException(HttpStatus.CONFLICT, "This category name already exists.");
        }
        Category category = new Category();
        apply(category, request, null);
        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Integer id, CategoryRequest request) {
        Category category = find(id);
        String name = request.categoryName().trim();
        if (categoryRepository.existsByCategoryNameIgnoreCaseAndCategoryIdNot(name, id)) {
            throw new ApiException(HttpStatus.CONFLICT, "This category name already exists.");
        }
        apply(category, request, id);
        return toResponse(categoryRepository.save(category));
    }

    /** Rule from the assignment: a category already used by a news article cannot be deleted. */
    @Transactional
    public void delete(Integer id) {
        Category category = find(id);
        if (newsRepository.existsByCategory_CategoryId(id)) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Cannot delete this category because it is used by news articles.");
        }
        if (categoryRepository.existsByParentCategory_CategoryId(id)) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Cannot delete this category because other categories use it as their parent.");
        }
        categoryRepository.delete(category);
    }

    private void apply(Category category, CategoryRequest request, Integer selfId) {
        category.setCategoryName(request.categoryName().trim());
        String description = request.categoryDescription();
        category.setCategoryDescription(description == null || description.isBlank() ? null : description.trim());
        category.setActive(request.active());

        Integer parentId = request.parentCategoryId();
        if (parentId == null) {
            category.setParentCategory(null);
        } else {
            if (parentId.equals(selfId)) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "A category cannot be its own parent.");
            }
            Category parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Parent category does not exist."));
            category.setParentCategory(parent);
        }
    }

    private Category find(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category not found."));
    }

    public static CategoryResponse toResponse(Category c) {
        Category parent = c.getParentCategory();
        return new CategoryResponse(
                c.getCategoryId(),
                c.getCategoryName(),
                c.getCategoryDescription(),
                parent == null ? null : parent.getCategoryId(),
                parent == null ? null : parent.getCategoryName(),
                c.getActive());
    }
}
