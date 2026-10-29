package com.fu.news.config;

import com.fu.news.entity.Category;
import com.fu.news.entity.NewsArticle;
import com.fu.news.entity.SystemAccount;
import com.fu.news.entity.Tag;
import com.fu.news.repository.CategoryRepository;
import com.fu.news.repository.NewsArticleRepository;
import com.fu.news.repository.SystemAccountRepository;
import com.fu.news.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

/** Inserts sample data the first time the app starts on an empty database. */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final SystemAccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final NewsArticleRepository newsRepository;

    @Override
    public void run(String... args) {
        if (accountRepository.count() > 0) {
            return;
        }

        SystemAccount admin = account("Administrator", "admin@funews.com", Role.ADMIN, "Admin@123");
        SystemAccount staff = account("Staff One", "staff@funews.com", Role.STAFF, "Staff@123");
        account("Staff Two", "staff2@funews.com", Role.STAFF, "Staff@123");

        Category campus = category("Campus", "Events and announcements on campus", true);
        Category tech = category("Technology", "Software, devices and AI", true);
        category("Archive", "Old topics no longer used", false);

        Tag ai = tag("ai", "Artificial intelligence");
        Tag events = tag("events", "Things happening soon");

        news("FPT University opens a new AI lab", "New lab for student research",
                "The lab will support student research projects starting next semester.",
                tech, 1, staff, Set.of(ai));
        news("Inter-faculty football final this Friday", "Kickoff at 5 pm",
                "Two teams remain. The match is held on the main field.",
                campus, 1, staff, Set.of(events));
        news("Draft: scholarship deadline", "Not published yet",
                "Submit documents to the student office before the deadline.",
                campus, 0, staff, Set.of());
    }

    private SystemAccount account(String name, String email, int role, String password) {
        SystemAccount a = new SystemAccount();
        a.setAccountName(name);
        a.setAccountEmail(email);
        a.setAccountRole(role);
        a.setAccountPassword(password);
        return accountRepository.save(a);
    }

    private Category category(String name, String description, boolean active) {
        Category c = new Category();
        c.setCategoryName(name);
        c.setCategoryDescription(description);
        c.setActive(active);
        return categoryRepository.save(c);
    }

    private Tag tag(String name, String note) {
        Tag t = new Tag();
        t.setTagName(name);
        t.setNote(note);
        return tagRepository.save(t);
    }

    private void news(String title, String headline, String content, Category category,
                      int status, SystemAccount creator, Set<Tag> tags) {
        NewsArticle n = new NewsArticle();
        n.setNewsTitle(title);
        n.setHeadline(headline);
        n.setNewsContent(content);
        n.setNewsSource("FUNews");
        n.setCategory(category);
        n.setNewsStatus(status);
        n.setCreatedBy(creator);
        n.setCreatedDate(LocalDateTime.now());
        n.setModifiedDate(LocalDateTime.now());
        n.setTags(new java.util.HashSet<>(tags));
        newsRepository.save(n);
    }
}
