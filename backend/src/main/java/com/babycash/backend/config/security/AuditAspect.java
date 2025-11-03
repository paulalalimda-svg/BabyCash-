package com.babycash.backend.config.security;

import com.babycash.backend.entity.AuditLog;
import com.babycash.backend.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Aspecto AOP para auditoría automática de operaciones críticas
 * 
 * Intercepta métodos en servicios y registra automáticamente:
 * - Creación/cancelación de órdenes
 * - Procesamiento de pagos
 * - Operaciones de admin
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;

    /**
     * Audita creación de órdenes
     */
    @AfterReturning(
        pointcut = "execution(* com.babycash.backend.service.OrderService.createOrder(..))",
        returning = "result"
    )
    public void auditOrderCreation(JoinPoint joinPoint, Object result) {
        try {
            if (result != null) {
                auditService.logAction(
                    AuditLog.ActionType.ORDER_CREATED,
                    "Order",
                    extractEntityId(result),
                    "Nueva orden creada"
                );
            }
        } catch (Exception e) {
            log.error("Error auditing order creation", e);
        }
    }

    /**
     * Audita cancelación de órdenes
     */
    @AfterReturning(
        pointcut = "execution(* com.babycash.backend.service.OrderService.cancelOrder(..))"
    )
    public void auditOrderCancellation(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] instanceof Long orderId) {
                auditService.logAction(
                    AuditLog.ActionType.ORDER_CANCELLED,
                    "Order",
                    orderId,
                    "Orden cancelada"
                );
            }
        } catch (Exception e) {
            log.error("Error auditing order cancellation", e);
        }
    }

    /**
     * Audita procesamiento de pagos exitosos
     */
    @AfterReturning(
        pointcut = "execution(* com.babycash.backend.service.PaymentService.processPayment(..))",
        returning = "result"
    )
    public void auditPaymentProcessing(JoinPoint joinPoint, Object result) {
        try {
            if (result != null) {
                auditService.logAction(
                    AuditLog.ActionType.PAYMENT_COMPLETED,
                    "Payment",
                    extractEntityId(result),
                    "Pago procesado exitosamente"
                );
            }
        } catch (Exception e) {
            log.error("Error auditing payment", e);
        }
    }

    /**
     * Audita fallos en procesamiento de pagos
     */
    @AfterThrowing(
        pointcut = "execution(* com.babycash.backend.service.PaymentService.processPayment(..))",
        throwing = "exception"
    )
    public void auditPaymentFailure(JoinPoint joinPoint, Throwable exception) {
        try {
            Object[] args = joinPoint.getArgs();
            Long orderId = args.length > 0 && args[0] instanceof Long ? (Long) args[0] : null;
            
            auditService.logFailure(
                AuditLog.ActionType.PAYMENT_FAILED,
                "Payment",
                orderId,
                "Fallo en procesamiento de pago",
                exception.getMessage()
            );
        } catch (Exception e) {
            log.error("Error auditing payment failure", e);
        }
    }

    /**
     * Audita operaciones de productos (create, update, delete)
     */
    @AfterReturning(
        pointcut = "execution(* com.babycash.backend.service.ProductService.createProduct(..))",
        returning = "result"
    )
    public void auditProductCreation(JoinPoint joinPoint, Object result) {
        try {
            if (result != null) {
                auditService.logAction(
                    AuditLog.ActionType.PRODUCT_CREATED,
                    "Product",
                    extractEntityId(result),
                    "Producto creado"
                );
            }
        } catch (Exception e) {
            log.error("Error auditing product creation", e);
        }
    }

    @AfterReturning(
        pointcut = "execution(* com.babycash.backend.service.ProductService.updateProduct(..))",
        returning = "result"
    )
    public void auditProductUpdate(JoinPoint joinPoint, Object result) {
        try {
            if (result != null) {
                auditService.logAction(
                    AuditLog.ActionType.PRODUCT_UPDATED,
                    "Product",
                    extractEntityId(result),
                    "Producto actualizado"
                );
            }
        } catch (Exception e) {
            log.error("Error auditing product update", e);
        }
    }

    @AfterReturning(
        pointcut = "execution(* com.babycash.backend.service.ProductService.deleteProduct(..))"
    )
    public void auditProductDeletion(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] instanceof Long productId) {
                auditService.logAction(
                    AuditLog.ActionType.PRODUCT_DELETED,
                    "Product",
                    productId,
                    "Producto eliminado"
                );
            }
        } catch (Exception e) {
            log.error("Error auditing product deletion", e);
        }
    }

    /**
     * Extrae el ID de una entidad usando reflection
     */
    private Long extractEntityId(Object entity) {
        try {
            var method = entity.getClass().getMethod("getId");
            return (Long) method.invoke(entity);
        } catch (Exception e) {
            log.debug("Could not extract entity ID", e);
            return null;
        }
    }
}
