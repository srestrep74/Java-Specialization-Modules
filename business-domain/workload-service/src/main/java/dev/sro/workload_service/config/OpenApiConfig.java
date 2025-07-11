package dev.sro.workload_service.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Workload Management API",
        version = "1.0",
        description = "API for managing trainer workloads and monthly summaries",
        contact = @Contact(
            name = "Sebastian Restrepo",
            email = "srestrep74@eafit.edu.co"),
        license = @License(
            name = "Apache 2.0", 
            url = "http://www.apache.org/licenses/LICENSE-2.0"),
        summary = "Comprehensive API for trainer workload management system"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
    
} 