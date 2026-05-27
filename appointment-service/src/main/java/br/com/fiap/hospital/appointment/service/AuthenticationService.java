package br.com.fiap.hospital.appointment.service;

import br.com.fiap.hospital.appointment.domain.UserEntity;
import br.com.fiap.hospital.appointment.dto.LoginRequest;
import br.com.fiap.hospital.appointment.dto.LoginResponse;
import br.com.fiap.hospital.appointment.exception.BusinessException;
import br.com.fiap.hospital.appointment.repository.UserRepository;
import br.com.fiap.hospital.appointment.security.AuthenticatedUser;
import br.com.fiap.hospital.appointment.security.JwtService;
import br.com.fiap.hospital.sharedkernel.security.UserRole;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final long expirationMinutes;

    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService,
                                 @Value("${app.security.jwt.expiration-minutes}") long expirationMinutes) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.expirationMinutes = expirationMinutes;
    }

    public LoginResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByUsername(request.username())
                .filter(UserEntity::isActive)
                .orElseThrow(() -> new BusinessException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException("Invalid credentials");
        }

        UserRole role = UserRole.valueOf(user.getRole());
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user.getId(), user.getUsername(), role);
        String token = jwtService.generateToken(authenticatedUser);
        Instant expiresAt = Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES);
        return new LoginResponse(token, "Bearer", expiresAt, user.getId(), user.getRole());
    }
}
