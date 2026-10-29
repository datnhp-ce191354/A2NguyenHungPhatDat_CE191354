package com.fu.news;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NewsApiIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void publicNewsExcludesDraftsAndSupportsSearch() throws Exception {
        JsonNode articles = json(mvc.perform(get("/api/news/public"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertTrue(articles.isArray());
        assertFalse(articles.isEmpty());
        articles.forEach(article -> assertEquals(1, article.get("newsStatus").asInt()));

        JsonNode filtered = json(mvc.perform(get("/api/news/public").param("keyword", "AI lab"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertEquals(1, filtered.size());
        assertTrue(filtered.get(0).get("newsTitle").asText().contains("AI lab"));
    }

    @Test
    void loginAndRoleChecksWork() throws Exception {
        int adminId = login("admin@funews.com", "Admin@123");
        int staffId = login("staff@funews.com", "Staff@123");

        mvc.perform(get("/api/accounts")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/accounts").header("X-Account-Id", staffId))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/accounts").header("X-Account-Id", adminId))
                .andExpect(status().isOk());
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("email", "staff@funews.com", "password", "wrong"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void staffCanCreateAndDeleteNewsWithCategoryRule() throws Exception {
        int staffId = login("staff@funews.com", "Staff@123");
        JsonNode category = json(mvc.perform(post("/api/categories")
                .header("X-Account-Id", staffId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                        "categoryName", "Integration category",
                        "categoryDescription", "Created by API test",
                        "active", true))))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
        int categoryId = category.get("categoryId").asInt();

        JsonNode news = json(mvc.perform(post("/api/news")
                .header("X-Account-Id", staffId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                        "newsTitle", "Integration article",
                        "headline", "API test headline",
                        "newsContent", "API test content",
                        "categoryId", categoryId,
                        "newsStatus", 1,
                        "tagIds", List.of()))))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
        int newsId = news.get("newsArticleId").asInt();
        assertEquals(staffId, news.get("createdById").asInt());

        JsonNode found = json(mvc.perform(get("/api/news/public").param("keyword", "Integration article"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertEquals(1, found.size());

        mvc.perform(delete("/api/categories/{id}", categoryId).header("X-Account-Id", staffId))
                .andExpect(status().isConflict());
        mvc.perform(delete("/api/news/{id}", newsId).header("X-Account-Id", staffId))
                .andExpect(status().isNoContent());
        mvc.perform(delete("/api/categories/{id}", categoryId).header("X-Account-Id", staffId))
                .andExpect(status().isNoContent());
    }

    private int login(String email, String password) throws Exception {
        JsonNode account = json(mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("email", email, "password", password))))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        return account.get("accountId").asInt();
    }

    private JsonNode json(String body) throws Exception {
        return mapper.readTree(body);
    }
}
