package co.com.pragma.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseApiDto<T> {
    private Integer status;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String message;
    private T data;
    private ErrorInfoDto error;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> meta;
}


