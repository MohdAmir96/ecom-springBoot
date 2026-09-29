package com.amir.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotBlank @Size(max = 80) String userName,
        @NotBlank @Size(min = 8, max = 72) String password) {
}
