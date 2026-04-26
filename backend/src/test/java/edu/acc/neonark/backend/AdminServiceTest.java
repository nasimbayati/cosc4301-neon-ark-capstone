package edu.acc.neonark.backend;

import edu.acc.neonark.backend.admin.AdminService;
import edu.acc.neonark.backend.admin.AppUserRepository;
import edu.acc.neonark.backend.error.ForbiddenException;
import edu.acc.neonark.backend.error.UnauthorizedException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminServiceTest {

    private final AppUserRepository userRepository = mock(AppUserRepository.class);
    private final AdminService adminService = new AdminService(userRepository);

    @Test
    void rejectsMissingAdminRoleHeader() {
        assertThrows(UnauthorizedException.class, () -> adminService.listUsers(null));
    }

    @Test
    void rejectsNonAdminRoleHeader() {
        assertThrows(ForbiddenException.class, () -> adminService.listUsers("STAFF"));
    }

    @Test
    void acceptsAdminRoleHeader() {
        when(userRepository.findAllByOrderByFullNameAsc()).thenReturn(List.of());

        assertTrue(adminService.listUsers("ADMIN").isEmpty());
    }
}

