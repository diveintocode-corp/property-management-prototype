package com.example.app.controller;

import com.example.app.model.User;
import com.example.app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("hashedpassword");
        testUser.setEmail("test@example.com");
        
        // Clear security context
        SecurityContextHolder.clearContext();
    }

    @Test
    void loginForm_ShouldReturnLoginPage() throws Exception {
        // When & Then
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    void loginForm_WithErrorParameter_ShouldShowError() throws Exception {
        // When & Then
        mockMvc.perform(get("/login").param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attribute("error", "ユーザー名またはパスワードが正しくありません"));
    }

    @Test
    void loginForm_WithLogoutParameter_ShouldShowMessage() throws Exception {
        // When & Then
        mockMvc.perform(get("/login").param("logout", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attribute("message", "ログアウトしました"));
    }

    @Test
    void registerForm_ShouldReturnRegisterPage() throws Exception {
        // When & Then
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void register_WhenValidUser_ShouldRegisterAndRedirect() throws Exception {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");
        newUser.setEmail("newuser@example.com");

        User registeredUser = new User();
        registeredUser.setId(1L);
        registeredUser.setUsername("newuser");
        registeredUser.setPassword("hashedpassword");

        Authentication mockAuthentication = new UsernamePasswordAuthenticationToken(
                "newuser",
                "password123",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        doNothing().when(userService).register(any(User.class));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);

        // When & Then
        mockMvc.perform(post("/register")
                .param("username", "newuser")
                .param("password", "password123")
                .param("email", "newuser@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/properties"))
                .andExpect(flash().attribute("message", "ユーザー登録が完了し、ログインしました。"));

        verify(userService, times(1)).register(any(User.class));
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void register_WhenValidationFails_ShouldReturnForm() throws Exception {
        // When & Then - empty username should fail validation
        mockMvc.perform(post("/register")
                .param("username", "")
                .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));

        verify(userService, never()).register(any(User.class));
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void register_WhenUsernameAlreadyExists_ShouldReturnFormWithError() throws Exception {
        // Given
        doThrow(new IllegalStateException("ユーザー名は既に使用されています"))
                .when(userService).register(any(User.class));

        // When & Then
        mockMvc.perform(post("/register")
                .param("username", "existinguser")
                .param("password", "password123")
                .param("email", "existing@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));

        verify(userService, times(1)).register(any(User.class));
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void register_WhenAuthenticationFails_ShouldReturnFormWithError() throws Exception {
        // Given
        doNothing().when(userService).register(any(User.class));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        // When & Then
        mockMvc.perform(post("/register")
                .param("username", "newuser")
                .param("password", "password123")
                .param("email", "newuser@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));

        verify(userService, times(1)).register(any(User.class));
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}

