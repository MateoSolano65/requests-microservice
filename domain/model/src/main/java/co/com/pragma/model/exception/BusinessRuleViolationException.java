package co.com.pragma.model.exception;

import co.com.pragma.model.response.ResponseCode;

public class BusinessRuleViolationException extends BusinessException {
    
    private final ResponseCode responseCode;
    
    public BusinessRuleViolationException(String message) {
        super(message);
        ResponseCode foundCode = ResponseCode.findByMessage(message);
        this.responseCode = foundCode != null
            ? foundCode 
            : ResponseCode.LOAN_APPLICATION_NOT_ACTIVE;
    }
    
    public BusinessRuleViolationException(ResponseCode responseCode) {
        super(responseCode.getDefaultMessage());
        this.responseCode = responseCode;
    }
    
    @Override
    public int statusCode() { 
        return 400; // Bad Request
    }
    
    @Override
    public String code() { 
        return responseCode.getCodeValue();
    }
}