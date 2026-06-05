package com.taotaoapi.exception;

import com.taotaoapi.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {

        ApiResponse<Void> body = new ApiResponse<>();
        body.setCode(String.valueOf(ex.getCode()));
        body.setMsg(ex.getMessage());

        return ResponseEntity.ok(body);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        ex.printStackTrace();
        ApiResponse<Void> body = ApiResponse.error(
                "500",
                "系統錯誤"
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }
}