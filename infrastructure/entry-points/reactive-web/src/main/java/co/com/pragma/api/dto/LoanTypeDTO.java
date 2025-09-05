package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(
    description = "Información de un tipo de préstamo"
)
public class LoanTypeDTO {
    
    @Schema(description = "Identificador del tipo de préstamo", example = "1")
    private Long id;
    
    @Schema(description = "Nombre del tipo de préstamo", example = "Personal")
    private String name;
    
    @Schema(description = "Monto mínimo", example = "1000000.00")
    private BigDecimal minAmount;
    
    @Schema(description = "Monto máximo", example = "10000000.00")
    private BigDecimal maxAmount;
    
    @Schema(description = "Tasa de interés anual", example = "18.50")
    private BigDecimal interestRate;
    
    @Schema(description = "Indica si el préstamo tiene validación automática", example = "true")
    private Boolean automaticValidation;
}
