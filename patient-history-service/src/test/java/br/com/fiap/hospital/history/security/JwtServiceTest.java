package br.com.fiap.hospital.history.security;

import br.com.fiap.hospital.sharedkernel.security.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtServiceTest {

    @Test
    void shouldParseTokenCreatedWithSameClaimsContract() {
        String secret = "0123456789012345678901234567890123456789012345678901234567890123";
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        UUID userId = UUID.randomUUID();

        String token = Jwts.builder()
                .subject(userId.toString())
                .claim("username", "doctor1")
                .claim("role", UserRole.ROLE_DOCTOR.name())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(key)
                .compact();

        JwtService jwtService = new JwtService(secret);
        AuthenticatedUser parsed = jwtService.parseToken(token);

        assertEquals(userId, parsed.userId());
        assertEquals("doctor1", parsed.username());
        assertEquals(UserRole.ROLE_DOCTOR, parsed.role());
    }
}
