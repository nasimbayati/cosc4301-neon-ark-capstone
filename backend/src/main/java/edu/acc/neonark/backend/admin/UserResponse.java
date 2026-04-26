package edu.acc.neonark.backend.admin;

import java.util.List;

public record UserResponse(
        Long id,
        String username,
        String fullName,
        String email,
        String phone,
        boolean active,
        List<String> roles
) {
}

