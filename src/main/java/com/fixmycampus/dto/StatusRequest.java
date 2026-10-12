package com.fixmycampus.dto;

import com.fixmycampus.model.Status;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StatusRequest(
        @NotNull(message = "Choose a status") Status status,
        @Size(max = 1000, message = "Note must be 1000 characters or fewer") String remarks) {
}
