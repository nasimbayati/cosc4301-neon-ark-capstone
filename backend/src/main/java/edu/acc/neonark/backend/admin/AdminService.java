package edu.acc.neonark.backend.admin;

import edu.acc.neonark.backend.error.ForbiddenException;
import edu.acc.neonark.backend.error.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class AdminService {
    private final AppUserRepository userRepository;

    public AdminService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listUsers(String roleHeader) {
        if (roleHeader == null || roleHeader.isBlank()) {
            throw new UnauthorizedException("Admin role header is required");
        }
        if (!"ADMIN".equalsIgnoreCase(roleHeader.trim())) {
            throw new ForbiddenException("Only ADMIN users can view system users");
        }
        return userRepository.findAllByOrderByFullNameAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    private UserResponse toResponse(AppUser user) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .sorted(Comparator.naturalOrder())
                .toList();
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.isActive(),
                roles
        );
    }
}

