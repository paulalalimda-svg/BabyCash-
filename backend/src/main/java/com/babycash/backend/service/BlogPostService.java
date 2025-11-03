package com.babycash.backend.service;

import com.babycash.backend.dto.request.BlogPostRequest;
import com.babycash.backend.dto.response.BlogPostResponse;
import com.babycash.backend.exception.custom.BusinessException;
import com.babycash.backend.exception.custom.ResourceNotFoundException;
import com.babycash.backend.model.entity.BlogPost;
import com.babycash.backend.model.entity.User;
import com.babycash.backend.repository.BlogPostRepository;
import com.babycash.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for blog post operations
 * Single Responsibility: All blog business logic in one place
 * Clean Code: Clear method names, focused responsibilities
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BlogPostService implements IBlogPostService {

    private final BlogPostRepository blogPostRepository;
    private final UserRepository userRepository;

    private static final String POST_NOT_FOUND = "Post de blog no encontrado";
    private static final String USER_NOT_FOUND = "Usuario no encontrado";
    private static final String UNAUTHORIZED = "No tienes permiso para realizar esta acción";

    @Override
    @Transactional
    public BlogPostResponse createPost(String authorEmail, BlogPostRequest request) {
        log.info("Creating blog post: {} by author: {}", request.getTitle(), authorEmail);

        User author = findUserByEmail(authorEmail);

        // Generate unique slug
        String slug = generateUniqueSlug(request.getTitle());

        BlogPost post = BlogPost.builder()
                .title(request.getTitle())
                .slug(slug)
                .excerpt(request.getExcerpt())
                .content(request.getContent())
                .author(author)
                .imageUrl(request.getImageUrl())
                .published(false) // Always start as draft
                .featured(false)
                .tags(request.getTags())
                .viewCount(0L)
                .build();

        BlogPost savedPost = blogPostRepository.save(post);
        log.info("Blog post created with ID: {} and slug: {}", savedPost.getId(), savedPost.getSlug());

        return mapToResponse(savedPost);
    }

    @Override
    @Transactional
    public BlogPostResponse updatePost(Long id, String authorEmail, BlogPostRequest request) {
        log.info("Updating blog post ID: {} by author: {}", id, authorEmail);

        BlogPost post = findPostById(id);
        User author = findUserByEmail(authorEmail);

        // Verify ownership
        validateOwnership(post, author);

        // Update slug if title changed (check BEFORE updating)
        boolean titleChanged = !post.getTitle().equals(request.getTitle());
        if (titleChanged) {
            String newSlug = generateUniqueSlug(request.getTitle());
            post.setSlug(newSlug);
        }

        // Update fields
        post.setTitle(request.getTitle());
        post.setExcerpt(request.getExcerpt());
        post.setContent(request.getContent());
        post.setImageUrl(request.getImageUrl());
        post.setTags(request.getTags());

        BlogPost updatedPost = blogPostRepository.save(post);
        log.info("Blog post updated: {}", updatedPost.getId());

        return mapToResponse(updatedPost);
    }

    @Override
    @Transactional
    public void deletePost(Long id, String authorEmail) {
        log.info("Deleting blog post ID: {} by author: {}", id, authorEmail);

        BlogPost post = findPostById(id);
        User author = findUserByEmail(authorEmail);

        // Verify ownership or admin
        if (!post.getAuthor().getId().equals(author.getId()) && 
            !author.getRole().name().equals("ADMIN")) {
            throw new BusinessException(UNAUTHORIZED);
        }

        blogPostRepository.delete(post);
        log.info("Blog post deleted: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public BlogPostResponse getPostById(Long id) {
        BlogPost post = findPostById(id);
        return mapToResponse(post);
    }

    @Override
    @Transactional
    public BlogPostResponse getPostBySlug(String slug) {
        BlogPost post = blogPostRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));

        // Increment view count
        post.incrementViewCount();
        blogPostRepository.save(post);

        return mapToResponse(post);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlogPostResponse> getPublishedPosts(Pageable pageable) {
        Page<BlogPost> posts = blogPostRepository.findByPublishedTrueOrderByPublishedAtDesc(pageable);
        return posts.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<BlogPostResponse> getAllPosts(Pageable pageable) {
        log.info("Getting all posts (published and unpublished)");
        Page<BlogPost> posts = blogPostRepository.findAll(pageable);
        return posts.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlogPostResponse> getPostsByAuthor(String authorEmail, Pageable pageable) {
        User author = findUserByEmail(authorEmail);
        Page<BlogPost> posts = blogPostRepository.findByAuthorOrderByCreatedAtDesc(author, pageable);
        return posts.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogPostResponse> getFeaturedPosts() {
        List<BlogPost> posts = blogPostRepository
                .findTop5ByPublishedTrueAndFeaturedTrueOrderByPublishedAtDesc();
        return posts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlogPostResponse> searchPosts(String query, Pageable pageable) {
        Page<BlogPost> posts = blogPostRepository.searchPublishedPosts(query, pageable);
        return posts.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlogPostResponse> getPostsByTag(String tag, Pageable pageable) {
        Page<BlogPost> posts = blogPostRepository.findByTag(tag, pageable);
        return posts.map(this::mapToResponse);
    }

    @Override
    @Transactional
    public BlogPostResponse publishPost(Long id, String authorEmail) {
        log.info("Publishing blog post ID: {} by author: {}", id, authorEmail);

        BlogPost post = findPostById(id);
        User author = findUserByEmail(authorEmail);

        validateOwnership(post, author);

        post.publish();
        BlogPost publishedPost = blogPostRepository.save(post);

        log.info("Blog post published: {}", publishedPost.getId());
        return mapToResponse(publishedPost);
    }

    @Override
    @Transactional
    public BlogPostResponse unpublishPost(Long id, String authorEmail) {
        log.info("Unpublishing blog post ID: {} by author: {}", id, authorEmail);

        BlogPost post = findPostById(id);
        User author = findUserByEmail(authorEmail);

        validateOwnership(post, author);

        post.unpublish();
        BlogPost unpublishedPost = blogPostRepository.save(post);

        log.info("Blog post unpublished: {}", unpublishedPost.getId());
        return mapToResponse(unpublishedPost);
    }

    @Override
    @Transactional
    public BlogPostResponse toggleFeatured(Long id, String authorEmail) {
        log.info("Toggling featured status for blog post ID: {}", id);

        BlogPost post = findPostById(id);
        User author = findUserByEmail(authorEmail);

        // Only admins can toggle featured
        if (!author.getRole().name().equals("ADMIN")) {
            throw new BusinessException("Solo administradores pueden destacar posts");
        }

        // If trying to feature a post, check limit
        if (!post.getFeatured()) {
            long featuredCount = blogPostRepository.countByFeaturedTrue();
            if (featuredCount >= 3) {
                throw new BusinessException("Solo se pueden destacar máximo 3 posts. Desmarca uno primero.");
            }
        }

        post.toggleFeatured();
        BlogPost updatedPost = blogPostRepository.save(post);

        log.info("Blog post featured status toggled: {}", updatedPost.getId());
        return mapToResponse(updatedPost);
    }

    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        BlogPost post = findPostById(id);
        post.incrementViewCount();
        blogPostRepository.save(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogPostResponse> getMostViewedPosts() {
        List<BlogPost> posts = blogPostRepository.findTop10ByPublishedTrueOrderByViewCountDesc();
        return posts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ==================== Private Helper Methods ====================

    /**
     * Find post by ID or throw exception
     */
    private BlogPost findPostById(Long id) {
        return blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND + " con ID: " + id));
    }

    /**
     * Find user by email or throw exception
     */
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));
    }

    /**
     * Validate that user is the owner of the post
     */
    private void validateOwnership(BlogPost post, User user) {
        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new BusinessException(UNAUTHORIZED);
        }
    }

    /**
     * Generate unique slug from title
     */
    private String generateUniqueSlug(String title) {
        String baseSlug = BlogPost.generateSlug(title);
        String slug = baseSlug;
        int counter = 1;

        while (blogPostRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }

    /**
     * Map entity to DTO
     */
    private BlogPostResponse mapToResponse(BlogPost post) {
        return BlogPostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .excerpt(post.getExcerpt())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .published(post.getPublished())
                .featured(post.getFeatured())
                .viewCount(post.getViewCount())
                .tags(post.getTags())
                .publishedAt(post.getPublishedAt())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .author(BlogPostResponse.AuthorInfo.builder()
                        .id(post.getAuthor().getId())
                        .firstName(post.getAuthor().getFirstName())
                        .lastName(post.getAuthor().getLastName())
                        .email(post.getAuthor().getEmail())
                        .build())
                .build();
    }
}
