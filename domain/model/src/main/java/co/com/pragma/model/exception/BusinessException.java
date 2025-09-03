package co.com.pragma.model.exception;

public abstract class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public abstract int statusCode();
    
    public abstract String code();
}