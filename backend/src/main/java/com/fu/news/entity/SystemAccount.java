package com.fu.news.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "SystemAccount")
@Getter
@Setter
@NoArgsConstructor
public class SystemAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AccountID")
    private Integer accountId;

    @Nationalized // nvarchar, so Vietnamese names are stored correctly
    @Column(name = "AccountName", nullable = false, length = 100)
    private String accountName;

    @Column(name = "AccountEmail", nullable = false, unique = true, length = 100)
    private String accountEmail;

    /** Admin = 1, Staff = 2 */
    @Column(name = "AccountRole", nullable = false)
    private Integer accountRole;

    @Column(name = "AccountPassword", nullable = false, length = 100)
    private String accountPassword;
}
