package com.makhadoni.customer.service.exception.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author makhado on 2023/11/21
 */
public class ErrorDto {

    @Schema(description = "error code", example = "100")
    private String code;
    @Schema(description = "error message", example = "error message")
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
