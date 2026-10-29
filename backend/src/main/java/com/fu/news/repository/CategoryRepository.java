package com.fu.news.repository;

import com.fu.news.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    boolean existsByCategoryNameIgnoreCase(String name);

    boolean existsByCategoryNameIgnoreCaseAndCategoryIdNot(String name, Integer categoryId);

    /** true if some category uses this one as its parent */
    boolean existsByParentCategory_CategoryId(Integer categoryId);

    List<Category> findByCategoryNameContainingIgnoreCaseOrderByCategoryIdAsc(String name);
}
