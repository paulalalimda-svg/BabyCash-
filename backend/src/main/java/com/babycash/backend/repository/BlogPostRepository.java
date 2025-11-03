package com.babycash.backend.repository;

import com.babycash.backend.model.entity.BlogPost;
import com.babycash.backend.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BlogPost entity
 * Interface Segregation Principle: Only necessary query methods
 */
@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

    /**
     * Find published posts ordered by publication date (newest first)
     */
    Page<BlogPost> findByPublishedTrueOrderByPublishedAtDesc(Pageable pageable);

    /**
     * Find all posts by author
     */
    Page<BlogPost> findByAuthorOrderByCreatedAtDesc(User author, Pageable pageable);

    /**
     * Find featured published posts
     */
    List<BlogPost> findTop5ByPublishedTrueAndFeaturedTrueOrderByPublishedAtDesc();

    /**
     * Find post by slug (for SEO-friendly URLs)
     */
    Optional<BlogPost> findBySlug(String slug);

    /**
     * Check if slug exists (for validation)
     */
    boolean existsBySlug(String slug);

    /**
     * Search posts by title or content (published only)
     */
    @Query("SELECT bp FROM BlogPost bp WHERE bp.published = true AND " +
           "(LOWER(bp.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(bp.content) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(bp.excerpt) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<BlogPost> searchPublishedPosts(@Param("query") String query, Pageable pageable);

    /**
     * Find posts by tag
     */
    @Query("SELECT bp FROM BlogPost bp JOIN bp.tags t WHERE bp.published = true AND " +
           "LOWER(t) = LOWER(:tag) ORDER BY bp.publishedAt DESC")
    Page<BlogPost> findByTag(@Param("tag") String tag, Pageable pageable);

    /**
     * Count published posts
     */
    long countByPublishedTrue();

    /**
     * Count featured posts
     */
    long countByFeaturedTrue();

    /**
     * Count posts by author
     */
    long countByAuthor(User author);

    /**
     * Find most viewed posts
     */
    List<BlogPost> findTop10ByPublishedTrueOrderByViewCountDesc();
}
