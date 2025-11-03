package com.babycash.backend.controller;

import com.babycash.backend.dto.auth.ForgotPasswordRequest;
import com.babycash.backend.dto.auth.ResetPasswordRequest;
import com.babycash.backend.dto.request.LoginRequest;
import com.babycash.backend.dto.request.RefreshTokenRequest;
import com.babycash.backend.dto.request.RegisterRequest;
import com.babycash.backend.dto.response.AuthResponse;
import com.babycash.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador de autenticación para registro e inicio de sesión de usuarios
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "🔐 Authentication", description = "Endpoints para autenticación de usuarios con refresh token")
public class AuthController {

    private final AuthService authService;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @PostMapping("/register")
    @Operation(
            summary = "Registrar nuevo usuario",
            description = """
                    Registra un nuevo usuario en el sistema con validaciones completas:
                    - Email único (no duplicado)
                    - Contraseña segura (mínimo 8 caracteres, 1 letra mayúscula, 1 número, 1 carácter especial)
                    - Nombre y apellido obligatorios
                    - Dirección de envío válida
                    
                    Retorna un token JWT válido por 24 horas para autenticación inmediata.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Usuario registrado exitosamente con token JWT"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "❌ Email ya registrado o datos inválidos"
            )
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Iniciar sesión",
            description = """
                    Autentica un usuario con email y contraseña.
                    
                    Retorna:
                    - Access Token (JWT válido por 24 horas)
                    - Refresh Token (válido por 7 días)
                    - Información del usuario (id, email, nombre, rol)
                    
                    El Access Token debe incluirse en el header Authorization como: Bearer {token}
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Login exitoso con tokens JWT"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "❌ Credenciales inválidas"
            )
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refrescar access token",
            description = """
                    Genera un nuevo Access Token usando un Refresh Token válido.
                    
                    Útil cuando el Access Token expira (24 horas) pero el Refresh Token sigue válido (7 días).
                    No requiere volver a pedir credenciales al usuario.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Nuevo access token generado"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "❌ Refresh token inválido o expirado"
            )
    })
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Cerrar sesión",
            description = """
                    Invalida el Refresh Token actual para prevenir uso futuro.
                    El Access Token seguirá siendo válido hasta su expiración natural.
                    
                    Buena práctica de seguridad al cerrar sesión.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Logout exitoso - Refresh token invalidado"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "❌ Refresh token no encontrado"
            )
    })
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Solicitar recuperación de contraseña",
            description = """
                    Genera un token de recuperación y envía un email al usuario con un enlace para restablecer su contraseña.
                    
                    El token es válido por 1 hora. Si el email no existe, no se envía ningún email por seguridad.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Email de recuperación enviado (si el usuario existe)"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "❌ Datos inválidos"
            )
    })
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail(), frontendUrl);
        return ResponseEntity.ok(Map.of(
            "message", "Si existe una cuenta con ese email, recibirás un enlace de recuperación"
        ));
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Restablecer contraseña con token",
            description = """
                    Restablece la contraseña del usuario usando el token recibido por email.
                    
                    La nueva contraseña debe cumplir con los requisitos de seguridad:
                    - Mínimo 8 caracteres
                    - Al menos 1 letra mayúscula
                    - Al menos 1 letra minúscula
                    - Al menos 1 número
                    
                    El token se invalida después de usarse.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Contraseña actualizada exitosamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "❌ Token inválido, expirado o contraseñas no coinciden"
            )
    })
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Las contraseñas no coinciden"
            ));
        }

        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of(
            "message", "Contraseña actualizada exitosamente"
        ));
    }

    @GetMapping("/validate-reset-token/{token}")
    @Operation(
            summary = "Validar token de recuperación",
            description = """
                    Verifica si un token de recuperación de contraseña es válido y no ha expirado.
                    
                    Útil para mostrar el formulario de nueva contraseña solo si el token es válido.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Token válido"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "❌ Token inválido o expirado"
            )
    })
    public ResponseEntity<Map<String, Boolean>> validateResetToken(@PathVariable String token) {
        try {
            authService.validateResetToken(token);
            return ResponseEntity.ok(Map.of("valid", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("valid", false));
        }
    }
}
