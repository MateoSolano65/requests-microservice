package co.com.pragma.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Solicitudes API",
                version = "1.0.0",
                description = "API para la gestión de solicitudes de crédito",
                contact = @Contact(
                        name = "Equipo CrediYa",
                        email = "soporte@crediya.co",
                        url = ""
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        )
)
public class OpenApiConfig {
}