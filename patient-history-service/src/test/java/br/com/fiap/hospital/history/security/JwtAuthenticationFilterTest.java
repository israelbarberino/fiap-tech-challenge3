package br.com.fiap.hospital.history.security;

import br.com.fiap.hospital.sharedkernel.security.UserRole;
import jakarta.servlet.FilterChain;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    @Test
    void shouldReturnUnauthorizedWhenTokenIsInvalid() throws Exception {
        JwtService jwtService = Mockito.mock(JwtService.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        request.addHeader("Authorization", "Bearer invalid-token");
        doThrow(new RuntimeException("invalid token")).when(jwtService).parseToken("invalid-token");

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        verify(filterChain, Mockito.never()).doFilter(any(), any());
    }

    @Test
    void shouldAuthenticateValidToken() throws Exception {
        JwtService jwtService = Mockito.mock(JwtService.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        AuthenticatedUser user = new AuthenticatedUser(UUID.randomUUID(), "patient1", UserRole.ROLE_PATIENT);

        request.addHeader("Authorization", "Bearer valid-token");
        when(jwtService.parseToken("valid-token")).thenReturn(user);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(any(), any());
        assertThat(response.getStatus()).isEqualTo(200);
    }
}
