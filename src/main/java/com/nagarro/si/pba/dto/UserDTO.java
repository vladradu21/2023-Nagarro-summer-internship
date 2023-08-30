package com.nagarro.si.pba.dto;

import com.nagarro.si.pba.model.Currency;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserDTO(
        Integer id,

        @Email(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email")
        @NotNull
        String email,

        @Size(min = 4, max = 20)
        @NotNull
        String username,

        @NotNull
        Currency defaultCurrency,

        @Size(min = 4, max = 20)
        @NotNull
        String firstName,

        @Size(max = 50)
        @NotNull
        String lastName,

        String country,

        @NotNull
        Integer age) {
}