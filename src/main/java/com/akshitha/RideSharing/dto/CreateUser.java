package com.akshitha.RideSharing.dto;

import com.akshitha.RideSharing.enums.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description="Create User requst")
public record CreateUser(
    @NotBlank(message="Name is required")
    String name,
    @NotBlank(message="Phone number is required")
    @Pattern(regexp="^[0-9]{10}$",message="Phone number must be of 10 digits")
    String phone,
    @NotNull(message="Role is required")
    Role role
) {
}
