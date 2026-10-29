package com.fu.news.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "Tag")
@Getter
@Setter
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TagID")
    private Integer tagId;

    @Nationalized
    @Column(name = "TagName", nullable = false, length = 50)
    private String tagName;

    @Nationalized
    @Column(name = "Note", length = 400)
    private String note;
}
