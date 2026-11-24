package com.marketplace.common;

import com.marketplace.cart.service.CartException;
import com.marketplace.auth.service.InvalidCredentialsException;
import com.marketplace.auth.service.AccountLockedException;
import com.marketplace.auth.service.DuplicateAccountException;
import com.marketplace.auth.service.InvalidRefreshTokenException;
import com.marketplace.order.service.ActorNotAllowedException;
import com.marketplace.order.service.InvalidTransitionException;
import com.marketplace.order.service.OrderNotFoundException;
import com.marketplace.order.service.ReasonRequiredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentials(InvalidCredentialsException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<Map<String, String>> handleAccountLocked(AccountLockedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(DuplicateAccountException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateAccount(DuplicateAccountException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRefreshToken(InvalidRefreshTokenException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("error", "Request validation failed");
        response.put("fields", errors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(CartException.class)
    public ResponseEntity<Map<String, String>> handleCartException(CartException exception) {
        return ResponseEntity.status(exception.getStatus())
                .body(Map.of("code", exception.getCode(), "message", exception.getMessage()));
    }

    @ExceptionHandler(InvalidTransitionException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTransition(InvalidTransitionException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("code", "INVALID_TRANSITION", "message", exception.getMessage()));
    }

    @ExceptionHandler(ReasonRequiredException.class)
    public ResponseEntity<Map<String, String>> handleReasonRequired(ReasonRequiredException exception) {
        return ResponseEntity.badRequest()
                .body(Map.of("code", "REASON_REQUIRED", "message", exception.getMessage()));
    }

    @ExceptionHandler(ActorNotAllowedException.class)
    public ResponseEntity<Map<String, String>> handleActorNotAllowed(ActorNotAllowedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("code", "ACTOR_NOT_ALLOWED", "message", exception.getMessage()));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleOrderNotFound(OrderNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("code", "ORDER_NOT_FOUND", "message", exception.getMessage()));
    }
}
