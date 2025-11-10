package com.babycash.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para servir la aplicación frontend de React.
 *
 * Este controlador maneja todas las rutas que no son API (no empiezan con /api/)
 * y las redirige al index.html del frontend, permitiendo que React Router
 * maneje el routing del lado del cliente.
 *
 * Estructura MVC:
 * - Spring Boot sirve el backend (REST API en /api/*)
 * - React sirve el frontend (todas las demás rutas)
 * - Los archivos estáticos (JS, CSS, imágenes) se sirven desde /static
 *
 * @author Baby Cash Team
 * @version 1.0
 * @since 2025-11-08
 */
@Controller
public class FrontendController {

    /**
     * Sirve el index.html del frontend para todas las rutas que no son API.
     *
     * Esto permite que React Router maneje el routing del lado del cliente.
     * Las rutas de API (/api/**) son manejadas por otros controladores REST.
     *
     * Rutas manejadas:
     * - / (home)
     * - /products
     * - /product/{id}
     * - /cart
     * - /checkout
     * - /login
     * - /register
     * - /profile
     * - /admin/**
     * - /blog/**
     * - Y cualquier otra ruta del frontend
     *
     * @return El nombre de la plantilla Thymeleaf (index.html)
     */
    @GetMapping(value = {
        "/",
        "/products",
        "/products/**",
        "/product/**",
        "/cart",
        "/checkout",
        "/checkout/**",
        "/login",
        "/register",
        "/profile",
        "/profile/**",
        "/admin",
        "/admin/**",
        "/blog",
        "/blog/**",
        "/about",
        "/contact",
        "/testimonials",
        "/terms",
        "/privacy",
        "/loyalty",
        "/orders",
        "/orders/**"
    })
    public String forward() {
        return "forward:/index.html";
    }

    /**
     * Método alternativo para servir el index.html directamente.
     * Se puede usar si se prefiere un approach diferente.
     */
    // @GetMapping("/{path:[^\\.]*}")
    // public String redirect() {
    //     return "forward:/";
    // }
}
