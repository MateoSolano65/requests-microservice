package co.com.pragma.model.response;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum ResponseCode {
    
    // Success codes
    LOAN_APPLICATION_CREATED("LA200", "Préstamo creado exitosamente"),
    LOAN_APPLICATION_UPDATED("LA201", "Préstamo actualizado exitosamente"),
    LOAN_APPLICATION_FOUND("LA202", "Préstamo encontrado"),
    LOAN_TYPES_FOUND("LA203", "Tipos de préstamo obtenidos exitosamente"),
    
    // Error codes
    LOAN_APPLICATION_NOT_ACTIVE("BUSINESS-001", "El prestamo no está activo"),
    LOAN_TYPE_NOT_FOUND("BUSINESS-002", "El tipo de préstamo no existe"),
    CLIENT_VALIDATION_ERROR("BUSINESS-003", "Error en la validación del cliente"),
    LOAN_APPLICATION_ALREADY_EXISTS("CONFLICT-002", "El prestamo ya existe"),
    
    // Common error codes
    VALIDATION_ERROR("VAL-001", "Error de validación"),
    INTERNAL_SERVER_ERROR("API-500", "Error interno del servidor"),
    BAD_REQUEST("API-400", "Solicitud incorrecta"),
    CONFLICT("API-409", "Conflicto con el recurso");
    
    private final String codeValue;
    private final String defaultMessage;
    
    private static final Map<String, ResponseCode> messageMap = new HashMap<>();
    
    static {
        for (ResponseCode code : values()) {
            messageMap.put(code.getDefaultMessage(), code);
        }
    }
    
    ResponseCode(String codeValue, String defaultMessage) {
        this.codeValue = codeValue;
        this.defaultMessage = defaultMessage;
    }
    
    public static ResponseCode findByMessage(String message) {
        return messageMap.get(message);
    }
    
    public static ResponseCode findByHttpStatus(int statusCode) {
        switch (statusCode) {
            case 400:
                return BAD_REQUEST;
            case 409:
                return CONFLICT;
            case 422:
                return VALIDATION_ERROR;
            case 500:
                return INTERNAL_SERVER_ERROR;
            default:
                return INTERNAL_SERVER_ERROR;
        }
    }
}