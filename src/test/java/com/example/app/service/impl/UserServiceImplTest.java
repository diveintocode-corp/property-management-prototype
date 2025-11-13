package com.example.app.service.impl;

import com.example.app.mapper.UserMapper;
import com.example.app.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("plainpassword");
        testUser.setEmail("test@example.com");
    }

    @Test
    void register_WhenUsernameDoesNotExist_ShouldHashPasswordAndInsert() {
        // Given
        when(userMapper.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("plainpassword")).thenReturn("hashedpassword");
        doNothing().when(userMapper).insert(any(User.class));

        // When
        userService.register(testUser);

        // Then
        verify(userMapper, times(1)).existsByUsername("testuser");
        verify(passwordEncoder, times(1)).encode("plainpassword");
        verify(userMapper, times(1)).insert(testUser);
        assertEquals("hashedpassword", testUser.getPassword());
    }

    @Test
    void register_WhenUsernameAlreadyExists_ShouldThrowException() {
        // Given
        when(userMapper.existsByUsername("testuser")).thenReturn(true);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            userService.register(testUser);
        });

        assertEquals("ユーザー名は既に使用されています", exception.getMessage());
        verify(userMapper, times(1)).existsByUsername("testuser");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        // Given
        when(userMapper.findByUsername("testuser")).thenReturn(testUser);

        // When
        User result = userService.findByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(userMapper, times(1)).findByUsername("testuser");
    }

    @Test
    void findByUsername_WhenUserDoesNotExist_ShouldReturnNull() {
        // Given
        when(userMapper.findByUsername("nonexistent")).thenReturn(null);

        // When
        User result = userService.findByUsername("nonexistent");

        // Then
        assertNull(result);
        verify(userMapper, times(1)).findByUsername("nonexistent");
    }
}

