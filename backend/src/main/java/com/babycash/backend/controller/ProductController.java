package com.babycash.backend.controller;

import com.babycash.backend.dto.response.ProductResponse;
import com.babycash.backend.model.enums.ProductCategory;
import com.babycash.backend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de productos para consultas públicas del catálogo
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "🛍️ Products", description = "Endpoints públicos para consultar el catálogo de productos. No requiere autenticación. Incluye paginación, búsqueda, filtros por categoría y productos destacados.")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(
            summary = "Obtener todos los productos paginados",
            description = """
                    Retorna una lista paginada de todos los productos activos con soporte para:
                    - Paginación configurable (tamaño y número de página)
                    - Ordenamiento por cualquier campo (createdAt, price, name, rating)
                    - Dirección del ordenamiento (ASC o DESC)
                    
                    Por defecto retorna 12 productos por página ordenados por fecha de creación descendente.
                    Útil para mostrar el catálogo completo o implementar scroll infinito.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de productos obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "content": [
                                                {
                                                  "id": 1,
                                                  "name": "Body de Algodón",
                                                  "description": "Body suave de algodón orgánico...",
                                                  "price": 25000.00,
                                                  "discountPrice": 22500.00,
                                                  "category": "CLOTHING",
                                                  "imageUrl": "https://example.com/image.jpg",
                                                  "stock": 50,
                                                  "featured": true,
                                                  "rating": 4.50,
                                                  "reviewCount": 128
                                                }
                                              ],
                                              "pageable": {
                                                "pageNumber": 0,
                                                "pageSize": 12
                                              },
                                              "totalElements": 27,
                                              "totalPages": 3,
                                              "last": false
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros de paginación inválidos",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @Parameter(description = "Número de página (inicia en 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamaño de página (productos por página)", example = "12")
            @RequestParam(defaultValue = "12") int size,
            
            @Parameter(description = "Campo por el cual ordenar (createdAt, price, name, rating, stock)", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            
            @Parameter(description = "Dirección del ordenamiento (ASC o DESC)", example = "DESC")
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/category/{category}")
    @Operation(
            summary = "Obtener productos por categoría",
            description = """
                    Filtra productos por una categoría específica con paginación.
                    
                    Categorías disponibles:
                    - CLOTHING: Ropa para bebé
                    - TOYS: Juguetes educativos y de entretenimiento
                    - FOOD: Alimentación infantil
                    - FURNITURE: Muebles y cunas
                    - ACCESSORIES: Accesorios varios
                    - HEALTHCARE: Productos de cuidado e higiene
                    - BOOKS: Libros infantiles
                    - OTHER: Otros productos
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Productos filtrados por categoría",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Categoría inválida",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-10-28T10:30:00",
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "Categoría inválida: INVALID",
                                              "path": "/api/products/category/INVALID"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
            @Parameter(description = "Categoría del producto (CLOTHING, TOYS, FOOD, FURNITURE, ACCESSORIES, HEALTHCARE, BOOKS, OTHER)", example = "CLOTHING")
            @PathVariable ProductCategory category,
            
            @Parameter(description = "Número de página (inicia en 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamaño de página", example = "12")
            @RequestParam(defaultValue = "12") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getProductsByCategory(category, pageable));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Buscar productos por texto",
            description = """
                    Busca productos por nombre o descripción con coincidencia parcial (case-insensitive).
                    Útil para implementar barra de búsqueda en tiempo real.
                    
                    Ejemplo: buscar "body" retornará "Body de Algodón", "Body Manga Larga", etc.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Resultados de búsqueda encontrados",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "content": [
                                                {
                                                  "id": 1,
                                                  "name": "Body de Algodón",
                                                  "description": "Body suave...",
                                                  "price": 25000.00,
                                                  "category": "CLOTHING",
                                                  "stock": 50
                                                }
                                              ],
                                              "totalElements": 1
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Query de búsqueda vacío o inválido",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @Parameter(description = "Texto a buscar en nombre y descripción del producto", example = "body", required = true)
            @RequestParam String query,
            
            @Parameter(description = "Número de página", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamaño de página", example = "12")
            @RequestParam(defaultValue = "12") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.searchProducts(query, pageable));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener producto por ID",
            description = """
                    Obtiene los detalles completos de un producto específico por su ID único.
                    Incluye toda la información: precio, descuentos, stock disponible, rating, etc.
                    
                    Ideal para páginas de detalle de producto.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "id": 1,
                                              "name": "Body de Algodón",
                                              "description": "Body suave de algodón orgánico perfecto para la piel delicada del bebé. Disponible en varios colores.",
                                              "price": 25000.00,
                                              "discountPrice": 22500.00,
                                              "category": "CLOTHING",
                                              "imageUrl": "https://example.com/body-algodon.jpg",
                                              "stock": 50,
                                              "featured": true,
                                              "enabled": true,
                                              "rating": 4.50,
                                              "reviewCount": 128,
                                              "createdAt": "2025-10-28T10:00:00",
                                              "updatedAt": "2025-10-28T10:00:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-10-28T10:30:00",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Producto no encontrado con id: 999",
                                              "path": "/api/products/999"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "ID único del producto", example = "1", required = true)
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/featured")
    @Operation(
            summary = "Obtener productos destacados",
            description = """
                    Retorna una lista de productos marcados como destacados (featured=true).
                    Estos productos suelen mostrarse en la página principal, carruseles promocionales
                    o secciones especiales del sitio.
                    
                    No requiere paginación ya que típicamente son pocos productos (5-10).
                    Los resultados están cacheados para máximo rendimiento.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de productos destacados",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = List.class),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              {
                                                "id": 1,
                                                "name": "Body de Algodón",
                                                "price": 25000.00,
                                                "discountPrice": 22500.00,
                                                "category": "CLOTHING",
                                                "imageUrl": "https://example.com/body.jpg",
                                                "featured": true,
                                                "rating": 4.50
                                              },
                                              {
                                                "id": 5,
                                                "name": "Peluche Musical",
                                                "price": 45000.00,
                                                "category": "TOYS",
                                                "imageUrl": "https://example.com/peluche.jpg",
                                                "featured": true,
                                                "rating": 4.80
                                              }
                                            ]
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<List<ProductResponse>> getFeaturedProducts() {
        return ResponseEntity.ok(productService.getFeaturedProducts());
    }
}
