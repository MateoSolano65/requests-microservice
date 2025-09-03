package co.com.pragma.model.exception;

public class ResourceConflictException extends BusinessException {
    
    private final ErrorType errorType;
    
    public ResourceConflictException(String message) {
        super(message);
        ErrorType foundType = ErrorType.findByMessage(message);
        this.errorType = foundType != null && foundType.getStatusCode() == 409 
            ? foundType 
            : ErrorType.LOAND_APLICATION_ALREADY_EXISTS;
    }
    
    public ResourceConflictException(ErrorType errorType) {
        super(errorType.getDefaultMessage());
        this.errorType = errorType;
    }
    
    @Override
    public int statusCode() { 
        return errorType.getStatusCode();
    }
    
    @Override
    public String code() { 
        return errorType.getErrorCode();
    }
}
