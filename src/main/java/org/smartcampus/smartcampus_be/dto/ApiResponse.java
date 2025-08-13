package org.smartcampus.smartcampus_be.dto;

public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public ApiResponse(int code, String message, T data){
        this.code = code; this.message = message; this.data = data;
    }
    public static <T> ApiResponse<T> ok(String message, T data){
        return new ApiResponse<>(200, message, data);
    }
    public static <T> ApiResponse<T> error(int code, String message){
        return new ApiResponse<>(code, message, null);
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
