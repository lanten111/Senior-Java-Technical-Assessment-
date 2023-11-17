package com.makhadoni.customer.service.config;

import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi CustomerGroupApi() {
        return GroupedOpenApi.builder()
                .group("Customer")
                .pathsToMatch("/swagger-demo/customer/**")
                .addOpenApiCustomiser(getOpenApiCustomiser())
                .build();
    }

    public OpenApiCustomiser getOpenApiCustomiser() {

        return openAPI -> openAPI.getPaths().values().stream().flatMap(pathItem ->
                        pathItem.readOperations().stream())
                .forEach(operation -> {
                    operation.addParametersItem(new Parameter().name("Authorization").in("header").
                            schema(new StringSchema().example("token")).required(true));
                    operation.addParametersItem(new Parameter().name("userId").in("header").
                            schema(new StringSchema().example("test")).required(true));
                });
    }

}
