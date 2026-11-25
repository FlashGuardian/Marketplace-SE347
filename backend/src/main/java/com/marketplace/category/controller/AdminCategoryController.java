package com.marketplace.category.controller;

import com.marketplace.category.dto.CategoryActiveRequest;
import com.marketplace.category.dto.CategoryRequest;
import com.marketplace.category.dto.CategoryResponse;
import com.marketplace.category.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/** UC19. Chỉ Admin truy cập được (SecurityConfig: /api/admin/** cần ROLE_ADMIN). */
@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> list() {
        return ResponseEntity.ok(categoryService.list());
    }

    @PostMapping
    public ResponseEntity<Map<String, Integer>> create(
            Authentication authentication,
            @Valid @RequestBody CategoryRequest request
    ) {
        Integer id = categoryService.create(authentication.getName(), request.name(), request.description());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Integer>> update(
            Authentication authentication,
            @PathVariable Integer id,
            @Valid @RequestBody CategoryRequest request
    ) {
        Integer updated = categoryService.update(authentication.getName(), id, request.name(), request.description());
        return ResponseEntity.ok(Map.of("id", updated));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<Map<String, Boolean>> setActive(
            Authentication authentication,
            @PathVariable Integer id,
            @Valid @RequestBody CategoryActiveRequest request
    ) {
        boolean active = categoryService.setActive(authentication.getName(), id, request.isActive());
        return ResponseEntity.ok(Map.of("isActive", active));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Integer id) {
        categoryService.delete(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
