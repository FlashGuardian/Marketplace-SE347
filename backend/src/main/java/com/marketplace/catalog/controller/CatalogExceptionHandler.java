package com.marketplace.catalog.controller;

import com.marketplace.catalog.service.InvalidPaginationException;
import com.marketplace.catalog.service.InvalidPriceRangeException;
import com.marketplace.catalog.service.InvalidSearchParameterException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

/** Lỗi của trang công khai theo định dạng tài liệu API {code, message}; chỉ áp dụng cho hai controller này. */
@RestControllerAdvice(assignableTypes = {ProductController.class, PublicCategoryController.class})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CatalogExceptionHandler {

    @ExceptionHandler(InvalidPaginationException.class)
    public ResponseEntity<Map<String, Object>> invalidPagination(InvalidPaginationException e) {
        return error("INVALID_PAGINATION", e.getMessage());
    }

    @ExceptionHandler(InvalidPriceRangeException.class)
    public ResponseEntity<Map<String, Object>> invalidPriceRange(InvalidPriceRangeException e) {
        return error("INVALID_PRICE_RANGE", e.getMessage());
    }

    @ExceptionHandler(InvalidSearchParameterException.class)
    public ResponseEntity<Map<String, Object>> invalidSearchParameter(InvalidSearchParameterException e) {
        return error("INVALID_REQUEST", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> invalidParameter(MethodArgumentTypeMismatchException e) {
        return error("INVALID_REQUEST", "Tham số " + e.getName() + " không hợp lệ.");
    }

    private static ResponseEntity<Map<String, Object>> error(String code, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", code);
        body.put("message", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
