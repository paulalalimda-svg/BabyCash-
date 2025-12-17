package com.babycash.backend.controller;

import com.babycash.backend.repository.OrderRepository;
import com.babycash.backend.repository.ProductRepository;
import com.babycash.backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller for generating simple CSV reports
 */
@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "📊 Reports", description = "Endpoints para generación de reportes CSV")
public class ReportController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @GetMapping("/dashboard/csv")
    @Operation(summary = "Descargar extracto del dashboard en CSV")
    public ResponseEntity<String> downloadDashboardCSV() {
        log.info("Generating dashboard CSV report");

        // Get statistics
        long totalUsers = userRepository.count();
        long totalOrders = orderRepository.count();
        long totalProducts = productRepository.count();

        // Build CSV content
        StringBuilder csv = new StringBuilder();
        csv.append("BabyCash - Extracto del Dashboard\n");
        csv.append("Generado el: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n");
        csv.append("\n");
        csv.append("Métrica,Valor\n");
        csv.append("Total Usuarios,").append(totalUsers).append("\n");
        csv.append("Total Pedidos,").append(totalOrders).append("\n");
        csv.append("Total Productos,").append(totalProducts).append("\n");

        // Set headers for CSV download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv"));
        headers.setContentDispositionFormData("attachment", "dashboard-" +
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".csv");

        log.info("Dashboard CSV report generated successfully");
        return ResponseEntity.ok()
                .headers(headers)
                .body(csv.toString());
    }
}
