package com.marketplace.catalog.service;

import com.marketplace.catalog.dto.CategoryOption;
import com.marketplace.catalog.dto.PageResponse;
import com.marketplace.catalog.dto.ProductFilter;
import com.marketplace.catalog.dto.ProductListItem;
import com.marketplace.catalog.repository.CatalogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/** UC03 Xem danh sách và UC04 Tìm kiếm, lọc sản phẩm (công khai). */
@Service
public class CatalogService {

    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 50;
    static final int MAX_KEYWORD_LENGTH = 100;
    /** ProductCondition (tài liệu enum 2.3, schema products.condition_level). */
    static final Set<String> CONDITION_LEVELS = Set.of("LIKE_NEW", "GOOD", "FAIR", "POOR");

    private final CatalogRepository catalog;

    public CatalogService(CatalogRepository catalog) {
        this.catalog = catalog;
    }

    /** UC03.1: danh sách, mới nhất trước, lọc theo danh mục. */
    @Transactional(readOnly = true)
    public PageResponse<ProductListItem> listProducts(Integer categoryId, Integer page, Integer size) {
        return query(ProductFilter.category(categoryId), page, size);
    }

    /** UC04.1: tìm theo từ khóa, lọc theo danh mục, khoảng giá, tình trạng và sắp xếp. */
    @Transactional(readOnly = true)
    public PageResponse<ProductListItem> searchProducts(
            String q, Integer categoryId, Long minPrice, Long maxPrice, String conditionLevel, String sort,
            Integer page, Integer size
    ) {
        if ((minPrice != null && minPrice < 0) || (maxPrice != null && maxPrice < 0)
                || (minPrice != null && maxPrice != null && minPrice > maxPrice)) {
            throw new InvalidPriceRangeException();
        }
        String level = conditionLevel == null || conditionLevel.isBlank() ? null : conditionLevel;
        if (level != null && !CONDITION_LEVELS.contains(level)) {
            throw new InvalidSearchParameterException("Tình trạng sản phẩm không hợp lệ.");
        }
        ProductFilter.Sort order = ProductFilter.Sort.fromParameter(sort);
        if (order == null) {
            throw new InvalidSearchParameterException("Cách sắp xếp không hợp lệ: newest, priceAsc hoặc priceDesc.");
        }
        return query(new ProductFilter(categoryId, keyword(q), minPrice, maxPrice, level, order), page, size);
    }

    private PageResponse<ProductListItem> query(ProductFilter filter, Integer page, Integer size) {
        int pageNumber = page == null ? 1 : page;
        int pageSize = size == null ? DEFAULT_SIZE : size;
        if (pageNumber < 1 || pageSize < 1 || pageSize > MAX_SIZE) {
            throw new InvalidPaginationException();
        }
        long total = catalog.countVisible(filter);
        long offset = (long) (pageNumber - 1) * pageSize;
        List<ProductListItem> items = offset >= total
                ? List.of()
                : catalog.findVisible(filter, pageSize, (int) offset);
        return PageResponse.of(items, pageNumber, pageSize, total);
    }

    /** Cắt khoảng trắng; rỗng nghĩa là không lọc theo từ khóa; thoát % _ để người dùng gõ chúng như chữ thường. */
    private static String keyword(String q) {
        if (q == null || q.isBlank()) {
            return null;
        }
        String trimmed = q.trim();
        if (trimmed.length() > MAX_KEYWORD_LENGTH) {
            throw new InvalidSearchParameterException("Từ khóa tối đa " + MAX_KEYWORD_LENGTH + " ký tự.");
        }
        return trimmed.replace("!", "!!").replace("%", "!%").replace("_", "!_");
    }

    @Transactional(readOnly = true)
    public List<CategoryOption> activeCategories() {
        return catalog.findActiveCategories();
    }
}
