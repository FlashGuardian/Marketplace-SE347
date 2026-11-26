package com.marketplace.catalog.service;

import com.marketplace.catalog.dto.CategoryOption;
import com.marketplace.catalog.dto.PageResponse;
import com.marketplace.catalog.dto.ProductListItem;
import com.marketplace.catalog.repository.CatalogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** UC03 Xem danh sách sản phẩm (công khai). */
@Service
public class CatalogService {

    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 50;

    private final CatalogRepository catalog;

    public CatalogService(CatalogRepository catalog) {
        this.catalog = catalog;
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductListItem> listProducts(Integer categoryId, Integer page, Integer size) {
        int pageNumber = page == null ? 1 : page;
        int pageSize = size == null ? DEFAULT_SIZE : size;
        if (pageNumber < 1 || pageSize < 1 || pageSize > MAX_SIZE) {
            throw new InvalidPaginationException();
        }
        long total = catalog.countVisible(categoryId);
        long offset = (long) (pageNumber - 1) * pageSize;
        List<ProductListItem> items = offset >= total
                ? List.of()
                : catalog.findVisible(categoryId, pageSize, (int) offset);
        return PageResponse.of(items, pageNumber, pageSize, total);
    }

    @Transactional(readOnly = true)
    public List<CategoryOption> activeCategories() {
        return catalog.findActiveCategories();
    }
}
