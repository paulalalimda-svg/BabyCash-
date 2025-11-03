package com.babycash.backend.service;

import com.babycash.backend.dto.request.BlogPostRequest;
import com.babycash.backend.dto.response.BlogPostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface for Blog Service
 * Dependency Inversion Principle - depend on abstractions
 */
public interface IBlogPostService {

    /**
     * Create a new blog post
     */
    BlogPostResponse createPost(String authorEmail, BlogPostRequest request);

    /**
     * Update an existing blog post
     */
    BlogPostResponse updatePost(Long id, String authorEmail, BlogPostRequest request);

    /**
     * Delete a blog post
     */
    void deletePost(Long id, String authorEmail);

    /**
     * Get post by ID
     */
    BlogPostResponse getPostById(Long id);

    /**
     * Get post by slug (SEO-friendly)
     */
    BlogPostResponse getPostBySlug(String slug);

    /**
     * Get all published posts (paginated)
     */
    Page<BlogPostResponse> getPublishedPosts(Pageable pageable);

    /**
     * Get all posts (published and unpublished) - Admin only
     */
    Page<BlogPostResponse> getAllPosts(Pageable pageable);

    /**
     * Get posts by author
     */
    Page<BlogPostResponse> getPostsByAuthor(String authorEmail, Pageable pageable);

    /**
     * Get featured posts
     */
    List<BlogPostResponse> getFeaturedPosts();

    /**
     * Search posts
     */
    Page<BlogPostResponse> searchPosts(String query, Pageable pageable);

    /**
     * Get posts by tag
     */
    Page<BlogPostResponse> getPostsByTag(String tag, Pageable pageable);

    /**
     * Publish a post
     */
    BlogPostResponse publishPost(Long id, String authorEmail);

    /**
     * Unpublish a post
     */
    BlogPostResponse unpublishPost(Long id, String authorEmail);

    /**
     * Toggle featured status
     */
    BlogPostResponse toggleFeatured(Long id, String authorEmail);

    /**
     * Increment view count
     */
    void incrementViewCount(Long id);

    /**
     * Get most viewed posts
     */
    List<BlogPostResponse> getMostViewedPosts();
}
