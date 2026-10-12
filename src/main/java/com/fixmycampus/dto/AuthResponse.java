package com.fixmycampus.dto;

import com.fixmycampus.model.Role;

public record AuthResponse(String token, String name, String email, Role role) {
}
