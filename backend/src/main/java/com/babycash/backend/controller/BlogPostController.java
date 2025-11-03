package com.babycash.backend.controller;

import com.babycash.backend.dto.request.BlogPostRequest;
import com.babycash.backend.dto.response.BlogPostResponse;
import com.babycash.backend.service.IBlogPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for blog post operations
 * Thin controller - delegates all logic to service
 * RESTful endpoints with proper HTTP methods
 */
@RestController
@RequestMapping("/api/blog")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "📝 Blog", description = "Endpoints para gestionar posts del blog")
public class BlogPostController {

    private final IBlogPostService blogPostService;

    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Crear nuevo post", description = "Crea un nuevo post de blog. Solo usuarios autenticados.")
    public ResponseEntity<BlogPostResponse> createPost(
            Authentication authentication,
            @Valid @RequestBody BlogPostRequest request) {

        String authorEmail = authentication.getName();
        BlogPostResponse response = blogPostService.createPost(authorEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Actualizar post", description = "Actualiza un post existente. Solo el autor puede editar.")
    public ResponseEntity<BlogPostResponse> updatePost(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody BlogPostRequest request) {

        String authorEmail = authentication.getName();
        BlogPostResponse response = blogPostService.updatePost(id, authorEmail, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Eliminar post", description = "Elimina un post. Solo el autor o admin pueden eliminar.")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            Authentication authentication) {

        String authorEmail = authentication.getName();
        blogPostService.deletePost(id, authorEmail);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Obtener posts publicados", description = "Lista paginada de posts publicados (público)")
    public ResponseEntity<Page<BlogPostResponse>> getPublishedPosts(
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"));
        Page<BlogPostResponse> posts = blogPostService.getPublishedPosts(pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener post por ID", description = "Obtiene un post específico por ID")
    public ResponseEntity<BlogPostResponse> getPostById(@PathVariable Long id) {
        BlogPostResponse response = blogPostService.getPostById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Obtener post por slug", description = "Obtiene un post por su slug SEO-friendly e incrementa contador de vistas")
    public ResponseEntity<BlogPostResponse> getPostBySlug(@PathVariable String slug) {
        BlogPostResponse response = blogPostService.getPostBySlug(slug);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/featured")
    @Operation(summary = "Obtener posts destacados", description = "Lista los 5 posts destacados más recientes")
    public ResponseEntity<List<BlogPostResponse>> getFeaturedPosts() {
        List<BlogPostResponse> posts = blogPostService.getFeaturedPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar posts", description = "Busca posts por título, contenido o extracto")
    public ResponseEntity<Page<BlogPostResponse>> searchPosts(
            @Parameter(description = "Término de búsqueda") @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<BlogPostResponse> posts = blogPostService.searchPosts(q, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/tag/{tag}")
    @Operation(summary = "Obtener posts por tag", description = "Lista posts filtrados por tag")
    public ResponseEntity<Page<BlogPostResponse>> getPostsByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<BlogPostResponse> posts = blogPostService.getPostsByTag(tag, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/author/me")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Obtener mis posts", description = "Lista posts del autor autenticado")
    public ResponseEntity<Page<BlogPostResponse>> getMyPosts(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String authorEmail = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);
        Page<BlogPostResponse> posts = blogPostService.getPostsByAuthor(authorEmail, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/most-viewed")
    @Operation(summary = "Posts más vistos", description = "Top 10 posts más vistos")
    public ResponseEntity<List<BlogPostResponse>> getMostViewedPosts() {
        List<BlogPostResponse> posts = blogPostService.getMostViewedPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/admin/all")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Obtener todos los posts (admin)", description = "Lista paginada de TODOS los posts (publicados y no publicados). Solo admin.")
    public ResponseEntity<Page<BlogPostResponse>> getAllPostsAdmin(
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<BlogPostResponse> posts = blogPostService.getAllPosts(pageable);
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{id}/publish")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Publicar post", description = "Publica un post (cambia de draft a publicado)")
    public ResponseEntity<BlogPostResponse> publishPost(
            @PathVariable Long id,
            Authentication authentication) {

        String authorEmail = authentication.getName();
        BlogPostResponse response = blogPostService.publishPost(id, authorEmail);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/unpublish")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Despublicar post", description = "Despublica un post (cambia a draft)")
    public ResponseEntity<BlogPostResponse> unpublishPost(
            @PathVariable Long id,
            Authentication authentication) {

        String authorEmail = authentication.getName();
        BlogPostResponse response = blogPostService.unpublishPost(id, authorEmail);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/toggle-featured")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Toggle destacado", description = "Alterna el estado destacado del post (solo admin)")
    public ResponseEntity<BlogPostResponse> toggleFeatured(
            @PathVariable Long id,
            Authentication authentication) {

        String authorEmail = authentication.getName();
        BlogPostResponse response = blogPostService.toggleFeatured(id, authorEmail);
        return ResponseEntity.ok(response);
    }
}
