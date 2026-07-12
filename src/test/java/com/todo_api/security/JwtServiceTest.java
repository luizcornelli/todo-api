package com.todo_api.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

class JwtServiceTest {

    private static final String SECRET_KEY = "unit-test-secret-key-for-jwt-service-32bytes-minimum";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET_KEY, 3_600_000);
    }

    @Test
    void generateToken_thenExtractUsername_returnsSameSubject() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("luiz@example.com");

        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.extractUsername(token)).isEqualTo("luiz@example.com");
    }

    @Test
    void isTokenValid_withMatchingUserAndUnexpiredToken_returnsTrue() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("luiz@example.com");
        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void isTokenValid_withDifferentUser_returnsFalse() {
        UserDetails owner = mock(UserDetails.class);
        when(owner.getUsername()).thenReturn("luiz@example.com");
        String token = jwtService.generateToken(owner);

        UserDetails otherUser = mock(UserDetails.class);
        when(otherUser.getUsername()).thenReturn("other@example.com");

        assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
    }

    @Test
    void isTokenValid_withExpiredToken_returnsFalse() throws InterruptedException {
        JwtService shortLivedJwtService = new JwtService(SECRET_KEY, 1);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("luiz@example.com");
        String token = shortLivedJwtService.generateToken(userDetails);

        Thread.sleep(10);

        assertThat(shortLivedJwtService.isTokenValid(token, userDetails)).isFalse();
    }

    @Test
    void isTokenValid_withMalformedToken_returnsFalse() {
        UserDetails userDetails = mock(UserDetails.class);

        assertThat(jwtService.isTokenValid("not-a-valid-token", userDetails)).isFalse();
    }
}
