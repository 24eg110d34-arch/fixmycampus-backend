package com.fixmycampus.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AssignRequest(
        @NotNull(message = "Choose a department") Long departmentId,
        @Size(max = 1000, message = "Note must be 1000 characters or fewer") String remarks) {
}
