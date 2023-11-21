package com.makhadoni.customer.service.modules.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

public class CustomerDto implements Serializable {

    @Schema(description = "customer id", example = "1")
    private Integer id;
    @Schema(description = "customer first name", example = "bruce")
    private String firstName;
    @Schema(description = "customer last name", example = "wayne")
    private String lastName;
    @Schema(description = "customer email", example = "bruce@wayneenterprise.batman")
    private String email;
    @Schema(description = "customers age", example = "44")
    private int age;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
