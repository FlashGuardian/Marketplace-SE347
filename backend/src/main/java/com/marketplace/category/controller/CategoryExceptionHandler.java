package com.marketplace.category.controller;

import com.marketplace.category.service.CategoryHasProductsException;
import com.marketplace.category.service.CategoryNameExistsException;
import com.marketplace.category.service.CategoryNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Lỗi của UC19 theo định dạng tài liệu API: {code, message, fields?}. Chỉ áp dụng cho
 * AdminCategoryController nên không đổi cách trả lỗi của các API khác.
 */
@RestControllerAdvice(assignableTypes = AdminCategoryController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CategoryExceptionHandler {

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Map<String, Object>> notFound(CategoryNotFoundException e) {
        return error(HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND", e.getMessage(), null);
    }

    @ExceptionHandler(CategoryNameExistsException.class)
    public ResponseEntity<Map<String, Object>> nameExists(CategoryNameExistsException e) {
        return error(HttpStatus.CONFLICT, "CATEGORY_NAME_EXISTS", e.getMessage(), null);
    }

    @ExceptionHandler(CategoryHasProductsException.class)
    public ResponseEntity<Map<String, Object>> hasProducts(CategoryHasProductsException e) {
        return error(HttpStatus.CONFLICT, "CATEGORY_HAS_PRODUCTS", e.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> invalid(MethodArgumentNotValidException e) {
        Map<String, String> fields = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(f -> fields.putIfAbsent(f.getField(), f.getDefaultMessage()));
        if (fields.containsKey("name") && "NotBlank".equals(
                e.getBindingResult().getFieldError("name").getCode())) {
            return error(HttpStatus.BAD_REQUEST, "NAME_REQUIRED", fields.get("name"), fields);
        }
        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "Dữ liệu gửi lên không hợp lệ.", fields);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> unreadable(HttpMessageNotReadableException e) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "Nội dung gửi lên không phải JSON hợp lệ.", null);
    }

    private static ResponseEntity<Map<String, Object>> error(
            HttpStatus status, String code, String message, Map<String, String> fields) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", code);
        body.put("message", message);
        if (fields != null) {
            body.put("fields", fields);
        }
        return ResponseEntity.status(status).body(body);
    }
}
