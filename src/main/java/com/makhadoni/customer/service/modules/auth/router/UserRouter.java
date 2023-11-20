package com.makhadoni.customer.service.modules.auth.router;

import com.makhadoni.customer.service.modules.customer.service.CustomerService;
import com.makhadoni.customer.service.modules.auth.handler.UserHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
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
//    @RouterOperations(
//            {
//                    @RouterOperation(path = "/api/customer/{customerId}", produces = {
//                            MediaType.APPLICATION_JSON_VALUE},
//                            method = RequestMethod.DELETE, beanClass = CustomerService.class, beanMethod = "deleteCustomer",
//                            operation = @Operation(operationId = "deleteCustomer",
//                                    responses = {
//                                            @ApiResponse(responseCode = "204", description = "successful operation", content = @Content(mediaType = "Boolean")),
//                                            @ApiResponse(responseCode = "400", description = "Bad parameter input"),},
//                                    parameters = {
//                                            @Parameter(in = ParameterIn.PATH, name = "customerId")}
//                            )),
//            })
    public RouterFunction<ServerResponse> userRoute(UserHandler handler) {
        return
                RouterFunctions.nest(path("/api"),
                        route(POST("/login"),handler::signIn)
                        .andRoute(PUT("/signup"), handler::createUser));
    }
}
