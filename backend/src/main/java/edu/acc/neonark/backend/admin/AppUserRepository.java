package edu.acc.neonark.backend.admin;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    @EntityGraph(attributePaths = "roles")
    List<AppUser> findAllByOrderByFullNameAsc();
}

