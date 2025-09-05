package co.com.pragma.model.exception;

import co.com.pragma.model.response.ResponseCode;

public class ResourceConflictException extends BusinessException {
    
    private final ResponseCode responseCode;
    
    public ResourceConflictException(String message) {
        super(message);
        ResponseCode foundCode = ResponseCode.findByMessage(message);
        this.responseCode = foundCode != null
            ? foundCode 
            : ResponseCode.LOAN_APPLICATION_ALREADY_EXISTS;
    }
    
    public ResourceConflictException(ResponseCode responseCode) {
        super(responseCode.getDefaultMessage());
        this.responseCode = responseCode;
    }
    
    @Override
    public int statusCode() { 
        return 409; // Conflict
    }
    
    @Override
    public String code() { 
        return responseCode.getCodeValue();
    }
}