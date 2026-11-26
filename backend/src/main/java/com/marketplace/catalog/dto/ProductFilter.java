package com.marketplace.catalog.dto;

/**
 * Điều kiện lọc danh sách công khai. Mọi trường có thể null (không lọc). keyword đã được chuẩn hóa
 * (cắt khoảng trắng, thoát ký tự đại diện của LIKE) và sort đã được kiểm tra thuộc danh sách cho phép.
 */
public record ProductFilter(
        Integer categoryId,
        String keyword,
        Long minPrice,
        Long maxPrice,
        String conditionLevel,
        Sort sort
) {

    public static ProductFilter category(Integer categoryId) {
        return new ProductFilter(categoryId, null, null, null, null, Sort.NEWEST);
    }

    /** Cách sắp xếp (UC04: newest, priceAsc, priceDesc); mỗi cách có thêm khóa phụ để thứ tự ổn định. */
    public enum Sort {
        NEWEST("newest", "p.created_at DESC, p.id DESC"),
        PRICE_ASC("priceAsc", "p.price ASC, p.created_at DESC, p.id DESC"),
        PRICE_DESC("priceDesc", "p.price DESC, p.created_at DESC, p.id DESC");

        private final String parameter;
        private final String orderBy;

        Sort(String parameter, String orderBy) {
            this.parameter = parameter;
            this.orderBy = orderBy;
        }

        public String orderBy() {
            return orderBy;
        }

        public static Sort fromParameter(String value) {
            if (value == null || value.isBlank()) {
                return NEWEST;
            }
            for (Sort sort : values()) {
                if (sort.parameter.equals(value)) {
                    return sort;
                }
            }
            return null;
        }
    }
}
