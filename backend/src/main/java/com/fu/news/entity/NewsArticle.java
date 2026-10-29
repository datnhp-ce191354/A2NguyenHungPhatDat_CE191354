package com.fu.news.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "NewsArticle")
@Getter
@Setter
@NoArgsConstructor
public class NewsArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NewsArticleID")
    private Integer newsArticleId;

    @Nationalized
    @Column(name = "NewsTitle", nullable = false, length = 400)
    private String newsTitle;

    @Nationalized
    @Column(name = "Headline", nullable = false, length = 150)
    private String headline;

    @Column(name = "CreatedDate", nullable = false)
    private LocalDateTime createdDate;

    @Nationalized
    @Column(name = "NewsContent", nullable = false, length = 4000)
    private String newsContent;

    @Nationalized
    @Column(name = "NewsSource", length = 400)
    private String newsSource;

    /** A news article belongs to exactly one category. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CategoryID", nullable = false)
    private Category category;

    /** active = 1, inactive = 0 */
    @Column(name = "NewsStatus", nullable = false)
    private Integer newsStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CreatedByID", nullable = false)
    private SystemAccount createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UpdatedByID")
    private SystemAccount updatedBy;

    @Column(name = "ModifiedDate")
    private LocalDateTime modifiedDate;

    /** Many-to-many through the NewsTag join table (see the ERD). */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "NewsTag",
            joinColumns = @JoinColumn(name = "NewsArticleID"),
            inverseJoinColumns = @JoinColumn(name = "TagID"))
    private Set<Tag> tags = new HashSet<>();
}
