package com.ragchat.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "RAG Chat Storage Microservice API",
                version = "1.0.0",
                description = "Backend microservice to store chat histories for RAG-based chatbot systems. " +
                        "Provides APIs for managing chat sessions and messages with support for " +
                        "session management features like rename, mark as favorite, and delete.",
                contact = @Contact(
                        name = "API Support",
                        email = "support@ragchat.com"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(url = "/", description = "Current Server")
        }
)
@SecurityScheme(
        name = "apiKey",
        description = "API Key authentication. Provide your API key in the X-API-Key header.",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "X-API-Key"
)
public class OpenApiConfig {
}
