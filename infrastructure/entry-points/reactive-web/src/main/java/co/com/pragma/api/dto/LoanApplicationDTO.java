package co.com.pragma.api.dto;

import co.com.pragma.api.utils.Regex;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    description = "Información de la solicitud de préstamo",
    requiredProperties = { "loanAmount", "termInMonths", "documentNumber", "loanType" }
)
public class LoanApplicationDTO {
    
    @Schema(description = "Identificador del préstamo (generado automáticamente)", example = "1")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Long idLoan;
    
    @Schema(description = "Monto del préstamo en pesos", example = "5000000.00")
    @NotNull(message = "El monto del préstamo es obligatorio")
    @DecimalMin(value = "1.0", message = "El monto del préstamo debe ser mayor que cero")
    private BigDecimal loanAmount;
    
    @Schema(description = "Plazo del préstamo en meses", example = "24")
    @NotNull(message = "El plazo del préstamo es obligatorio")
    @Min(value = 1, message = "El plazo debe ser al menos 1 mes")
    private Long termInMonths;
    
    @Schema(description = "Número de documento del solicitante", example = "1098765432")
    @NotBlank(message = "El número de documento es obligatorio")
    @Pattern(regexp = Regex.DOCUMENT_NUMBER_REGEX, message = "El número de documento debe tener entre 5 y 20 dígitos")
    private String documentNumber;
    
    @Schema(description = "Tipo de préstamo", example = "1")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "El tipo de préstamo es obligatorio")
    private Long loanType;
    
    @Schema(description = "Estado del préstamo", example = "1", hidden = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long loanStatus;
}
