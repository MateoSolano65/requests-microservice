package co.com.pragma.model.exception;

public class BusinessRuleViolationException extends BusinessException {
    
    private final ErrorType errorType;
    
    public BusinessRuleViolationException(String message) {
        super(message);
        ErrorType foundType = ErrorType.findByMessage(message);
        this.errorType = foundType != null && foundType.getStatusCode() == 400 
            ? foundType 
            : ErrorType.LOAND_APLICATION_NOT_ACTIVE;
    }
    
    public BusinessRuleViolationException(ErrorType errorType) {
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
