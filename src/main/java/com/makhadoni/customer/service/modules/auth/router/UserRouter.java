package com.makhadoni.customer.service.modules.auth.router;

import com.makhadoni.customer.service.modules.auth.dto.UserDto;
import com.makhadoni.customer.service.modules.auth.service.UserServiceImpl;
import com.makhadoni.customer.service.modules.customer.dto.CustomerDto;
import com.makhadoni.customer.service.modules.customer.service.CustomerService;
import com.makhadoni.customer.service.modules.auth.handler.UserHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
                    @RouterOperation(path = "/api/auth/login", produces = {
                            MediaType.APPLICATION_JSON_VALUE},
                            method = RequestMethod.POST, beanClass = UserServiceImpl.class,
                            operation = @Operation(operationId = "login",
                                    responses = {
                                            @ApiResponse(responseCode = "200", description = "successful operation",
                                                    content =
                                                    @Content(
                                                            schema =
                                                            @Schema(implementation = String.class))),
                                            @ApiResponse(responseCode = "401", description = UserHandler.UNOTHORIXED_MESSAGE),},
                                    requestBody =
                                    @RequestBody(content = @Content(schema = @Schema(implementation = UserDto.class))))),
                    @RouterOperation(path = "/api/auth/signup", produces = {
                            MediaType.APPLICATION_JSON_VALUE},
                            method = RequestMethod.PUT, beanClass = UserServiceImpl.class,
                            operation = @Operation(operationId = "signup",
                                    responses = {
                                            @ApiResponse(responseCode = "200", description = "User with username {username} has successfully been created",
                                                    content =
                                                    @Content(
                                                            schema =
                                                            @Schema(implementation = String.class))),
                                            @ApiResponse(responseCode = "409", description = "User with username: {username} already exists"),},
                                    requestBody =
                                    @RequestBody(content = @Content(schema = @Schema(implementation = UserDto.class)))))
            })
    public RouterFunction<ServerResponse> userRoute(UserHandler handler) {
        return
                RouterFunctions.nest(path("/api/auth"),
                        route(POST("/login"),handler::signIn)
                        .andRoute(PUT("/signup"), handler::createUser));
    }
}
