package com.makhadoni.customer.service.modules.customer.router;

import com.makhadoni.customer.service.exception.dto.ErrorDto;
import com.makhadoni.customer.service.modules.customer.dto.CustomerDto;
import com.makhadoni.customer.service.modules.customer.handler.CustomerHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@SecurityScheme(name = "Authorization", scheme = "Bearer", bearerFormat = "JWT", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class CustomerRouter {

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(path = "/api/customer/all", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET,
                            operation = @Operation(operationId = "getCustomers", tags = { "customer service"}, security = @SecurityRequirement(name = "Authorization") , responses = {
                                        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = CustomerDto.class))),
                                        @ApiResponse(responseCode = "400", description = "When bad payload/params supplied", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                                        @ApiResponse(responseCode = "401", description = "When token for auth is missing or invalid", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                                        @ApiResponse(responseCode = "404", description = "when no data found", content = @Content(schema = @Schema(implementation = ErrorDto.class)))},
                                    parameters = {
                                        @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "int", nullable = true)),
                                        @Parameter(in = ParameterIn.QUERY, name = "size", schema = @Schema(type = "int", nullable = true)),
                                        @Parameter(in = ParameterIn.QUERY, name = "firstName", schema = @Schema(type = "string", nullable = true))})
                    ),
                    @RouterOperation(path = "/api/customer/{customerId}", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET,
                            operation = @Operation(operationId = "getCustomer", tags = { "customer service"}, security = @SecurityRequirement(name = "Authorization"), responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = CustomerDto.class))),
                                    @ApiResponse(responseCode = "400", description = "When bad payload/params supplied", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                                    @ApiResponse(responseCode = "401", description = "When token for auth is missing or invalid", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                                    @ApiResponse(responseCode = "404", description = "when no data found", content = @Content(schema = @Schema(implementation = ErrorDto.class)))},
                                    parameters = {@Parameter(in = ParameterIn.PATH, name = "customerId", required = true , schema = @Schema(type = "int"))})),

                    @RouterOperation(path = "/api/customer", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.POST,
                            operation = @Operation(operationId = "createCustomer", tags = { "customer service"},security = @SecurityRequirement(name = "Authorization"), responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = CustomerDto.class))),
                                    @ApiResponse(responseCode = "400", description = "When bad payload/params supplied", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                                    @ApiResponse(responseCode = "401", description = "When token for auth is missing or invalid", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                                    @ApiResponse(responseCode = "409", description = "when supplied data already exist", content = @Content(schema = @Schema(implementation = ErrorDto.class))),},
                                    requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = CustomerDto.class))))),

                    @RouterOperation(path = "/api/customer", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.PATCH,
                            operation = @Operation(operationId = "UpdateCustomer", tags = { "customer service"}, security = @SecurityRequirement(name = "Authorization"), responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = CustomerDto.class))),
                                    @ApiResponse(responseCode = "401", description = "When token for auth is missing or invalid", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                                    @ApiResponse(responseCode = "409", description = "when supplied data already exist", content = @Content(schema = @Schema(implementation = CustomerDto.class)))},
                                    requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = CustomerDto.class))))),

                    @RouterOperation(path = "/api/customer/{customerId}", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.DELETE,
                            operation = @Operation(operationId = "deleteCustomer", tags = { "customer service"}, security = @SecurityRequirement(name = "Authorization"),responses = {
                                    @ApiResponse(responseCode = "204", description = "successful operation", content = @Content(mediaType = "no content")),
                                    @ApiResponse(responseCode = "400", description = "When bad payload/params supplied", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                                    @ApiResponse(responseCode = "401", description = "When token for auth is missing or invalid", content = @Content(schema = @Schema(implementation = ErrorDto.class))),},
                                    parameters = {@Parameter(in = ParameterIn.PATH, name = "customerId", required = true, schema = @Schema(type = "int"))}
                    )),
            })
    public RouterFunction<ServerResponse> customerRoute(CustomerHandler handler) {
        return
                RouterFunctions.nest(path("/api/customer"),
                        route(GET("/all"),handler::getCustomers)
                                .andRoute(GET("/{customerId}"), handler::getCustomer)
                                .andRoute(POST("").and(accept(MediaType.APPLICATION_JSON)),handler::createCustomer)
                                .andRoute(PATCH("").and(accept(MediaType.APPLICATION_JSON)),handler::updateCustomer)
                                .andRoute(DELETE("/{customerId}"), handler::deleteCustomer));
    }
}


