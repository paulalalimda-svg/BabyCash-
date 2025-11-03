package com.babycash.backend.service;

import com.babycash.backend.dto.request.LoginRequest;
import com.babycash.backend.dto.request.RegisterRequest;
import com.babycash.backend.dto.response.AuthResponse;
import com.babycash.backend.exception.custom.AuthenticationException;
import com.babycash.backend.exception.custom.BusinessException;
import com.babycash.backend.model.entity.User;
import com.babycash.backend.model.enums.UserRole;
import com.babycash.backend.repository.UserRepository;
import com.babycash.backend.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService
 * Testing authentication and registration logic
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private AuditService auditService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User mockUser;
    private UserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        // Setup register request
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setPhone("+1234567890");

        // Setup login request
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        // Setup mock user
        mockUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("$2a$10$encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .phone("+1234567890")
                .role(UserRole.USER)
                .enabled(true)
                .emailVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Setup mock UserDetails
        mockUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("test@example.com")
                .password("$2a$10$encodedPassword")
                .authorities("ROLE_USER")
                .build();
    }

    @Test
    @DisplayName("Should register new user successfully")
    void testRegister_Success() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("mock.jwt.token");
        when(refreshTokenService.createRefreshToken(any(User.class)))
                .thenReturn(com.babycash.backend.model.entity.RefreshToken.builder()
                        .token("refresh-token-123")
                        .build());

        // When
        AuthResponse response = authService.register(registerRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getRole()).isEqualTo("USER");

        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken(any(UserDetails.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void testRegister_EmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("El email ya está registrado");

        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
        verify(jwtUtil, never()).generateToken(any(UserDetails.class));
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void testLogin_Success() {
        // Given
        Authentication mockAuthentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(userRepository.findByEmail(anyString())).thenReturn(java.util.Optional.of(mockUser));
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("mock.jwt.token");
        when(refreshTokenService.createRefreshToken(any(User.class)))
                .thenReturn(com.babycash.backend.model.entity.RefreshToken.builder()
                        .token("refresh-token-123")
                        .build());

        // When
        AuthResponse response = authService.login(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail("test@example.com");
        verify(userDetailsService).loadUserByUsername("test@example.com");
        verify(jwtUtil).generateToken(any(UserDetails.class));
    }

    @Test
    @DisplayName("Should throw exception with invalid credentials")
    void testLogin_InvalidCredentials() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Credenciales inválidas");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, never()).generateToken(any(UserDetails.class));
    }

    @Test
    @DisplayName("Should handle user not found after authentication")
    void testLogin_UserNotFoundAfterAuth() {
        // Given
        Authentication mockAuthentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(userRepository.findByEmail(anyString())).thenReturn(java.util.Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Usuario no encontrado");

        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should create user with USER role by default")
    void testRegister_DefaultRole() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertThat(savedUser.getRole()).isEqualTo(UserRole.USER);
            return mockUser;
        });
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("mock.jwt.token");
        when(refreshTokenService.createRefreshToken(any(User.class)))
                .thenReturn(com.babycash.backend.model.entity.RefreshToken.builder()
                        .token("refresh-token-123")
                        .build());

        // When
        authService.register(registerRequest);

        // Then
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should set enabled true by default on registration")
    void testRegister_EnabledByDefault() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertThat(savedUser.getEnabled()).isTrue();
            return mockUser;
        });
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("mock.jwt.token");
        when(refreshTokenService.createRefreshToken(any(User.class)))
                .thenReturn(com.babycash.backend.model.entity.RefreshToken.builder()
                        .token("refresh-token-123")
                        .build());

        // When
        authService.register(registerRequest);

        // Then
        verify(userRepository).save(any(User.class));
    }
}
