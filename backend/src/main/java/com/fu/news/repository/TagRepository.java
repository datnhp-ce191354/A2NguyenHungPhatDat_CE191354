package com.fu.news.repository;

import com.fu.news.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Integer> {

    boolean existsByTagNameIgnoreCase(String name);
}
