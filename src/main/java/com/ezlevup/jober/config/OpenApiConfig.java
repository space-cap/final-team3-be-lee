package com.ezlevup.jober.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI joberOpenAPI() {
        String jwtSchemeName = "JWT";
        
        return new OpenAPI()
                .info(new Info()
                        .title("Jober API")
                        .description("Jober 프로젝트의 REST API 문서입니다.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Jober Team")
                                .email("support@ezlevup.com")))
                .components(new Components()
                        .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                                .name(jwtSchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT 토큰을 입력하세요. Bearer 접두사는 자동으로 추가됩니다.")))
                .addSecurityItem(new SecurityRequirement().addList(jwtSchemeName));
    }
}