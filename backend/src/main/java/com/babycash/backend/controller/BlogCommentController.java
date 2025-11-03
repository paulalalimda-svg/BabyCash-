package com.babycash.backend.controller;

import com.babycash.backend.dto.comment.CommentRequest;
import com.babycash.backend.dto.comment.CommentResponse;
import com.babycash.backend.service.BlogCommentService;
import com.babycash.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blog/{postId}/comments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class BlogCommentController {

    private final BlogCommentService commentService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        Long userId = userService.getUserByEmail(email).getId();
        log.info("POST /api/blog/{}/comments - Creating comment by user: {}", postId, userId);
        CommentResponse response = commentService.createComment(postId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        log.info("GET /api/blog/{}/comments - Fetching approved comments", postId);
        List<CommentResponse> comments = commentService.getApprovedCommentsByPost(postId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getCommentsCount(@PathVariable Long postId) {
        log.info("GET /api/blog/{}/comments/count - Counting approved comments", postId);
        long count = commentService.getApprovedCommentsCount(postId);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        Long userId = userService.getUserByEmail(email).getId();
        log.info("PUT /api/blog/{}/comments/{} - Updating comment by user: {}", postId, commentId, userId);
        CommentResponse response = commentService.updateComment(commentId, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        Long userId = userService.getUserByEmail(email).getId();
        log.info("DELETE /api/blog/{}/comments/{} - Deleting comment by user: {}", postId, commentId, userId);
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    // Admin endpoints

    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CommentResponse>> getPendingComments() {
        log.info("GET /api/blog/comments/admin/pending - Fetching pending comments");
        List<CommentResponse> comments = commentService.getPendingComments();
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{commentId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommentResponse> approveComment(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        log.info("POST /api/blog/{}/comments/{}/approve - Approving comment", postId, commentId);
        CommentResponse response = commentService.approveComment(commentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/pending/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getPendingCommentsCount() {
        log.info("GET /api/blog/comments/admin/pending/count - Counting pending comments");
        long count = commentService.getPendingCommentsCount();
        return ResponseEntity.ok(count);
    }
}
