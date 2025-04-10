package com.sro.SpringCoreTask1.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Gym Management API",
        version = "1.0",
        description = "API for managing trainers, trainees and training sessions",
        contact = @Contact(
            name = "Sebastian Restrepo",
            email = "srestrep74@eafit.edu.co"),
        license = @License(
            name = "Apache 2.0", 
            url = "http://www.apache.org/licenses/LICENSE-2.0"),
        summary = "Comprehensive API for gym management system"))
public class OpenApiConfig {
    
}
