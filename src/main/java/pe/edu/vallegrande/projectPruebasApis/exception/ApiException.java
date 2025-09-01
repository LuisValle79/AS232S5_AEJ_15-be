package pe.edu.vallegrande.projectPruebasApis.exception;

public class ApiException extends RuntimeException {
    
    private final String resourceName;
    private final String errorCode;
    
    public ApiException(String message, String resourceName, String errorCode) {
        super(message);
        this.resourceName = resourceName;
        this.errorCode = errorCode;
    }
    
    public String getResourceName() {
        return resourceName;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}