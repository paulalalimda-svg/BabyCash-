package com.babycash.backend.service;

import com.babycash.backend.dto.contact.ContactMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Servicio para envío de emails
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.admin-email}")
    private String adminEmail;

    @Value("${app.mail.from-email}")
    private String fromEmail;

    @Value("${app.mail.from-name:Baby Cash}")
    private String fromName;

    /**
     * Envía email del formulario de contacto al administrador
     */
    @Async
    public void sendContactFormEmail(ContactMessageRequest request, String ipAddress) {
        try {
            log.info("Sending contact form email to admin: {}", adminEmail);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(adminEmail);
            helper.setReplyTo(request.getEmail());
            helper.setSubject("📧 Nuevo mensaje de contacto: " + request.getSubject());

            String htmlContent = buildContactEmailHtml(request, ipAddress);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Contact form email sent successfully to {}", adminEmail);

        } catch (Exception e) {
            log.error("Error sending contact form email: {}", e.getMessage(), e);
            throw new RuntimeException("Error al enviar el email", e);
        }
    }

    /**
     * Envía email de confirmación al usuario
     */
    @Async
    public void sendConfirmationEmail(String toEmail, String name) {
        try {
            log.info("Sending confirmation email to: {}", toEmail);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("✅ Mensaje recibido - Baby Cash");

            String htmlContent = buildConfirmationEmailHtml(name);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Confirmation email sent successfully to {}", toEmail);

        } catch (Exception e) {
            log.error("Error sending confirmation email: {}", e.getMessage(), e);
            // No lanzar excepción aquí para no afectar el flujo principal
        }
    }

    /**
     * Construye el HTML del email de contacto para el admin
     */
    private String buildContactEmailHtml(ContactMessageRequest request, String ipAddress) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #93C5FD 0%, #FBB6CE 100%);
                              color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .field { margin-bottom: 20px; }
                    .label { font-weight: bold; color: #555; margin-bottom: 5px; }
                    .value { padding: 10px; background: white; border-left: 3px solid #93C5FD; margin-top: 5px; }
                    .footer { text-align: center; margin-top: 30px; color: #777; font-size: 12px; }
                    .button { display: inline-block; padding: 12px 30px; background: #93C5FD;
                              color: white; text-decoration: none; border-radius: 5px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>📧 Nuevo Mensaje de Contacto</h1>
                        <p>Baby Cash - Sistema de Contacto</p>
                    </div>
                    <div class="content">
                        <div class="field">
                            <div class="label">👤 Nombre:</div>
                            <div class="value">%s</div>
                        </div>
                        <div class="field">
                            <div class="label">📧 Email:</div>
                            <div class="value"><a href="mailto:%s">%s</a></div>
                        </div>
                        %s
                        <div class="field">
                            <div class="label">📋 Asunto:</div>
                            <div class="value">%s</div>
                        </div>
                        <div class="field">
                            <div class="label">💬 Mensaje:</div>
                            <div class="value">%s</div>
                        </div>
                        <div class="field">
                            <div class="label">🌐 IP Address:</div>
                            <div class="value">%s</div>
                        </div>
                    </div>
                    <div class="footer">
                        <p>Este email fue generado automáticamente por el sistema de contacto de Baby Cash</p>
                        <p>&copy; 2025 Baby Cash. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                request.getName(),
                request.getEmail(),
                request.getEmail(),
                request.getPhone() != null ?
                    "<div class=\"field\"><div class=\"label\">📱 Teléfono:</div><div class=\"value\">" + request.getPhone() + "</div></div>" : "",
                request.getSubject(),
                request.getMessage().replace("\n", "<br>"),
                ipAddress != null ? ipAddress : "No disponible"
            );
    }

    /**
     * Construye el HTML del email de confirmación para el usuario
     */
    private String buildConfirmationEmailHtml(String name) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #93C5FD 0%, #FBB6CE 100%);
                              color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .footer { text-align: center; margin-top: 30px; color: #777; font-size: 12px; }
                    .checkmark { font-size: 48px; color: #10B981; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="checkmark">✅</div>
                        <h1>¡Mensaje Recibido!</h1>
                    </div>
                    <div class="content">
                        <p>Hola <strong>%s</strong>,</p>
                        <p>Hemos recibido tu mensaje y queremos agradecerte por contactarnos.</p>
                        <p>Nuestro equipo revisará tu consulta y te responderemos en un plazo máximo de <strong>24 horas</strong>.</p>
                        <p>Si tu consulta es urgente, puedes contactarnos directamente a través de:</p>
                        <ul>
                            <li>📱 WhatsApp: +57 321 929 7605</li>
                            <li>📧 Email: mazoanas09@gmail.com</li>
                        </ul>
                        <p>Gracias por confiar en <strong>Baby Cash</strong> 💙💖</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2025 Baby Cash. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(name);
    }

    /**
     * Envía email de recuperación de contraseña con código de 6 dígitos
     */
    @Async
    public void sendPasswordResetCodeEmail(String toEmail, String name, String resetCode) {
        try {
            log.info("Sending password reset code email to: {}", toEmail);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("🔐 Código de Recuperación - Baby Cash");

            String htmlContent = buildPasswordResetCodeEmailHtml(name, resetCode);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Password reset code email sent successfully to {}", toEmail);

        } catch (Exception e) {
            log.error("Error sending password reset code email: {}", e.getMessage(), e);
            throw new RuntimeException("Error al enviar el email de recuperación", e);
        }
    }

    /**
     * Envía email de recuperación de contraseña (método legacy con token largo)
     * @deprecated Use sendPasswordResetCodeEmail instead
     */
    @Deprecated
    @Async
    public void sendPasswordResetEmail(String toEmail, String name, String resetToken, String baseUrl) {
        try {
            log.info("Sending password reset email to: {}", toEmail);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("🔐 Recuperación de Contraseña - Baby Cash");

            String resetUrl = baseUrl + "/reset-password?token=" + resetToken;
            String htmlContent = buildPasswordResetEmailHtml(name, resetUrl);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Password reset email sent successfully to {}", toEmail);

        } catch (Exception e) {
            log.error("Error sending password reset email: {}", e.getMessage(), e);
            throw new RuntimeException("Error al enviar el email de recuperación", e);
        }
    }

    /**
     * Envía email de confirmación de cambio de contraseña
     */
    @Async
    public void sendPasswordChangedEmail(String toEmail, String name) {
        try {
            log.info("Sending password changed confirmation email to: {}", toEmail);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("✅ Contraseña Actualizada - Baby Cash");

            String htmlContent = buildPasswordChangedEmailHtml(name);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Password changed confirmation email sent successfully to {}", toEmail);

        } catch (Exception e) {
            log.error("Error sending password changed email: {}", e.getMessage(), e);
            // No lanzar excepción para no afectar el flujo
        }
    }

    /**
     * Envía email de bienvenida al registrarse
     */
    @Async
    public void sendWelcomeEmail(String toEmail, String name) {
        try {
            log.info("Sending welcome email to: {}", toEmail);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("🎉 ¡Bienvenido a Baby Cash!");

            String htmlContent = buildWelcomeEmailHtml(name);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Welcome email sent successfully to {}", toEmail);

        } catch (Exception e) {
            log.error("Error sending welcome email: {}", e.getMessage(), e);
            // No lanzar excepción para no afectar el flujo
        }
    }

    /**
     * Envía email de verificación de correo con código de 6 dígitos
     */
    @Async
    public void sendEmailVerificationCode(String toEmail, String name, String verificationCode) {
        try {
            log.info("Sending email verification code to: {}", toEmail);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("✉️ Verifica tu cuenta - Baby Cash");

            String htmlContent = buildEmailVerificationHtml(name, verificationCode);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Email verification code sent successfully to {}", toEmail);

        } catch (Exception e) {
            log.error("Error sending email verification code: {}", e.getMessage(), e);
            throw new RuntimeException("Error al enviar el código de verificación", e);
        }
    }

    /**
     * Envía email con código para eliminación de cuenta
     */
    @Async
    public void sendAccountDeletionCode(String toEmail, String name, String deletionCode) {
        try {
            log.info("Sending account deletion code to: {}", toEmail);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("⚠️ Confirmación de eliminación de cuenta - Baby Cash");

            String htmlContent = buildAccountDeletionHtml(name, deletionCode);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Account deletion code sent successfully to {}", toEmail);

        } catch (Exception e) {
            log.error("Error sending account deletion code: {}", e.getMessage(), e);
            throw new RuntimeException("Error al enviar el código de eliminación", e);
        }
    }

    /**
     * Envía email de confirmación de pedido
     */
    @Async
    public void sendOrderConfirmationEmail(String toEmail, String name, String orderNumber, String orderDetails, Double totalAmount) {
        try {
            log.info("Sending order confirmation email to: {}", toEmail);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("🎁 Confirmación de Pedido #" + orderNumber + " - Baby Cash");

            String htmlContent = buildOrderConfirmationEmailHtml(name, orderNumber, orderDetails, totalAmount);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Order confirmation email sent successfully to {}", toEmail);

        } catch (Exception e) {
            log.error("Error sending order confirmation email: {}", e.getMessage(), e);
            // No lanzar excepción para no afectar el flujo
        }
    }

    /**
     * Envía email de actualización de estado de pedido
     */
    @Async
    public void sendOrderStatusUpdateEmail(String toEmail, String name, String orderNumber, String newStatus) {
        try {
            log.info("Sending order status update email to: {}", toEmail);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("📦 Actualización de Pedido #" + orderNumber + " - Baby Cash");

            String htmlContent = buildOrderStatusUpdateEmailHtml(name, orderNumber, newStatus);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Order status update email sent successfully to {}", toEmail);

        } catch (Exception e) {
            log.error("Error sending order status update email: {}", e.getMessage(), e);
            // No lanzar excepción para no afectar el flujo
        }
    }

    // ==================== EMAIL TEMPLATES ====================

    /**
     * Template de email con código de 6 dígitos para recuperación
     */
    private String buildPasswordResetCodeEmailHtml(String name, String resetCode) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 0 auto; background: #ffffff; }
                    .header { background: linear-gradient(135deg, #93C5FD 0%%, #FBB6CE 100%%);
                              color: white; padding: 40px 30px; text-align: center; }
                    .header h1 { margin: 0; font-size: 28px; }
                    .content { padding: 40px 30px; background: #f8f9fa; }
                    .message { background: white; padding: 30px; border-radius: 10px; margin-bottom: 20px;
                               box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    .code-container { text-align: center; margin: 30px 0; }
                    .code { display: inline-block; font-size: 48px; font-weight: bold; letter-spacing: 8px;
                            color: #93C5FD; background: #EEF2FF; padding: 20px 40px; border-radius: 12px;
                            border: 3px dashed #93C5FD; font-family: 'Courier New', monospace; }
                    .warning { background: #FEF3C7; border-left: 4px solid #F59E0B; padding: 15px;
                               border-radius: 5px; margin: 20px 0; }
                    .info-box { background: #DBEAFE; border-left: 4px solid #3B82F6; padding: 15px;
                                border-radius: 5px; margin: 20px 0; }
                    .footer { background: #374151; color: #9CA3AF; padding: 30px; text-align: center; font-size: 13px; }
                    .footer a { color: #93C5FD; text-decoration: none; }
                    .icon { font-size: 48px; margin-bottom: 20px; }
                    .steps { margin: 20px 0; padding-left: 20px; }
                    .steps li { margin: 10px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="icon">🔐</div>
                        <h1>Código de Recuperación</h1>
                        <p>Baby Cash - Sistema de Seguridad</p>
                    </div>
                    <div class="content">
                        <div class="message">
                            <p>Hola <strong>%s</strong>,</p>
                            <p>Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en Baby Cash.</p>
                            <p>Usa el siguiente código de 6 dígitos para crear tu nueva contraseña:</p>

                            <div class="code-container">
                                <div class="code">%s</div>
                            </div>

                            <div class="info-box">
                                <strong>📝 Pasos para restablecer tu contraseña:</strong>
                                <ol class="steps">
                                    <li>Ingresa el código de 6 dígitos en la página de recuperación</li>
                                    <li>Crea tu nueva contraseña</li>
                                    <li>Confirma tu nueva contraseña</li>
                                    <li>¡Listo! Ya puedes iniciar sesión</li>
                                </ol>
                            </div>

                            <div class="warning">
                                <strong>⏰ Importante:</strong> Este código es válido por <strong>15 minutos</strong> solamente.
                            </div>

                            <div class="warning" style="background: #FEE2E2; border-left-color: #EF4444; margin-top: 20px;">
                                <strong>🚨 ¿No solicitaste este cambio?</strong><br>
                                Si no solicitaste restablecer tu contraseña, ignora este correo.
                                Tu cuenta está segura y no se realizarán cambios.
                                <br><br>
                                Por seguridad, te recomendamos:
                                <ul style="margin: 10px 0;">
                                    <li>Cambiar tu contraseña inmediatamente</li>
                                    <li>Revisar la actividad reciente de tu cuenta</li>
                                    <li>Contactarnos si sospechas de acceso no autorizado</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    <div class="footer">
                        <p>Este correo fue enviado desde Baby Cash</p>
                        <p>📧 <a href="mailto:mazoanas09@gmail.com">mazoanas09@gmail.com</a> |
                           📱 <a href="tel:+573219297605">+57 321 929 7605</a></p>
                        <p>&copy; 2025 Baby Cash. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(name, resetCode);
    }

    /**
     * Template de email de recuperación de contraseña (legacy con URL)
     */
    private String buildPasswordResetEmailHtml(String name, String resetUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 0 auto; background: #ffffff; }
                    .header { background: linear-gradient(135deg, #93C5FD 0%%, #FBB6CE 100%%);
                              color: white; padding: 40px 30px; text-align: center; }
                    .header h1 { margin: 0; font-size: 28px; }
                    .content { padding: 40px 30px; background: #f8f9fa; }
                    .message { background: white; padding: 30px; border-radius: 10px; margin-bottom: 20px;
                               box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    .button-container { text-align: center; margin: 30px 0; }
                    .button { display: inline-block; padding: 15px 40px; background: #93C5FD;
                              color: white !important; text-decoration: none; border-radius: 8px;
                              font-weight: bold; font-size: 16px; transition: background 0.3s; }
                    .button:hover { background: #7DB4F8; }
                    .warning { background: #FEF3C7; border-left: 4px solid #F59E0B; padding: 15px;
                               border-radius: 5px; margin: 20px 0; }
                    .footer { background: #374151; color: #9CA3AF; padding: 30px; text-align: center; font-size: 13px; }
                    .footer a { color: #93C5FD; text-decoration: none; }
                    .icon { font-size: 48px; margin-bottom: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="icon">🔐</div>
                        <h1>Recuperación de Contraseña</h1>
                        <p>Baby Cash - Sistema de Seguridad</p>
                    </div>
                    <div class="content">
                        <div class="message">
                            <p>Hola <strong>%s</strong>,</p>
                            <p>Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en Baby Cash.</p>
                            <p>Para crear una nueva contraseña, haz clic en el botón de abajo:</p>

                            <div class="button-container">
                                <a href="%s" class="button">Restablecer Contraseña</a>
                            </div>

                            <div class="warning">
                                <strong>⏰ Importante:</strong> Este enlace es válido por <strong>1 hora</strong> solamente.
                            </div>

                            <p>Si el botón no funciona, copia y pega el siguiente enlace en tu navegador:</p>
                            <p style="word-break: break-all; color: #6B7280; font-size: 12px;">%s</p>

                            <div class="warning" style="background: #FEE2E2; border-left-color: #EF4444; margin-top: 30px;">
                                <strong>🚨 ¿No solicitaste este cambio?</strong><br>
                                Si no solicitaste restablecer tu contraseña, ignora este correo.
                                Tu cuenta está segura y no se realizarán cambios.
                            </div>
                        </div>
                    </div>
                    <div class="footer">
                        <p>Este correo fue enviado desde Baby Cash</p>
                        <p>📧 <a href="mailto:mazoanas09@gmail.com">mazoanas09@gmail.com</a> |
                           📱 <a href="tel:+573219297605">+57 321 929 7605</a></p>
                        <p>&copy; 2025 Baby Cash. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(name, resetUrl, resetUrl);
    }

    /**
     * Template de email de confirmación de cambio de contraseña
     */
    private String buildPasswordChangedEmailHtml(String name) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 0 auto; background: #ffffff; }
                    .header { background: linear-gradient(135deg, #10B981 0%%, #059669 100%%);
                              color: white; padding: 40px 30px; text-align: center; }
                    .content { padding: 40px 30px; background: #f8f9fa; }
                    .message { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    .warning { background: #FEE2E2; border-left: 4px solid #EF4444; padding: 15px;
                               border-radius: 5px; margin: 20px 0; }
                    .footer { background: #374151; color: #9CA3AF; padding: 30px; text-align: center; font-size: 13px; }
                    .icon { font-size: 64px; margin-bottom: 10px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="icon">✅</div>
                        <h1>Contraseña Actualizada</h1>
                    </div>
                    <div class="content">
                        <div class="message">
                            <p>Hola <strong>%s</strong>,</p>
                            <p>Te confirmamos que tu contraseña ha sido actualizada exitosamente.</p>
                            <p>Ya puedes iniciar sesión en Baby Cash con tu nueva contraseña.</p>

                            <div class="warning">
                                <strong>🚨 ¿No realizaste este cambio?</strong><br>
                                Si no fuiste tú quien cambió la contraseña, contáctanos inmediatamente:
                                <br><strong>📧 mazoanas09@gmail.com</strong>
                                <br><strong>📱 +57 321 929 7605</strong>
                            </div>
                        </div>
                    </div>
                    <div class="footer">
                        <p>&copy; 2025 Baby Cash. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(name);
    }

    /**
     * Template de email de bienvenida
     */
    private String buildWelcomeEmailHtml(String name) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 0 auto; background: #ffffff; }
                    .header { background: linear-gradient(135deg, #93C5FD 0%%, #FBB6CE 100%%);
                              color: white; padding: 40px 30px; text-align: center; }
                    .content { padding: 40px 30px; }
                    .message { background: #f8f9fa; padding: 30px; border-radius: 10px; margin-bottom: 20px; }
                    .benefits { display: grid; gap: 15px; margin: 30px 0; }
                    .benefit { background: white; padding: 20px; border-radius: 8px; border-left: 4px solid #93C5FD;
                               box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
                    .footer { background: #374151; color: #9CA3AF; padding: 30px; text-align: center; font-size: 13px; }
                    .icon { font-size: 64px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="icon">🎉</div>
                        <h1>¡Bienvenido a Baby Cash!</h1>
                    </div>
                    <div class="content">
                        <div class="message">
                            <p>Hola <strong>%s</strong>,</p>
                            <p>¡Qué emoción tenerte con nosotros! Tu cuenta ha sido creada exitosamente.</p>
                            <p>En Baby Cash encontrarás todo lo que necesitas para tu bebé con la mejor calidad y a los mejores precios.</p>
                        </div>

                        <div class="benefits">
                            <div class="benefit">
                                <strong>🚚 Envío Rápido</strong>
                                <p style="margin: 5px 0 0 0; color: #666;">Recibe tus productos en tiempo récord</p>
                            </div>
                            <div class="benefit">
                                <strong>💳 Pago Seguro</strong>
                                <p style="margin: 5px 0 0 0; color: #666;">Múltiples métodos de pago disponibles</p>
                            </div>
                            <div class="benefit">
                                <strong>🎁 Ofertas Exclusivas</strong>
                                <p style="margin: 5px 0 0 0; color: #666;">Descuentos especiales para miembros</p>
                            </div>
                        </div>

                        <p style="text-align: center; margin-top: 30px;">
                            <a href="http://localhost:5173/productos"
                               style="display: inline-block; padding: 15px 40px; background: #93C5FD;
                                      color: white; text-decoration: none; border-radius: 8px; font-weight: bold;">
                                Explorar Productos
                            </a>
                        </p>
                    </div>
                    <div class="footer">
                        <p>📧 mazoanas09@gmail.com | 📱 +57 321 929 7605</p>
                        <p>&copy; 2025 Baby Cash. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(name);
    }

    /**
     * Template de email de confirmación de pedido
     */
    private String buildOrderConfirmationEmailHtml(String name, String orderNumber, String orderDetails, Double totalAmount) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 0 auto; background: #ffffff; }
                    .header { background: linear-gradient(135deg, #10B981 0%%, #059669 100%%);
                              color: white; padding: 40px 30px; text-align: center; }
                    .content { padding: 40px 30px; background: #f8f9fa; }
                    .message { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    .order-info { background: #EEF2FF; padding: 20px; border-radius: 8px; margin: 20px 0; }
                    .total { font-size: 24px; color: #10B981; font-weight: bold; text-align: right; margin-top: 20px; }
                    .footer { background: #374151; color: #9CA3AF; padding: 30px; text-align: center; font-size: 13px; }
                    .icon { font-size: 64px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="icon">🎁</div>
                        <h1>¡Pedido Confirmado!</h1>
                        <p>Orden #%s</p>
                    </div>
                    <div class="content">
                        <div class="message">
                            <p>Hola <strong>%s</strong>,</p>
                            <p>¡Gracias por tu compra en Baby Cash! Tu pedido ha sido recibido y está siendo procesado.</p>

                            <div class="order-info">
                                <strong>📦 Detalles del Pedido:</strong>
                                %s
                                <div class="total">Total: $%,.0f COP</div>
                            </div>

                            <p>Te enviaremos actualizaciones sobre el estado de tu pedido.</p>
                            <p>Si tienes alguna pregunta, no dudes en contactarnos.</p>
                        </div>
                    </div>
                    <div class="footer">
                        <p>📧 mazoanas09@gmail.com | 📱 +57 321 929 7605</p>
                        <p>&copy; 2025 Baby Cash. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(orderNumber, name, orderDetails, totalAmount);
    }

    /**
     * Template de email de actualización de estado de pedido
     */
    private String buildOrderStatusUpdateEmailHtml(String name, String orderNumber, String newStatus) {
        String statusEmoji = switch (newStatus) {
            case "PROCESSING" -> "🔄";
            case "SHIPPED" -> "🚚";
            case "DELIVERED" -> "✅";
            case "CANCELLED" -> "❌";
            default -> "📦";
        };

        String statusText = switch (newStatus) {
            case "PROCESSING" -> "Tu pedido está siendo procesado";
            case "SHIPPED" -> "Tu pedido ha sido enviado";
            case "DELIVERED" -> "Tu pedido ha sido entregado";
            case "CANCELLED" -> "Tu pedido ha sido cancelado";
            default -> "Estado actualizado";
        };

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 0 auto; background: #ffffff; }
                    .header { background: linear-gradient(135deg, #93C5FD 0%%, #FBB6CE 100%%);
                              color: white; padding: 40px 30px; text-align: center; }
                    .content { padding: 40px 30px; background: #f8f9fa; }
                    .message { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    .status { background: #EEF2FF; padding: 20px; border-radius: 8px; margin: 20px 0; text-align: center; }
                    .footer { background: #374151; color: #9CA3AF; padding: 30px; text-align: center; font-size: 13px; }
                    .icon { font-size: 64px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="icon">%s</div>
                        <h1>Actualización de Pedido</h1>
                        <p>Orden #%s</p>
                    </div>
                    <div class="content">
                        <div class="message">
                            <p>Hola <strong>%s</strong>,</p>
                            <div class="status">
                                <h2 style="margin: 0; color: #93C5FD;">%s</h2>
                            </div>
                            <p>Puedes revisar el estado completo de tu pedido en tu perfil.</p>
                        </div>
                    </div>
                    <div class="footer">
                        <p>&copy; 2025 Baby Cash. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(statusEmoji, orderNumber, name, statusText);
    }

    /**
     * Template de email de verificación de cuenta
     */
    private String buildEmailVerificationHtml(String name, String verificationCode) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 0 auto; background: #ffffff; }
                    .header { background: linear-gradient(135deg, #93C5FD 0%%, #FBB6CE 100%%);
                              color: white; padding: 40px 30px; text-align: center; }
                    .header h1 { margin: 0; font-size: 28px; }
                    .content { padding: 40px 30px; background: #f8f9fa; }
                    .message { background: white; padding: 30px; border-radius: 10px; margin-bottom: 20px;
                               box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    .code-container { text-align: center; margin: 30px 0; }
                    .code { display: inline-block; font-size: 48px; font-weight: bold; letter-spacing: 8px;
                            color: #10B981; background: #D1FAE5; padding: 20px 40px; border-radius: 12px;
                            border: 3px dashed #10B981; font-family: 'Courier New', monospace; }
                    .info-box { background: #DBEAFE; border-left: 4px solid #3B82F6; padding: 15px;
                                border-radius: 5px; margin: 20px 0; }
                    .footer { background: #374151; color: #9CA3AF; padding: 30px; text-align: center; font-size: 13px; }
                    .footer a { color: #93C5FD; text-decoration: none; }
                    .icon { font-size: 48px; margin-bottom: 20px; }
                    .steps { margin: 20px 0; padding-left: 20px; }
                    .steps li { margin: 10px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="icon">✉️</div>
                        <h1>Verifica tu Cuenta</h1>
                        <p>Baby Cash - Bienvenido a la familia</p>
                    </div>
                    <div class="content">
                        <div class="message">
                            <p>¡Hola <strong>%s</strong>!</p>
                            <p>¡Gracias por registrarte en Baby Cash! Para completar tu registro y asegurar tu cuenta, por favor verifica tu correo electrónico.</p>
                            <p>Usa el siguiente código de 6 dígitos para verificar tu cuenta:</p>

                            <div class="code-container">
                                <div class="code">%s</div>
                            </div>

                            <div class="info-box">
                                <strong>📝 ¿Cómo verificar tu cuenta?</strong>
                                <ol class="steps">
                                    <li>Inicia sesión en Baby Cash</li>
                                    <li>Ve a tu perfil</li>
                                    <li>Ingresa el código de 6 dígitos</li>
                                    <li>¡Listo! Tu cuenta estará verificada</li>
                                </ol>
                            </div>

                            <p style="color: #6B7280; font-size: 14px; margin-top: 20px;">
                                Si no creaste una cuenta en Baby Cash, puedes ignorar este correo de forma segura.
                            </p>
                        </div>
                    </div>
                    <div class="footer">
                        <p>Este correo fue enviado desde Baby Cash</p>
                        <p>📧 <a href="mailto:mazoanas09@gmail.com">mazoanas09@gmail.com</a> |
                           📱 <a href="tel:+573219297605">+57 321 929 7605</a></p>
                        <p>&copy; 2025 Baby Cash. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(name, verificationCode);
    }

    /**
     * Template de email de confirmación de eliminación de cuenta
     */
    private String buildAccountDeletionHtml(String name, String deletionCode) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 0 auto; background: #ffffff; }
                    .header { background: linear-gradient(135deg, #EF4444 0%%, #DC2626 100%%);
                              color: white; padding: 40px 30px; text-align: center; }
                    .header h1 { margin: 0; font-size: 28px; }
                    .content { padding: 40px 30px; background: #f8f9fa; }
                    .message { background: white; padding: 30px; border-radius: 10px; margin-bottom: 20px;
                               box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    .code-container { text-align: center; margin: 30px 0; }
                    .code { display: inline-block; font-size: 48px; font-weight: bold; letter-spacing: 8px;
                            color: #EF4444; background: #FEE2E2; padding: 20px 40px; border-radius: 12px;
                            border: 3px dashed #EF4444; font-family: 'Courier New', monospace; }
                    .warning-box { background: #FEF3C7; border-left: 4px solid #F59E0B; padding: 15px;
                                   border-radius: 5px; margin: 20px 0; }
                    .footer { background: #374151; color: #9CA3AF; padding: 30px; text-align: center; font-size: 13px; }
                    .footer a { color: #93C5FD; text-decoration: none; }
                    .icon { font-size: 48px; margin-bottom: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="icon">⚠️</div>
                        <h1>Confirmación de Eliminación</h1>
                        <p>Baby Cash - Solicitud de eliminación de cuenta</p>
                    </div>
                    <div class="content">
                        <div class="message">
                            <p>Hola <strong>%s</strong>,</p>
                            <p>Hemos recibido una solicitud para <strong>eliminar tu cuenta de Baby Cash</strong>.</p>

                            <div class="warning-box">
                                <strong>⚠️ ADVERTENCIA</strong>
                                <p style="margin: 10px 0 0 0;">Esta acción es <strong>permanente e irreversible</strong>. Se eliminarán todos tus datos, pedidos, puntos de fidelidad y historial.</p>
                            </div>

                            <p>Si realmente deseas continuar, usa el siguiente código:</p>

                            <div class="code-container">
                                <div class="code">%s</div>
                            </div>

                            <p style="text-align: center; color: #6B7280; font-size: 14px;">
                                Este código expirará en <strong>15 minutos</strong>
                            </p>

                            <p style="color: #6B7280; font-size: 14px; margin-top: 30px;">
                                Si NO solicitaste eliminar tu cuenta, ignora este correo y tu cuenta permanecerá segura.
                                Te recomendamos cambiar tu contraseña inmediatamente.
                            </p>
                        </div>
                    </div>
                    <div class="footer">
                        <p>Este correo fue enviado desde Baby Cash</p>
                        <p>📧 <a href="mailto:mazoanas09@gmail.com">mazoanas09@gmail.com</a> |
                           📱 <a href="tel:+573219297605">+57 321 929 7605</a></p>
                        <p>&copy; 2025 Baby Cash. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(name, deletionCode);
    }

    /**
     * Envía un email simple (sin HTML)
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("Simple email sent to: {}", to);
        } catch (Exception e) {
            log.error("Error sending simple email: {}", e.getMessage(), e);
            throw new RuntimeException("Error al enviar el email", e);
        }
    }
}
