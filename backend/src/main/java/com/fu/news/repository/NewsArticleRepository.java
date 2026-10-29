package com.fu.news.repository;

import com.fu.news.entity.NewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NewsArticleRepository extends JpaRepository<NewsArticle, Integer> {

    // Used by the delete rules: "cannot delete if it is already used by a news article"
    boolean existsByCategory_CategoryId(Integer categoryId);

    boolean existsByCreatedBy_AccountId(Integer accountId);

    boolean existsByUpdatedBy_AccountId(Integer accountId);

    /** Staff: all news, filtered by keyword in title or headline. */
    @Query("select n from NewsArticle n "
            + "where lower(n.newsTitle) like lower(concat('%', :k, '%')) "
            + "or lower(n.headline) like lower(concat('%', :k, '%')) "
            + "order by n.createdDate desc")
    List<NewsArticle> search(@Param("k") String keyword);

    /** Public (no login): only ACTIVE news (status = 1). */
    @Query("select n from NewsArticle n "
            + "where n.newsStatus = 1 and (lower(n.newsTitle) like lower(concat('%', :k, '%')) "
            + "or lower(n.headline) like lower(concat('%', :k, '%'))) "
            + "order by n.createdDate desc")
    List<NewsArticle> searchActive(@Param("k") String keyword);

    /** Staff history: only the news created by this account. */
    @Query("select n from NewsArticle n "
            + "where n.createdBy.accountId = :accountId and (lower(n.newsTitle) like lower(concat('%', :k, '%')) "
            + "or lower(n.headline) like lower(concat('%', :k, '%'))) "
            + "order by n.createdDate desc")
    List<NewsArticle> searchByCreator(@Param("accountId") Integer accountId, @Param("k") String keyword);
}
