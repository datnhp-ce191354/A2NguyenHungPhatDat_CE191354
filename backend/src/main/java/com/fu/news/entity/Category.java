package com.fu.news.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "Category")
@Getter
@Setter
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CategoryID")
    private Integer categoryId;

    @Nationalized
    @Column(name = "CategoryName", nullable = false, length = 100)
    private String categoryName;

    @Nationalized
    @Column(name = "CategoryDescription", length = 250)
    private String categoryDescription;

    /** Self reference: a category may have a parent category (optional). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ParentCategoryID")
    private Category parentCategory;

    /** active = true (1), inactive = false (0) */
    @Column(name = "IsActive", nullable = false)
    private Boolean active = Boolean.TRUE;
}
