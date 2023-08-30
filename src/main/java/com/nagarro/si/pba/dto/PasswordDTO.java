package com.nagarro.si.pba.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PasswordDTO (
        @Size(min = 7, max = 20)
        @NotNull
        String password) {
}