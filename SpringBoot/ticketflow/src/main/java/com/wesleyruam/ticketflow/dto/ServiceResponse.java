package com.wesleyruam.ticketflow.dto;

public class ServiceResponse<T> {
    private boolean success;
    private String message;
    private T data;
    
    // Construtor sem data
    public ServiceResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
    }
    
    // Construtor com data
    public ServiceResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    // Métodos estáticos para facilitar a criação
    public static <T> ServiceResponse<T> success(String message) {
        return new ServiceResponse<>(true, message);
    }
    
    public static <T> ServiceResponse<T> success(String message, T data) {
        return new ServiceResponse<>(true, message, data);
    }
    
    public static <T> ServiceResponse<T> error(String message) {
        return new ServiceResponse<>(false, message);
    }
    
    public static <T> ServiceResponse<T> error(String message, T data) {
        return new ServiceResponse<>(false, message, data);
    }
    
    // Getters e Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
}