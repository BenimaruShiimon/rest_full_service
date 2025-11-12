package com.example.restfullservice.model.dto;

import java.time.LocalDate;

public record UserCreateRequest(
        String name,
        String email,
        LocalDate birthDate
) {
}