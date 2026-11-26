package com.marketplace.catalog.controller;

import com.marketplace.catalog.dto.PageResponse;
import com.marketplace.catalog.dto.ProductListItem;
import com.marketplace.catalog.service.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** UC03.1 Danh sách sản phẩm: công khai, mới nhất trước, có phân trang. */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final CatalogService catalogService;

    public ProductController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductListItem>> list(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return ResponseEntity.ok(catalogService.listProducts(categoryId, page, size));
    }
}
