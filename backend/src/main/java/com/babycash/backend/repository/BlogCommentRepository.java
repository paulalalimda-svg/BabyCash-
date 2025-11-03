package com.babycash.backend.repository;

import com.babycash.backend.model.entity.BlogComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogCommentRepository extends JpaRepository<BlogComment, Long> {

    // Find all approved comments for a blog post (excluding replies)
    List<BlogComment> findByBlogPostIdAndApprovedTrueAndParentCommentIsNullOrderByCreatedAtDesc(Long blogPostId);

    // Find all approved replies for a comment
    List<BlogComment> findByParentCommentIdAndApprovedTrueOrderByCreatedAtAsc(Long parentCommentId);

    // Find all pending comments for admin review
    List<BlogComment> findByApprovedFalseOrderByCreatedAtDesc();

    // Find all comments by user
    List<BlogComment> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Count approved comments for a post
    @Query("SELECT COUNT(c) FROM BlogComment c WHERE c.blogPost.id = :postId AND c.approved = true")
    long countApprovedCommentsByPostId(@Param("postId") Long postId);

    // Count pending comments (for admin dashboard)
    long countByApprovedFalse();

    // Find all comments for a post (admin view)
    List<BlogComment> findByBlogPostIdOrderByCreatedAtDesc(Long blogPostId);

    // Delete all comments for a blog post
    void deleteByBlogPostId(Long blogPostId);
}
