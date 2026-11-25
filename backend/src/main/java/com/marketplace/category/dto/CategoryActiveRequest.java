package com.marketplace.category.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record CategoryActiveRequest(
        @NotNull(message = "Cần chỉ rõ bật hay tắt danh mục.")
        @JsonProperty("isActive")
        Boolean isActive
) {
}
