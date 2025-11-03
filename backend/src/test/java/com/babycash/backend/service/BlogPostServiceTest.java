package com.babycash.backend.service;

import com.babycash.backend.dto.request.BlogPostRequest;
import com.babycash.backend.dto.response.BlogPostResponse;
import com.babycash.backend.exception.custom.BusinessException;
import com.babycash.backend.exception.custom.ResourceNotFoundException;
import com.babycash.backend.model.entity.BlogPost;
import com.babycash.backend.model.entity.User;
import com.babycash.backend.model.enums.UserRole;
import com.babycash.backend.repository.BlogPostRepository;
import com.babycash.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BlogPostService
 * Following Clean Code and testing best practices
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BlogPostService Unit Tests")
class BlogPostServiceTest {

    @Mock
    private BlogPostRepository blogPostRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BlogPostService blogPostService;

    private User testAuthor;
    private User testAdmin;
    private BlogPost testPost;
    private BlogPostRequest testRequest;

    @BeforeEach
    void setUp() {
        // Setup test author
        testAuthor = User.builder()
                .id(1L)
                .email("author@test.com")
                .firstName("Test")
                .lastName("Author")
                .role(UserRole.USER)
                .build();

        // Setup test admin
        testAdmin = User.builder()
                .id(2L)
                .email("admin@test.com")
                .firstName("Admin")
                .lastName("User")
                .role(UserRole.ADMIN)
                .build();

        // Setup test post
        testPost = BlogPost.builder()
                .id(1L)
                .title("Test Post")
                .slug("test-post")
                .excerpt("Test excerpt")
                .content("Test content for the blog post")
                .author(testAuthor)
                .imageUrl("https://example.com/image.jpg")
                .published(false)
                .featured(false)
                .viewCount(0L)
                .tags(Arrays.asList("test", "unit"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Setup test request
        testRequest = BlogPostRequest.builder()
                .title("Test Post")
                .excerpt("Test excerpt")
                .content("Test content for the blog post")
                .imageUrl("https://example.com/image.jpg")
                .published(false)
                .featured(false)
                .tags(Arrays.asList("test", "unit"))
                .build();
    }

    @Test
    @DisplayName("Should create post successfully")
    void shouldCreatePostSuccessfully() {
        // Given
        when(userRepository.findByEmail(testAuthor.getEmail())).thenReturn(Optional.of(testAuthor));
        when(blogPostRepository.existsBySlug(anyString())).thenReturn(false);
        when(blogPostRepository.save(any(BlogPost.class))).thenReturn(testPost);

        // When
        BlogPostResponse response = blogPostService.createPost(testAuthor.getEmail(), testRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Test Post");
        assertThat(response.getSlug()).isEqualTo("test-post");
        assertThat(response.getPublished()).isFalse();
        verify(blogPostRepository, times(1)).save(any(BlogPost.class));
        verify(userRepository, times(1)).findByEmail(testAuthor.getEmail());
    }

    @Test
    @DisplayName("Should throw exception when creating post with non-existent user")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> blogPostService.createPost("nonexistent@test.com", testRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");

        verify(blogPostRepository, never()).save(any(BlogPost.class));
    }

    @Test
    @DisplayName("Should generate unique slug when slug exists")
    void shouldGenerateUniqueSlug() {
        // Given
        when(userRepository.findByEmail(testAuthor.getEmail())).thenReturn(Optional.of(testAuthor));
        when(blogPostRepository.existsBySlug("test-post")).thenReturn(true);
        when(blogPostRepository.existsBySlug("test-post-1")).thenReturn(false);
        
        BlogPost postWithSuffix = BlogPost.builder()
                .id(2L)
                .title("Test Post")
                .slug("test-post-1")
                .content("Content")
                .author(testAuthor)
                .build();
        
        when(blogPostRepository.save(any(BlogPost.class))).thenReturn(postWithSuffix);

        // When
        BlogPostResponse response = blogPostService.createPost(testAuthor.getEmail(), testRequest);

        // Then
        assertThat(response.getSlug()).isEqualTo("test-post-1");
        verify(blogPostRepository, times(2)).existsBySlug(anyString());
    }

    @Test
    @DisplayName("Should update post successfully")
    void shouldUpdatePostSuccessfully() {
        // Given
        BlogPostRequest updateRequest = BlogPostRequest.builder()
                .title("Updated Title")
                .excerpt("Updated excerpt")
                .content("Updated content for the blog post")
                .imageUrl("https://example.com/new-image.jpg")
                .tags(Arrays.asList("updated", "test"))
                .build();

        when(blogPostRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findByEmail(testAuthor.getEmail())).thenReturn(Optional.of(testAuthor));
        // Only stub existsBySlug if title changes
        when(blogPostRepository.existsBySlug("updated-title")).thenReturn(false);
        when(blogPostRepository.save(any(BlogPost.class))).thenReturn(testPost);

        // When
        BlogPostResponse response = blogPostService.updatePost(1L, testAuthor.getEmail(), updateRequest);

        // Then
        assertThat(response).isNotNull();
        verify(blogPostRepository, times(1)).save(any(BlogPost.class));
        verify(blogPostRepository, times(1)).existsBySlug("updated-title");
    }

    @Test
    @DisplayName("Should throw exception when updating post by non-owner")
    void shouldThrowExceptionWhenUpdatingByNonOwner() {
        // Given
        User otherUser = User.builder()
                .id(999L)
                .email("other@test.com")
                .firstName("Other")
                .lastName("User")
                .role(UserRole.USER)
                .build();

        when(blogPostRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findByEmail(otherUser.getEmail())).thenReturn(Optional.of(otherUser));

        // When & Then
        assertThatThrownBy(() -> blogPostService.updatePost(1L, otherUser.getEmail(), testRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("No tienes permiso");

        verify(blogPostRepository, never()).save(any(BlogPost.class));
    }

    @Test
    @DisplayName("Should delete post successfully by owner")
    void shouldDeletePostByOwner() {
        // Given
        when(blogPostRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findByEmail(testAuthor.getEmail())).thenReturn(Optional.of(testAuthor));
        doNothing().when(blogPostRepository).delete(any(BlogPost.class));

        // When
        blogPostService.deletePost(1L, testAuthor.getEmail());

        // Then
        verify(blogPostRepository, times(1)).delete(testPost);
    }

    @Test
    @DisplayName("Should delete post successfully by admin")
    void shouldDeletePostByAdmin() {
        // Given
        when(blogPostRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findByEmail(testAdmin.getEmail())).thenReturn(Optional.of(testAdmin));
        doNothing().when(blogPostRepository).delete(any(BlogPost.class));

        // When
        blogPostService.deletePost(1L, testAdmin.getEmail());

        // Then
        verify(blogPostRepository, times(1)).delete(testPost);
    }

    @Test
    @DisplayName("Should get post by ID successfully")
    void shouldGetPostByIdSuccessfully() {
        // Given
        when(blogPostRepository.findById(1L)).thenReturn(Optional.of(testPost));

        // When
        BlogPostResponse response = blogPostService.getPostById(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Test Post");
    }

    @Test
    @DisplayName("Should throw exception when post not found by ID")
    void shouldThrowExceptionWhenPostNotFoundById() {
        // Given
        when(blogPostRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> blogPostService.getPostById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Post de blog no encontrado");
    }

    @Test
    @DisplayName("Should get post by slug and increment view count")
    void shouldGetPostBySlugAndIncrementViews() {
        // Given
        when(blogPostRepository.findBySlug("test-post")).thenReturn(Optional.of(testPost));
        when(blogPostRepository.save(any(BlogPost.class))).thenReturn(testPost);

        // When
        BlogPostResponse response = blogPostService.getPostBySlug("test-post");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getSlug()).isEqualTo("test-post");
        verify(blogPostRepository, times(1)).save(any(BlogPost.class)); // Save for view count
    }

    @Test
    @DisplayName("Should get published posts")
    void shouldGetPublishedPosts() {
        // Given
        testPost.setPublished(true);
        testPost.setPublishedAt(LocalDateTime.now());
        
        List<BlogPost> posts = Arrays.asList(testPost);
        Page<BlogPost> pagedPosts = new PageImpl<>(posts);
        
        Pageable pageable = PageRequest.of(0, 10);
        when(blogPostRepository.findByPublishedTrueOrderByPublishedAtDesc(pageable))
                .thenReturn(pagedPosts);

        // When
        Page<BlogPostResponse> response = blogPostService.getPublishedPosts(pageable);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getPublished()).isTrue();
    }

    @Test
    @DisplayName("Should get featured posts")
    void shouldGetFeaturedPosts() {
        // Given
        testPost.setPublished(true);
        testPost.setFeatured(true);
        testPost.setPublishedAt(LocalDateTime.now());
        
        when(blogPostRepository.findTop5ByPublishedTrueAndFeaturedTrueOrderByPublishedAtDesc())
                .thenReturn(Arrays.asList(testPost));

        // When
        List<BlogPostResponse> response = blogPostService.getFeaturedPosts();

        // Then
        assertThat(response).isNotNull();
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getFeatured()).isTrue();
    }

    @Test
    @DisplayName("Should publish post successfully")
    void shouldPublishPostSuccessfully() {
        // Given
        when(blogPostRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findByEmail(testAuthor.getEmail())).thenReturn(Optional.of(testAuthor));
        when(blogPostRepository.save(any(BlogPost.class))).thenReturn(testPost);

        // When
        BlogPostResponse response = blogPostService.publishPost(1L, testAuthor.getEmail());

        // Then
        assertThat(response).isNotNull();
        verify(blogPostRepository, times(1)).save(any(BlogPost.class));
    }

    @Test
    @DisplayName("Should unpublish post successfully")
    void shouldUnpublishPostSuccessfully() {
        // Given
        testPost.setPublished(true);
        testPost.setPublishedAt(LocalDateTime.now());
        
        when(blogPostRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findByEmail(testAuthor.getEmail())).thenReturn(Optional.of(testAuthor));
        when(blogPostRepository.save(any(BlogPost.class))).thenReturn(testPost);

        // When
        BlogPostResponse response = blogPostService.unpublishPost(1L, testAuthor.getEmail());

        // Then
        assertThat(response).isNotNull();
        verify(blogPostRepository, times(1)).save(any(BlogPost.class));
    }

    @Test
    @DisplayName("Should toggle featured status by admin")
    void shouldToggleFeaturedByAdmin() {
        // Given
        when(blogPostRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findByEmail(testAdmin.getEmail())).thenReturn(Optional.of(testAdmin));
        when(blogPostRepository.save(any(BlogPost.class))).thenReturn(testPost);

        // When
        BlogPostResponse response = blogPostService.toggleFeatured(1L, testAdmin.getEmail());

        // Then
        assertThat(response).isNotNull();
        verify(blogPostRepository, times(1)).save(any(BlogPost.class));
    }

    @Test
    @DisplayName("Should throw exception when non-admin tries to toggle featured")
    void shouldThrowExceptionWhenNonAdminTogglesFeatured() {
        // Given
        when(blogPostRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findByEmail(testAuthor.getEmail())).thenReturn(Optional.of(testAuthor));

        // When & Then
        assertThatThrownBy(() -> blogPostService.toggleFeatured(1L, testAuthor.getEmail()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Solo administradores");

        verify(blogPostRepository, never()).save(any(BlogPost.class));
    }

    @Test
    @DisplayName("Should search posts successfully")
    void shouldSearchPostsSuccessfully() {
        // Given
        testPost.setPublished(true);
        List<BlogPost> posts = Arrays.asList(testPost);
        Page<BlogPost> pagedPosts = new PageImpl<>(posts);
        
        Pageable pageable = PageRequest.of(0, 10);
        when(blogPostRepository.searchPublishedPosts("test", pageable)).thenReturn(pagedPosts);

        // When
        Page<BlogPostResponse> response = blogPostService.searchPosts("test", pageable);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Should get posts by tag successfully")
    void shouldGetPostsByTagSuccessfully() {
        // Given
        testPost.setPublished(true);
        List<BlogPost> posts = Arrays.asList(testPost);
        Page<BlogPost> pagedPosts = new PageImpl<>(posts);
        
        Pageable pageable = PageRequest.of(0, 10);
        when(blogPostRepository.findByTag("test", pageable)).thenReturn(pagedPosts);

        // When
        Page<BlogPostResponse> response = blogPostService.getPostsByTag("test", pageable);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getTags()).contains("test");
    }

    @Test
    @DisplayName("Should get most viewed posts")
    void shouldGetMostViewedPosts() {
        // Given
        testPost.setPublished(true);
        testPost.setViewCount(100L);
        
        when(blogPostRepository.findTop10ByPublishedTrueOrderByViewCountDesc())
                .thenReturn(Arrays.asList(testPost));

        // When
        List<BlogPostResponse> response = blogPostService.getMostViewedPosts();

        // Then
        assertThat(response).isNotNull();
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getViewCount()).isEqualTo(100L);
    }
}
