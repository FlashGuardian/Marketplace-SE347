package com.marketplace.category.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.marketplace.category.model.Category;

public record CategoryResponse(
        Integer id,
        String name,
        String description,
        @JsonProperty("isActive") boolean isActive,
        long productCount
) {

    public static CategoryResponse of(Category category, long productCount) {
        return new CategoryResponse(
                category.getId(), category.getName(), category.getDescription(), category.isActive(), productCount);
    }
}
