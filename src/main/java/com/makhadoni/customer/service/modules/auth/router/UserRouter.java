package com.makhadoni.customer.service.modules.auth.router;

import com.makhadoni.customer.service.exception.dto.ErrorDto;
import com.makhadoni.customer.service.modules.auth.dto.UserDto;
import com.makhadoni.customer.service.modules.auth.handler.UserHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
public class UserRouter {

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(path = "/auth/signup", produces = {MediaType.APPLICATION_JSON_VALUE},method = RequestMethod.PUT,
                            operation = @Operation(operationId = "signup",  tags = { "user auth"}, responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "400", description = "When bad payload/params supplied", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                                    @ApiResponse(responseCode = "409", description = "when supplied data already exist", content = @Content(schema = @Schema(implementation = ErrorDto.class))),},
                                    requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UserDto.class)))
                            )),
                    @RouterOperation(path = "/auth/login", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.POST,
                            operation = @Operation(operationId = "login", tags = { "user auth"}, responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = UserDto.class))),
                                    @ApiResponse(responseCode = "400", description = "When bad request supplied", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                                    @ApiResponse(responseCode = "401", description = "When invalid login details supplied", content = @Content(schema = @Schema(implementation = ErrorDto.class))),},
                                    requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UserDto.class)))
                            )),
            })
    public RouterFunction<ServerResponse> userRoute(UserHandler handler) {
        return
                RouterFunctions.nest(path("/auth"),
                        route(POST("/login").and(accept(MediaType.APPLICATION_JSON)),handler::signIn)
                        .andRoute(PUT("/signup").and(accept(MediaType.APPLICATION_JSON)), handler::createUser));
    }
}
