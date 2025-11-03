package com.babycash.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for blog posts
 * Decouples API from domain model
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostResponse {

    private Long id;
    private String title;
    private String slug;
    private String excerpt;
    private String content;
    private String imageUrl;
    private Boolean published;
    private Boolean featured;
    private Long viewCount;
    private List<String> tags;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Author information (nested DTO)
    private AuthorInfo author;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
    }
}
