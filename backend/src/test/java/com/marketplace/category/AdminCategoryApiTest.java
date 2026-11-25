package com.marketplace.category;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.audit.model.AuditLog;
import com.marketplace.audit.repository.AuditLogRepository;
import com.marketplace.category.repository.CategoryRepository;
import com.marketplace.category.service.CategoryProductCounter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** UC19 Quản lý danh mục (Admin) qua HTTP. Số sản phẩm theo danh mục được giả lập, không cần bảng products. */
@SpringBootTest
@AutoConfigureMockMvc
class AdminCategoryApiTest {

    static final String ADMIN_EMAIL = "uc19.admin@example.com";
    static final String USER_EMAIL = "uc19.user@example.com";
    static final String PASSWORD = "12345678";

    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired CategoryRepository categories;
    @Autowired AuditLogRepository auditLogs;
    @Autowired ObjectMapper mapper;

    @MockitoBean CategoryProductCounter productCounter;

    String adminToken;
    String userToken;

    @BeforeEach
    void setUp() throws Exception {
        auditLogs.deleteAll();
        categories.deleteAll();
        insertUser(ADMIN_EMAIL, "0911000001", "ADMIN");
        insertUser(USER_EMAIL, "0911000002", "USER");
        adminToken = login(ADMIN_EMAIL);
        userToken = login(USER_EMAIL);
    }

    void insertUser(String email, String phone, String role) {
        Integer exists = jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE email = ?", Integer.class, email);
        if (exists != null && exists > 0) {
            return;
        }
        jdbc.update(
                "INSERT INTO users (email, password_hash, full_name, phone, role, status) VALUES (?, ?, ?, ?, ?, 'ACTIVE')",
                email, passwordEncoder.encode(PASSWORD), "Người dùng thử", phone, role);
    }

    String login(String email) throws Exception {
        String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + PASSWORD + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return mapper.readTree(body).get("accessToken").asText();
    }

    MockHttpServletRequestBuilder asAdmin(MockHttpServletRequestBuilder request) {
        return request.header("Authorization", "Bearer " + adminToken);
    }

    MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder request, String body) {
        return asAdmin(request).contentType(MediaType.APPLICATION_JSON).content(body);
    }

    int createCategory(String name, String description) throws Exception {
        String body = mvc.perform(json(post("/api/admin/categories"),
                        "{\"name\":\"" + name + "\",\"description\":\"" + description + "\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return mapper.readTree(body).get("id").asInt();
    }

    List<AuditLog> auditOf(String action) {
        return auditLogs.findAll().stream().filter(a -> a.getAction().equals(action)).toList();
    }

    // ===== Chỉ Admin được dùng =====

    @Test
    void onlyAnAdminMayUseTheCategoryApi() throws Exception {
        mvc.perform(get("/api/admin/categories")).andExpect(status().isForbidden());
        mvc.perform(get("/api/admin/categories").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
        mvc.perform(post("/api/admin/categories").header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Trộm\"}"))
                .andExpect(status().isForbidden());
        assertThat(categories.count()).isZero();
        mvc.perform(asAdmin(get("/api/admin/categories"))).andExpect(status().isOk());
    }

    // ===== UC19.1 Danh sách =====

    @Test
    void listShowsStatusAndProductCountOfEveryCategory() throws Exception {
        int books = createCategory("Sách - Giáo trình", "Sách học tập");
        int tech = createCategory("Đồ điện tử", "Laptop, điện thoại");
        when(productCounter.countByCategoryId(eq(books))).thenReturn(24L);
        when(productCounter.countByCategoryId(eq(tech))).thenReturn(0L);

        mvc.perform(asAdmin(get("/api/admin/categories")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(books))
                .andExpect(jsonPath("$[0].name").value("Sách - Giáo trình"))
                .andExpect(jsonPath("$[0].description").value("Sách học tập"))
                .andExpect(jsonPath("$[0].isActive").value(true))
                .andExpect(jsonPath("$[0].productCount").value(24))
                .andExpect(jsonPath("$[1].productCount").value(0));
    }

    // ===== UC19.2 Thêm =====

    @Test
    void createReturns201WithTheIdTrimsTheNameAndWritesTheAuditLog() throws Exception {
        String body = mvc.perform(json(post("/api/admin/categories"), "{\"name\":\"  Xe cộ  \",\"description\":\"\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn().getResponse().getContentAsString();
        int id = mapper.readTree(body).get("id").asInt();

        var saved = categories.findById(id).orElseThrow();
        assertThat(saved.getName()).isEqualTo("Xe cộ");
        assertThat(saved.getDescription()).isNull();
        assertThat(saved.isActive()).isTrue();

        List<AuditLog> logs = auditOf("CREATE_CATEGORY");
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getTargetType()).isEqualTo(AuditLog.TargetType.CATEGORY);
        assertThat(logs.get(0).getTargetId()).isEqualTo((long) id);
        Long adminId = jdbc.queryForObject("SELECT id FROM users WHERE email = ?", Long.class, ADMIN_EMAIL);
        assertThat(logs.get(0).getAdminId()).isEqualTo(adminId);
    }

    @Test
    void createRejectsABlankOrMissingNameWithNameRequired() throws Exception {
        for (String body : List.of("{\"name\":\"\"}", "{\"name\":\"   \"}", "{\"description\":\"chỉ có mô tả\"}")) {
            mvc.perform(json(post("/api/admin/categories"), body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("NAME_REQUIRED"))
                    .andExpect(jsonPath("$.fields.name").exists());
        }
        assertThat(categories.count()).isZero();
        assertThat(auditLogs.count()).isZero();
    }

    @Test
    void createRejectsATooLongNameAndTooLongDescription() throws Exception {
        mvc.perform(json(post("/api/admin/categories"), "{\"name\":\"" + "a".repeat(101) + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.fields.name").exists());
        mvc.perform(json(post("/api/admin/categories"),
                        "{\"name\":\"Hợp lệ\",\"description\":\"" + "d".repeat(501) + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.description").exists());
        assertThat(categories.count()).isZero();
    }

    @Test
    void createRejectsADuplicateNameIgnoringCaseAndSpaces() throws Exception {
        createCategory("Đồ gia dụng", "Đồ dùng phòng trọ");

        mvc.perform(json(post("/api/admin/categories"), "{\"name\":\"  đồ GIA dụng \"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CATEGORY_NAME_EXISTS"));
        assertThat(categories.count()).isEqualTo(1);
        assertThat(auditOf("CREATE_CATEGORY")).hasSize(1);
    }

    @Test
    void brokenJsonIsAnInvalidRequestNotAServerError() throws Exception {
        mvc.perform(json(post("/api/admin/categories"), "{khong phai json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    // ===== UC19.3 Sửa =====

    @Test
    void updateChangesNameAndDescriptionAndWritesTheAuditLog() throws Exception {
        int id = createCategory("Khác", "Mô tả cũ");

        mvc.perform(json(patch("/api/admin/categories/" + id), "{\"name\":\"Đồ khác\",\"description\":\"Mô tả mới\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        var saved = categories.findById(id).orElseThrow();
        assertThat(saved.getName()).isEqualTo("Đồ khác");
        assertThat(saved.getDescription()).isEqualTo("Mô tả mới");
        assertThat(auditOf("UPDATE_CATEGORY")).hasSize(1);
    }

    @Test
    void updateMayKeepItsOwnNameButNotTakeAnotherCategorysName() throws Exception {
        int first = createCategory("Sách", "a");
        int second = createCategory("Xe cộ", "b");

        mvc.perform(json(patch("/api/admin/categories/" + first), "{\"name\":\"sách\",\"description\":\"đổi mô tả\"}"))
                .andExpect(status().isOk());
        mvc.perform(json(patch("/api/admin/categories/" + second), "{\"name\":\"SÁCH\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CATEGORY_NAME_EXISTS"));
        assertThat(categories.findById(second).orElseThrow().getName()).isEqualTo("Xe cộ");
    }

    @Test
    void updateRejectsABlankNameAndAnUnknownCategory() throws Exception {
        int id = createCategory("Sách", "a");

        mvc.perform(json(patch("/api/admin/categories/" + id), "{\"name\":\" \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("NAME_REQUIRED"));
        mvc.perform(json(patch("/api/admin/categories/999999"), "{\"name\":\"Không có\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CATEGORY_NOT_FOUND"));
        assertThat(categories.findById(id).orElseThrow().getName()).isEqualTo("Sách");
    }

    // ===== UC19.4 Vô hiệu hóa, kích hoạt =====

    @Test
    void aCategoryCanBeDeactivatedAndActivatedAgainEvenWithProducts() throws Exception {
        int id = createCategory("Đồ điện tử", "a");
        when(productCounter.countByCategoryId(eq(id))).thenReturn(12L);

        mvc.perform(json(patch("/api/admin/categories/" + id + "/active"), "{\"isActive\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));
        assertThat(categories.findById(id).orElseThrow().isActive()).isFalse();

        mvc.perform(json(patch("/api/admin/categories/" + id + "/active"), "{\"isActive\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(true));
        assertThat(categories.findById(id).orElseThrow().isActive()).isTrue();
        assertThat(auditOf("TOGGLE_CATEGORY")).hasSize(2);
    }

    @Test
    void toggleNeedsTheIsActiveFieldAndAKnownCategory() throws Exception {
        int id = createCategory("Khác", "a");

        mvc.perform(json(patch("/api/admin/categories/" + id + "/active"), "{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.fields.isActive").exists());
        mvc.perform(json(patch("/api/admin/categories/999999/active"), "{\"isActive\":false}"))
                .andExpect(status().isNotFound());
        assertThat(auditOf("TOGGLE_CATEGORY")).isEmpty();
    }

    // ===== UC19.5 Xóa (BR-10) =====

    @Test
    void aCategoryWithoutProductsCanBeDeleted() throws Exception {
        int id = createCategory("Xe cộ", "a");
        when(productCounter.countByCategoryId(any())).thenReturn(0L);

        mvc.perform(asAdmin(delete("/api/admin/categories/" + id))).andExpect(status().isNoContent());

        assertThat(categories.existsById(id)).isFalse();
        assertThat(auditOf("DELETE_CATEGORY")).hasSize(1);
    }

    @Test
    void aCategoryWithProductsCannotBeDeletedOnlyDeactivated() throws Exception {
        int id = createCategory("Đồ điện tử", "a");
        when(productCounter.countByCategoryId(eq(id))).thenReturn(3L);

        mvc.perform(asAdmin(delete("/api/admin/categories/" + id)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CATEGORY_HAS_PRODUCTS"));

        assertThat(categories.existsById(id)).isTrue();
        assertThat(auditOf("DELETE_CATEGORY")).isEmpty();
    }

    @Test
    void deletingAnUnknownCategoryIsNotFound() throws Exception {
        mvc.perform(asAdmin(delete("/api/admin/categories/999999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CATEGORY_NOT_FOUND"));
    }
}
