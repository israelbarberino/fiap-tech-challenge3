package br.com.fiap.hospital.appointment.security;

import br.com.fiap.hospital.sharedkernel.security.UserRole;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtServiceTest {

    @Test
    void shouldGenerateAndParseToken() {
        JwtService jwtService = new JwtService("0123456789012345678901234567890123456789012345678901234567890123", 120);
        AuthenticatedUser user = new AuthenticatedUser(UUID.randomUUID(), "doctor1", UserRole.ROLE_DOCTOR);

        String token = jwtService.generateToken(user);
        AuthenticatedUser parsed = jwtService.parseToken(token);

        assertEquals(user.userId(), parsed.userId());
        assertEquals(user.username(), parsed.username());
        assertEquals(user.role(), parsed.role());
    }
}
