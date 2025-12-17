# Servicio de envío de emails

Este documento describe cómo funciona el envío de correos en el proyecto BabyCash: flujo, componentes clave, configuración, pruebas y recomendaciones.

## Resumen

- Lenguaje / runtime: Java (Spring Boot)
- Componente principal: `EmailService` (backend)
- Integración en flujos: `AuthService` (registro, recuperación de contraseña), controladores de notificaciones y plantillas de email
- Protocolos: SMTP (TLS/STARTTLS), posibilidad de usar proveedores externos (SendGrid, Mailgun, SES, Mailtrap para pruebas)
- Plantillas: HTML/texto (templates en recursos o generadas en el servicio)

## Componentes y archivos relevantes

Revisa estos archivos en el backend para ver la implementación actual:

- `backend/src/main/java/com/babycash/backend/service/EmailService.java` — servicio que envía correos (envoltorio sobre JavaMail/JakartaMail u otra biblioteca).
- `backend/src/main/java/com/babycash/backend/service/AuthService.java` — utiliza `EmailService` para enviar correos de verificación y recuperación de contraseña (código de 6 dígitos o token según la implementación).
- `backend/src/main/java/com/babycash/backend/dto/auth/ResetPasswordRequest.java` — DTO usado en endpoints de reset.
- Controladores relevantes: `AuthController.java` (endpoints `/api/auth/forgot-password`, `/api/auth/reset-password`, etc.)

> Nota: los nombres anteriores reflejan la estructura del repositorio; revisa las rutas si hay refactorizaciones.

## Flujo típico: "Olvidé mi contraseña" (forgot password)

1. Cliente (frontend) envía POST a `/api/auth/forgot-password` con `{ "email": "usuario@dominio" }`.
2. `AuthController` delega a `AuthService.forgotPassword(email, baseUrl)`.
3. `AuthService` genera un identificador de recuperación (en este proyecto: un *código de 6 dígitos* y una expiración de ~15 minutos) y lo guarda en la entidad `User` (`resetPasswordCode`, `resetPasswordExpiry` o campos equivalentes).
4. `AuthService` construye el contenido del email (plantilla HTML/texto) y pide a `EmailService` que envíe el mensaje al usuario.
5. `EmailService` usa la configuración SMTP para abrir conexión con el proveedor y enviar el correo.
6. El usuario recibe el código por email y lo introduce en el frontend; el frontend llama al endpoint `/api/auth/reset-password` con `{ code, newPassword }`.
7. `AuthService.validateResetCode(code)` valida el código y la expiración; si es válido, actualiza la contraseña (`passwordEncoder.encode(newPassword)`) y borra el código/expiración.

## Configuración (application.properties / application.yml)

Asegúrate de definir las propiedades de correo en `src/main/resources/application-*.properties` o mediante variables de entorno en producción.
Ejemplo (application.properties):

```
# SMTP
spring.mail.host=smtp.example.com
spring.mail.port=587
spring.mail.username=${SMTP_USER}
spring.mail.password=${SMTP_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.default-encoding=UTF-8

# Remitente por defecto
app.mail.from=no-reply@babycash.com
```

Recomendación: guardar `SMTP_USER` y `SMTP_PASSWORD` en variables de entorno (o en secretos del entorno/CI), no en el repositorio.

### Para pruebas locales
- Usa Mailtrap o Ethereal (nodemailer/ethereal) para interceptar correos sin enviarlos a usuarios reales.
- Puedes configurar `spring.mail.host=smtp.mailtrap.io` y las credenciales que Mailtrap te provea.

## Ejemplo de plantilla (reset de contraseña)

Asunto: "BabyCash — Código para restablecer contraseña"

Cuerpo (HTML simplificado):

```
<h1>Restablecer contraseña</h1>
<p>Hola {firstName},</p>
<p>Usa este código para restablecer tu contraseña: <strong>{CODE}</strong></p>
<p>El código expirará en 15 minutos.</p>
<p>Si no solicitaste este cambio, ignora este correo.</p>
```

El `EmailService` puede usar plantillas Thymeleaf o generar HTML dinámicamente desde el servicio.

## Manejo de errores y reintentos

- `EmailService` debe capturar excepciones de envío (p. ej. `MessagingException`) y:
  - Loggear el error (sin exponer credenciales ni el código en logs de acceso público)
  - Reintentar en caliente 1-2 veces con backoff exponencial, o delegar a una cola/worker
- Para mayor robustez, considera usar una cola (RabbitMQ, Redis Queue, SQS) y un worker asíncrono para enviar correos fuera del hilo HTTP

## Seguridad y buenas prácticas

- Nunca loguear códigos/contraseñas en texto plano.
- Limitar intentos de generación/solicitud de códigos para evitar spam y abuso (rate limiter por IP o por cuenta).
- El código de recuperación debe expirar (p. ej. 15 minutos) y usarse solo una vez.
- Forzar el hashing de contraseñas con `BCryptPasswordEncoder` (ya utilizado en el proyecto).
- Proteger endpoints sensibles con reCAPTCHA o límites de petición si es necesario.

## Cómo probar manualmente (local)

1. Asegúrate de que la app use un SMTP de pruebas (Mailtrap) o tu SMTP real de pruebas.
2. Reinicia la app para cargar la configuración.
3. Ejecuta un `curl` desde la terminal para solicitar un reset:

```bash
curl -i -X POST http://localhost:8080/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@babycash.com"}'
```

4. Revisa el inbox en Mailtrap / proveedor de pruebas. Deberías ver el correo con el código.
5. Usar el endpoint de verificación/reset:

```bash
curl -i -X POST http://localhost:8080/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{"code":"123456","password":"NuevaContra123!"}'
```

6. Observa los logs del backend para ver mensajes relacionados con envío y validación.

## Logs y Troubleshooting

- Logs clave (backend): revisa `EmailService` y `AuthService` para entradas de log:
  - "Enviando email a ..."
  - Errores del tipo `MessagingException` o problemas de autenticación SMTP
- Si el correo no llega:
  - Revisar configuración SMTP (host/port/user/pass)
  - Revisar bloqueo por proveedor (puede requerir TLS/STARTTLS o puerto distinto)
  - Comprobar que no haya firewalls que bloqueen la salida SMTP
- Si recibes errores tipo `Invalid login` del servidor SMTP, confirma credenciales y que la cuenta tenga permisos de envío

## Recomendaciones / mejoras futuras

- Cambiar a un servicio de correo transaccional (SendGrid, SES, Mailgun) para mejor deliverability y métricas
- Añadir cola asíncrona para envío de correos (mejora de performance y resiliencia)
- Añadir plantillas en archivos separados y motor de templates (Thymeleaf/FreeMarker) para separar lógica de presentación
- Añadir tests de integración para el flujo "forgot password" usando un SMTP fake o un servidor de pruebas

---

## Resumen rápido (cómo funciona hoy)

- El frontend llama a endpoints de auth.
- `AuthService` genera código/tokens y usa `EmailService` para enviar el correo.
- El usuario recibe el correo y completa el flujo en el frontend.


---

Si quieres, puedo:
- Añadir ejemplos concretos de código (fragmentos de `EmailService.java` o plantilla) dentro de este documento.
- Implementar un worker asíncrono y ejemplo de configuración para Mailtrap/SendGrid.
- Añadir una sección con capturas de pantalla o pasos guiados para configurar Mailtrap y probar localmente.

Dime cuál de esas mejoras prefieres y lo añado al documento o lo implemento en el código.
