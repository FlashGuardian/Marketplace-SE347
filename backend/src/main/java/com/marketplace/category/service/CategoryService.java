package com.marketplace.category.service;

import com.marketplace.audit.model.AuditLog;
import com.marketplace.audit.service.AuditLogService;
import com.marketplace.auth.model.User;
import com.marketplace.auth.repository.UserRepository;
import com.marketplace.category.dto.CategoryResponse;
import com.marketplace.category.model.Category;
import com.marketplace.category.repository.CategoryRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** UC19 Quản lý danh mục (Admin). Mỗi thao tác ghi bảng và nhật ký trong cùng một giao dịch. */
@Service
public class CategoryService {

    static final String CREATE = "CREATE_CATEGORY";
    static final String UPDATE = "UPDATE_CATEGORY";
    static final String TOGGLE = "TOGGLE_CATEGORY";
    static final String DELETE = "DELETE_CATEGORY";

    private final CategoryRepository categories;
    private final CategoryProductCounter productCounter;
    private final UserRepository users;
    private final AuditLogService auditLog;

    public CategoryService(
            CategoryRepository categories,
            CategoryProductCounter productCounter,
            UserRepository users,
            AuditLogService auditLog
    ) {
        this.categories = categories;
        this.productCounter = productCounter;
        this.users = users;
        this.auditLog = auditLog;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> list() {
        return categories.findAllByOrderByIdAsc().stream()
                .map(category -> CategoryResponse.of(category, productCounter.countByCategoryId(category.getId())))
                .toList();
    }

    @Transactional
    public Integer create(String adminEmail, String name, String description) {
        String cleanName = name.trim();
        if (categories.existsByNameIgnoreCase(cleanName)) {
            throw new CategoryNameExistsException();
        }
        Category saved = saveOrNameExists(new Category(cleanName, clean(description)));
        auditLog.record(adminId(adminEmail), CREATE, AuditLog.TargetType.CATEGORY, saved.getId().longValue(), null);
        return saved.getId();
    }

    @Transactional
    public Integer update(String adminEmail, Integer id, String name, String description) {
        Category category = find(id);
        String cleanName = name.trim();
        if (categories.existsByNameIgnoreCaseAndIdNot(cleanName, id)) {
            throw new CategoryNameExistsException();
        }
        category.rename(cleanName, clean(description));
        saveOrNameExists(category);
        auditLog.record(adminId(adminEmail), UPDATE, AuditLog.TargetType.CATEGORY, id.longValue(), null);
        return id;
    }

    @Transactional
    public boolean setActive(String adminEmail, Integer id, boolean active) {
        Category category = find(id);
        category.setActive(active);
        categories.save(category);
        auditLog.record(adminId(adminEmail), TOGGLE, AuditLog.TargetType.CATEGORY, id.longValue(), null);
        return category.isActive();
    }

    @Transactional
    public void delete(String adminEmail, Integer id) {
        Category category = find(id);
        if (productCounter.countByCategoryId(id) > 0) {
            throw new CategoryHasProductsException();
        }
        categories.delete(category);
        auditLog.record(adminId(adminEmail), DELETE, AuditLog.TargetType.CATEGORY, id.longValue(), category.getName());
    }

    /**
     * Ràng buộc UNIQUE của cơ sở dữ liệu có thể coi hai tên là trùng (ví dụ khác dấu) trong khi kiểm tra
     * bằng code thì không: lỗi đó cũng phải thành 409, không phải 500.
     */
    private Category saveOrNameExists(Category category) {
        try {
            return categories.saveAndFlush(category);
        } catch (DataIntegrityViolationException e) {
            throw new CategoryNameExistsException();
        }
    }

    private Category find(Integer id) {
        return categories.findById(id).orElseThrow(CategoryNotFoundException::new);
    }

    private Long adminId(String adminEmail) {
        return users.findByEmail(adminEmail).map(User::getId).orElseThrow();
    }

    /** Mô tả rỗng được lưu là NULL. */
    private static String clean(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        return description.trim();
    }
}
