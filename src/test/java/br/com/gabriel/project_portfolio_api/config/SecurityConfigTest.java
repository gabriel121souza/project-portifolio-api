package br.com.gabriel.project_portfolio_api.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityConfigTest {

    @Test
    void shouldCreatePasswordEncoder() {
        PasswordEncoder passwordEncoder = new SecurityConfig().passwordEncoder();

        String encodedPassword = passwordEncoder.encode("portfolio123");

        assertTrue(passwordEncoder.matches("portfolio123", encodedPassword));
    }

    @Test
    void shouldCreateInMemoryUser() {
        SecurityConfig securityConfig = new SecurityConfig();
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        InMemoryUserDetailsManager users = securityConfig.users(passwordEncoder);
        UserDetails user = users.loadUserByUsername("portfolio_user");

        assertNotNull(user);
        assertTrue(passwordEncoder.matches("portfolio123", user.getPassword()));
        assertTrue(user.getAuthorities().stream().anyMatch(authority -> "ROLE_USER".equals(authority.getAuthority())));
    }
}
