package com.marketplace.catalog.controller;

import com.marketplace.catalog.dto.CategoryOption;
import com.marketplace.catalog.service.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** UC03.2 Danh mục đang hoạt động: công khai, dùng cho menu, bộ lọc và form đăng bán. */
@RestController
@RequestMapping("/api/categories")
public class PublicCategoryController {

    private final CatalogService catalogService;

    public PublicCategoryController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryOption>> list() {
        return ResponseEntity.ok(catalogService.activeCategories());
    }
}
