package br.com.fiap.hospital.history.security;

import br.com.fiap.hospital.sharedkernel.security.UserRole;
import java.util.UUID;

public record AuthenticatedUser(UUID userId, String username, UserRole role) {
}
