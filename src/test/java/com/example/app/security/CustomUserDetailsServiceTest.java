package com.example.app.security;

import com.example.app.mapper.UserMapper;
import com.example.app.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$hashedpassword");
        testUser.setEmail("test@example.com");
    }

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        // Given
        when(userMapper.findByUsername("testuser")).thenReturn(testUser);

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("$2a$10$hashedpassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        verify(userMapper, times(1)).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_WhenUserDoesNotExist_ShouldThrowUsernameNotFoundException() {
        // Given
        when(userMapper.findByUsername("nonexistent")).thenReturn(null);

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent");
        });

        assertTrue(exception.getMessage().contains("nonexistent"));
        verify(userMapper, times(1)).findByUsername("nonexistent");
    }

    @Test
    void loadUserByUsername_ShouldHaveCorrectAuthorities() {
        // Given
        when(userMapper.findByUsername("testuser")).thenReturn(testUser);

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals("ROLE_USER", userDetails.getAuthorities().iterator().next().getAuthority());
    }
}

