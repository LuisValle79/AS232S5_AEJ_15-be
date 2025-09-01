package pe.edu.vallegrande.projectPruebasApis.model;

import java.time.LocalDateTime;

public class ApiResponse<T> {
    private String status;
    private String message;
    private T data;
    private String timestamp;
    
    // Propiedad para indicar si hay datos disponibles
    private boolean hasData;
    
    public ApiResponse() {
    }
    
    public ApiResponse(String status, String message, T data, String timestamp) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
        this.hasData = data != null;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    // Agregar getter y setter para hasData
    public boolean isHasData() {
        return hasData;
    }
    
    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", "Operación exitosa", data, LocalDateTime.now().toString());
    }
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>("success", message, data, LocalDateTime.now().toString());
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("error", message, null, LocalDateTime.now().toString());
    }
}