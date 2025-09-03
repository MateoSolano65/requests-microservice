package co.com.pragma.model.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum ErrorType {
    
    LOAND_APLICATION_NOT_ACTIVE(400, "BUSINESS-001", "El prestamo no está activo"),
    LOAND_APLICATION_ALREADY_EXISTS(409, "CONFLICT-002", "El prestamo ya existe");
    
    private final int statusCode;
    private final String errorCode;
    private final String defaultMessage;
    
    private static final Map<String, ErrorType> messageMap = new HashMap<>();
    
    static {
        for (ErrorType error : values()) {
            messageMap.put(error.getDefaultMessage(), error);
        }
    }
    
    ErrorType(int statusCode, String errorCode, String defaultMessage) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.defaultMessage = defaultMessage;
    }
    
    public static ErrorType findByMessage(String message) {
        return messageMap.get(message);
    }

}