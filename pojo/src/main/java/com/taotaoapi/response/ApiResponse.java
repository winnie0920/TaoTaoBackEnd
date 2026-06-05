package com.taotaoapi.response;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private String code;
    private String msg;
    private T data;

    public static <T> ApiResponse<T> success(String msg) {
        ApiResponse<T> res = new ApiResponse<>();
        res.code = "200";
        res.msg = msg;
        return res;
    }

    public static <T> ApiResponse<T> success(String msg,T data) {
        ApiResponse<T> res = new ApiResponse<>();
        res.code = "200";
        res.msg = msg;
        res.data = data;
        return res;
    }

    public static <T> ApiResponse<T> error(String code, String msg) {
        ApiResponse<T> res = new ApiResponse<>();
        res.code = code;
        res.msg = msg;
        return res;
    }
}