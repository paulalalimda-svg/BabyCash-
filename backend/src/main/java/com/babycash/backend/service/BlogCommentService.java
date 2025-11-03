package com.babycash.backend.service;

import com.babycash.backend.dto.comment.CommentRequest;
import com.babycash.backend.dto.comment.CommentResponse;
import com.babycash.backend.model.entity.BlogComment;
import com.babycash.backend.model.entity.BlogPost;
import com.babycash.backend.model.entity.User;
import com.babycash.backend.repository.BlogCommentRepository;
import com.babycash.backend.repository.BlogPostRepository;
import com.babycash.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlogCommentService {

    private final BlogCommentRepository commentRepository;
    private final BlogPostRepository blogPostRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse createComment(Long postId, CommentRequest request, Long userId) {
        log.info("Creating comment for post {} by user {}", postId, userId);

        BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Blog post not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        BlogComment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
        }

        BlogComment comment = BlogComment.builder()
                .blogPost(post)
                .user(user)
                .content(request.getContent())
                .parentComment(parentComment)
                .approved(false) // Requires approval by default
                .build();

        BlogComment saved = commentRepository.save(comment);
        log.info("Comment created successfully with ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getApprovedCommentsByPost(Long postId) {
        log.info("Fetching approved comments for post: {}", postId);

        List<BlogComment> topLevelComments = commentRepository
                .findByBlogPostIdAndApprovedTrueAndParentCommentIsNullOrderByCreatedAtDesc(postId);

        return topLevelComments.stream()
                .map(this::mapToResponseWithReplies)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getPendingComments() {
        log.info("Fetching pending comments for approval");
        return commentRepository.findByApprovedFalseOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByUser(Long userId) {
        log.info("Fetching comments by user: {}", userId);
        return commentRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getApprovedCommentsCount(Long postId) {
        return commentRepository.countApprovedCommentsByPostId(postId);
    }

    @Transactional(readOnly = true)
    public long getPendingCommentsCount() {
        return commentRepository.countByApprovedFalse();
    }

    @Transactional
    public CommentResponse approveComment(Long commentId) {
        log.info("Approving comment: {}", commentId);

        BlogComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        comment.approve();
        BlogComment saved = commentRepository.save(comment);

        log.info("Comment approved successfully: {}", commentId);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        log.info("Deleting comment: {}", commentId);

        if (!commentRepository.existsById(commentId)) {
            throw new IllegalArgumentException("Comment not found");
        }

        commentRepository.deleteById(commentId);
        log.info("Comment deleted successfully: {}", commentId);
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, CommentRequest request, Long userId) {
        log.info("Updating comment: {} by user: {}", commentId, userId);

        BlogComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this comment");
        }

        comment.setContent(request.getContent());
        comment.setApproved(false); // Requires re-approval after edit

        BlogComment saved = commentRepository.save(comment);
        log.info("Comment updated successfully: {}", commentId);

        return mapToResponse(saved);
    }

    // Helper methods for mapping

    private CommentResponse mapToResponse(BlogComment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .blogPostId(comment.getBlogPost().getId())
                .user(mapUserInfo(comment.getUser()))
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .approved(comment.getApproved())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    private CommentResponse mapToResponseWithReplies(BlogComment comment) {
        CommentResponse response = mapToResponse(comment);

        List<BlogComment> replies = commentRepository
                .findByParentCommentIdAndApprovedTrueOrderByCreatedAtAsc(comment.getId());

        response.setReplies(
                replies.stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );

        return response;
    }

    private CommentResponse.UserInfo mapUserInfo(User user) {
        return CommentResponse.UserInfo.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}
